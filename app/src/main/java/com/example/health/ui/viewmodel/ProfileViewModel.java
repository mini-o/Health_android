package com.example.health.ui.viewmodel;

import static com.example.health.utils.PermissionUtils.checkAudioPermission;
import static com.example.health.utils.PermissionUtils.checkLocationPermission;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Toast;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.health.R;
import com.example.health.database.HealthDatabase;
import com.example.health.database.dao.UserProfileDao;
import com.example.health.model.enums.Gender;
import com.example.health.model.pojo.HealthBluetoothDevice;
import com.example.health.model.pojo.HealthReport;
import com.example.health.model.pojo.UserProfile;
import com.example.health.model.repository.BluetoothRepository;
import com.example.health.model.repository.ProfileRepository;
import com.example.health.utils.PermissionUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileViewModel extends AndroidViewModel {
    // 添加权限请求码
    private static final int REQUEST_CAMERA = 1001;
    private static final int REQUEST_GALLERY = 1002;
    private static final int CUSTOM_LOCATION_REQUEST_CODE = 1003;
    public static final int REQUEST_STORAGE = 1004;
    public static final int REQUEST_MICROPHONE = 1005;
    public static final int REQUEST_LOCATION = 1006;

    private final ExecutorService executor;
    private final MutableLiveData<Bitmap> avatarLiveData = new MutableLiveData<>();
    private Uri cameraImageUri;
    // Repositories
    private final ProfileRepository profileRepo;
    private final BluetoothRepository btRepo;

    // LiveData
    private final UserProfileDao userProfileDao;
    private final LiveData<UserProfile> currentProfile;
    private final LiveData<UserProfile> userProfile;
    private final LiveData<HealthBluetoothDevice> connectedDevice;
    private final LiveData<List<HealthBluetoothDevice>> savedDevices;
    private final LiveData<List<HealthBluetoothDevice>> discoveredDevices;

    private final MutableLiveData<HealthReport> healthReport = new MutableLiveData<>();

    public LiveData<HealthReport> getHealthReport() {
        return healthReport;
    }
    // Formatted Fields
    public final ObservableField<String> formattedBirthDate = new ObservableField<>("");

    // Permissions
    public final ObservableBoolean gpsPermissionGranted = new ObservableBoolean(false);
    public final ObservableBoolean accelerometerPermissionGranted = new ObservableBoolean(false);
    public final ObservableBoolean microphonePermissionGranted = new ObservableBoolean(false);
    public final ObservableBoolean storagePermissionGranted = new ObservableBoolean(false);

    private final MutableLiveData<Boolean> showGenderDialog = new MutableLiveData<>();
    private final MutableLiveData<Boolean> showDatePicker = new MutableLiveData<>();
    public final ObservableField<String> heightInput = new ObservableField<>();
    public final ObservableField<String> weightInput = new ObservableField<>();

    public static class PermissionItem {
        public int iconRes;
        public final String name;
        public final String permission;
        public final int requestCode;
        public boolean granted;

        public PermissionItem( int iconRes, String name, String permission, int requestCode,
                              boolean granted) {
            this.iconRes = iconRes;
            this.name = name;
            this.permission = permission;
            this.requestCode = requestCode;
            this.granted = granted;
        }
    }

    private final MutableLiveData<List<PermissionItem>> permissionItems = new MutableLiveData<>();

    private void loadPermissions() {
        List<PermissionItem> items = Arrays.asList(
                new PermissionItem(R.drawable.ic_location, "位置权限",
                        Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_LOCATION,
                        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)),
                new PermissionItem(R.drawable.ic_storage, "存储权限",
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_STORAGE,
                        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)),
                new PermissionItem(R.drawable.ic_mic, "麦克风权限",
                        Manifest.permission.RECORD_AUDIO, REQUEST_MICROPHONE,
                        checkPermission(Manifest.permission.RECORD_AUDIO))
        );
        permissionItems.setValue(items);
    }

    public void revokePermission(PermissionItem item) {
        // 实际需要跳转到系统设置
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.parse("package:" + getApplication().getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(intent);
    }

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(getApplication(), permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public LiveData<List<PermissionItem>> getPermissionItems() {
        return permissionItems;
    }

    public void updatePermissionStatus(int requestCode, boolean granted) {
        List<PermissionItem> items = permissionItems.getValue();
        if (items == null) return;

        for (PermissionItem item : items) {
            if (item.requestCode == requestCode) {
                item.granted = granted;
                break;
            }
        }
        permissionItems.postValue(items);
    }

    public void showPermissionRationale(int requestCode) {
        // 显示权限说明对话框逻辑
    }

    public void showGenderDialog() {
        showGenderDialog.setValue(true);
    }

    public LiveData<Boolean> getShowGenderDialog() {
        return showGenderDialog;
    }

    public void showDatePicker(Context context) {
        showDatePicker.setValue(true);
    }

    public LiveData<Boolean> getShowDatePicker() {
        return showDatePicker;
    }

    public void onDialogDismissed() {
        showGenderDialog.setValue(false);
        showDatePicker.setValue(false);
    }

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        HealthDatabase db = HealthDatabase.getDatabase(application);
        profileRepo = new ProfileRepository(application);
        btRepo = new BluetoothRepository(application);
        this.executor = Executors.newSingleThreadExecutor();
        this.userProfileDao = db.userProfileDao();
        this.currentProfile = profileRepo.getCurrentProfile();

        userProfile = profileRepo.getCurrentProfile();
        connectedDevice = btRepo.getConnectedDevice();
        savedDevices = btRepo.getSavedDevices();
        discoveredDevices = btRepo.getDiscoveredDevices();

        initPermissions();
    }

    // 设备列表获取方式
    public LiveData<List<HealthBluetoothDevice>> getSavedDevices() {
        return btRepo.getSavedDevices();
    }

    // 退出登录逻辑
    public void logout() {
        profileRepo.deleteAllHistory();
    }

    // 定义导航事件类型
    public enum NavigationEvent {
        PROFILE, HEALTH, EXERCISE, EDIT_PROFILE, EDIT_PRIVACY, HEALTH_REPORT, LOGIN
    }

    // 导航事件的 LiveData
    private final MutableLiveData<NavigationEvent> navigationEvent = new MutableLiveData<>();

    public void navigateToProfile() {
        navigationEvent.setValue(NavigationEvent.PROFILE);
    }

    public void navigateToHealth() {
        navigationEvent.setValue(NavigationEvent.HEALTH);
    }

    public void navigateToExercise() {
        navigationEvent.setValue(NavigationEvent.EXERCISE);
    }

    public void navigateToLogin() {
        navigationEvent.setValue(NavigationEvent.LOGIN);
        logout();
    }

    public void navigateToPrivacySettings() {
        navigationEvent.setValue(NavigationEvent.EDIT_PRIVACY);
        loadPermissions();
    }

    public void navigateToHealthReport() {
        navigationEvent.setValue(NavigationEvent.HEALTH_REPORT);
        loadHealthReport();
    }

    // 在ProfileViewModel类中添加以下方法

    private void loadHealthReport() {
        UserProfile profile = userProfile.getValue();
        if (profile != null) {
            generateHealthReport(profile);
        }
    }

    private float calculateBMI(float heightCm, float weightKg) {
        float heightM = heightCm / 100;
        return weightKg / (heightM * heightM);
    }

    private String getBMILevel(float bmi) {
        if (bmi < 18.5) return "体重过轻";
        if (bmi < 24) return "正常范围";
        if (bmi < 28) return "超重";
        return "肥胖";
    }

    public void generateHealthReport(UserProfile profile) {
        executor.execute(() -> {
            HealthReport report = new HealthReport();

            // 基础指标计算
            float bmi = calculateBMI(profile.getHeight(), profile.getWeight());
            report.setBmi(bmi);
            report.setBmiScore(mapBMIToScore(bmi));
            report.setBmiLevel(getBMILevel(bmi));

            // 各维度评分计算
            report.setCardioScore(calculateCardioScore(profile));
            report.setExerciseScore(calculateExerciseScore(profile));
            report.setSleepScore(calculateSleepScore(profile));
            report.setStressScore(calculateStressScore(profile));

            // 综合评分计算（加权平均）
            int totalScore = calculateTotalScore(report);
            report.setOverallScore(totalScore);

            // 生成个性化建议
            report.setSuggestions(generateHealthSuggestions(report));

            // 更新LiveData
            healthReport.postValue(report);
        });
    }

    private int calculateTotalScore(HealthReport report) {
        return (report.getBmiScore() +
                report.getCardioScore() +
                report.getExerciseScore() +
                report.getSleepScore() +
                report.getStressScore()) / 5;
    }

    private int mapBMIToScore(float bmi) {
        if (bmi < 18.5) return 60;  // 过轻
        if (bmi < 24) return 90;     // 正常
        if (bmi < 28) return 70;     // 过重
        return 50;                   // 肥胖
    }

    private String generateHealthSuggestions(HealthReport report) {
        StringBuilder sb = new StringBuilder();

        // BMI建议
        if (report.getBmiScore() < 70) {
            sb.append("• 体重管理建议：");
            if (report.getBmi() < 18.5) {
                sb.append("检测到体重过轻，建议增加营养摄入，每日增加300-500大卡热量\n");
            } else {
                sb.append("检测到体重超标，建议控制每日热量摄入在1800大卡以下，每周至少3次有氧运动\n");
            }
        }

        // 心肺功能建议
        if (report.getCardioScore() < 60) {
            sb.append("• 心肺健康建议：静息心率")
                    .append(report.getHeartRate())
                    .append("次/分钟，建议进行心肺功能检查，可尝试每周2-3次慢跑训练\n");
        }

        // 运动建议
        if (report.getExerciseScore() < 60) {
            sb.append("• 运动建议：当前每周运动量不足，建议增加至每周150分钟中等强度运动\n");
        }

        // 睡眠建议
        if (report.getSleepScore() < 60) {
            sb.append("• 睡眠建议：当前平均睡眠")
                    .append(String.format(Locale.getDefault(), "%.1f", report.getSleepHours()))
                    .append("小时，建议保持7-9小时优质睡眠\n");
        }

        // 压力建议（新增逻辑）
        if (report.getStressScore() < 60) {
            sb.append("• 压力管理：检测到压力水平较高，建议每天进行15分钟正念冥想\n");
        }

        return sb.length() > 0 ? sb.toString() : "您的健康状况良好，请继续保持！";
    }

    // 各维度评分计算方法（需根据实际业务数据实现）
    private int calculateCardioScore(UserProfile profile) {
        // 示例实现：根据心率变异性计算
        // 实际应接入健康设备数据
//        int restingHeartRate = profile.getRestingHeartRate(); // 假设有此字段
//        if (restingHeartRate < 60) return 90;
//        if (restingHeartRate <= 80) return 75;
//        if (restingHeartRate <= 100) return 60;
        return 50;
    }

    private int calculateExerciseScore(UserProfile profile) {
        // 根据每周运动时长计算
//        int weeklyMinutes = profile.getExerciseMinutes(); // 假设有此字段
//        if (weeklyMinutes >= 150) return 90;
//        if (weeklyMinutes >= 100) return 75;
//        if (weeklyMinutes >= 50) return 60;
        return 40;
    }

    private int calculateSleepScore(UserProfile profile) {
        // 根据平均睡眠时长计算
//        float avgSleep = profile.getSleepHours(); // 假设有此字段
//        if (avgSleep >= 7 && avgSleep <= 9) return 90;
//        if (avgSleep >= 6) return 70;
        return 50;
    }

    private int calculateStressScore(UserProfile profile) {
        // 根据压力检测数据计算
        // 示例：使用心率变异性（HRV）分析
//        int hrv = profile.getHrvValue(); // 假设有此字段
//        if (hrv > 60) return 90;
//        if (hrv > 40) return 70;
        return 50;
    }

    public void navigateToEditProfile() {
        navigationEvent.setValue(NavigationEvent.EDIT_PROFILE);
    }

    // 获取导航事件 LiveData
    public LiveData<NavigationEvent> getNavigationEvent() {
        return navigationEvent;
    }

    // 重置导航事件
    public void onNavigationComplete() {
        navigationEvent.setValue(null);
    }

    private void initPermissions() {
        Context context = getApplication();
        gpsPermissionGranted.set(checkLocationPermission(context));
        accelerometerPermissionGranted.set(true); // 传感器权限通常不需要运行时请求
        microphonePermissionGranted.set(checkAudioPermission(context));
        storagePermissionGranted.set(PermissionUtils.checkStoragePermission(context));
    }

    // region Profile Operations
    public void updateNickname(String nickname) {
        profileRepo.updateNickname(nickname);
    }

    public void updateGender(Gender gender) {
        profileRepo.updateGender(gender);
    }

    public void updateBirthDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        profileRepo.updateBirthDate(calendar.getTime());
        formattedBirthDate.set(String.format("%d年%d月%d日", year, month+1, day));
    }



    public void updateHeight(float height) {
        profileRepo.updateHeight(height);
    }

    public void updateWeight(float weight) {
        profileRepo.updateWeight(weight);
    }

    // 修改changeAvatar方法
    public void changeAvatar(Context context) {
        Activity activity = (Activity) context;
        new MaterialAlertDialogBuilder(context)
                .setTitle("更换头像")
                .setItems(new String[]{"拍照", "从相册选择"}, (dialog, which) -> {
                    if (which == 0) {
                        // 正确调用三个参数的版本
                        PermissionUtils.requestCameraPermission(
                                activity,
                                new PermissionUtils.PermissionCallback() {
                                    @Override
                                    public void onPermissionResult(Map<String, Boolean> results) {
                                        boolean granted = PermissionUtils.allPermissionsGranted(results);
                                        if (granted) launchCamera();
                                    }

                                    @Override
                                    public void onPermissionResult(boolean granted) {
                                        // 保持空实现避免抽象方法错误
                                    }
                                },
                                PermissionUtils.REQUEST_CODE // 使用工具类定义的常量
                        );
                    } else {
                        // 正确调用三个参数的版本
                        PermissionUtils.requestStoragePermission(
                                activity,
                                new PermissionUtils.PermissionCallback() {
                                    @Override
                                    public void onPermissionResult(Map<String, Boolean> results) {
                                        boolean granted = PermissionUtils.allPermissionsGranted(results);
                                        if (granted) pickFromGallery();
                                    }

                                    @Override
                                    public void onPermissionResult(boolean granted) {
                                        // 保持空实现避免抽象方法错误
                                    }
                                },
                                PermissionUtils.REQUEST_CODE
                        );
                    }
                })
                .show();
    }

    // 修改GPS权限处理方法
    public void onGpsPermissionChanged(boolean granted) {
        Activity activity = (Activity) getApplication().getApplicationContext();
        if (granted) {
            // 正确调用三个参数的版本
            PermissionUtils.requestFeaturePermissions(
                    (Activity) getApplication().getApplicationContext(),
                    new PermissionUtils.PermissionCallback() {
                        @Override
                        public void onPermissionResult(Map<String, Boolean> results) {
                            boolean granted = PermissionUtils.allPermissionsGranted(results);
                            handleLocationPermissionResult(granted);
                        }

                        @Override
                        public void onPermissionResult(boolean granted) {
                            // 兼容旧回调（可选）
                        }
                    },
                    PermissionUtils.FeatureType.LOCATION,
                    CUSTOM_LOCATION_REQUEST_CODE
            );
        } else {
            gpsPermissionGranted.set(false);
            showPermissionWarning("位置权限");
        }
    }

    private void handleLocationPermissionResult(boolean granted) {
        gpsPermissionGranted.set(granted);
        if (!granted) {
            showPermissionWarning("定位");
        }
    }
    // endregion

    // region Bluetooth Operations
    public void startDiscovery() {
        btRepo.startDiscovery();
    }

    public void connectDevice(HealthBluetoothDevice device) {
        btRepo.connectDevice(device);
    }

    public void disconnectDevice() {
        btRepo.disconnectDevice();
    }

    public void deleteDevice(HealthBluetoothDevice device) {
        btRepo.deleteDevice(device);
    }

    // 初始化文件提供者Authority（需与manifest配置一致）
    private static final String FILE_PROVIDER_AUTHORITY = "com.example.health.fileprovider";

    public LiveData<Bitmap> getAvatarLiveData() {
        return avatarLiveData;
    }

    // 修改权限检查方法
    private boolean checkCameraPermission(Context context) {
        return PermissionUtils.checkPermissions(context, PermissionUtils.CAMERA_PERMISSIONS);
    }

    private boolean checkStoragePermission(Context context) {
        return PermissionUtils.checkPermissions(context, PermissionUtils.STORAGE_PERMISSIONS);
    }

    private void launchCamera() {
        Context context = getApplication();
        if (!checkCameraPermission(context)) {
            PermissionUtils.requestCameraPermission((Activity) context,
                    new PermissionUtils.PermissionCallback() {
                        @Override
                        public void onPermissionResult(Map<String, Boolean> results) {
                            boolean granted = PermissionUtils.allPermissionsGranted(results);
                            if (granted) {
                                startCamera((Activity) context);
                            } else {
                                showPermissionWarning("相机");
                            }
                        }

                        @Override
                        public void onPermissionResult(boolean granted) {
                            // 兼容旧回调
                        }
                    },
                    PermissionUtils.REQUEST_CODE
            );
            return;
        }

        startCamera((Activity) context);
    }

    private void startCamera(Activity activity) {
        // 2. 创建图片文件
        File photoFile = createImageFile(activity);
        if (photoFile == null) {
            Toast.makeText(activity, "无法创建图片文件", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. 创建URI（适配Android 7.0+）
        cameraImageUri = FileProvider.getUriForFile(
                activity,
                FILE_PROVIDER_AUTHORITY,
                photoFile
        );

        // 4. 启动相机Intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);

        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(takePictureIntent, REQUEST_CAMERA);
        } else {
            Toast.makeText(activity, "未找到相机应用", Toast.LENGTH_SHORT).show();
        }
    }

    private void pickFromGallery() {
        Context context = getApplication();

        // 1. 检查存储权限
        if (!PermissionUtils.checkPermissions(context, PermissionUtils.STORAGE_PERMISSIONS)) {
            PermissionUtils.requestStoragePermission(
                    (Activity) context,
                    new PermissionUtils.PermissionCallback() {
                        @Override
                        public void onPermissionResult(Map<String, Boolean> results) {
                            if (PermissionUtils.allPermissionsGranted(results)) {
                                startGalleryPicker((Activity) context);
                            } else {
                                showPermissionWarning("存储");
                            }
                        }

                        @Override
                        public void onPermissionResult(boolean granted) {
                            // 兼容旧接口
                        }
                    },
                    PermissionUtils.REQUEST_CODE
            );
            return;
        }

        startGalleryPicker((Activity) context);
    }

    private void startGalleryPicker(Activity activity) {
        // 2. 创建图库选择Intent
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg", "image/png"});

        try {
            activity.startActivityForResult(
                    Intent.createChooser(intent, "选择图片"),
                    REQUEST_GALLERY
            );
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "未找到图库应用", Toast.LENGTH_SHORT).show();
        }
    }

    // 在关联的Activity中调用此方法
    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            case REQUEST_CAMERA:
                handleCameraResult();
                break;
            case REQUEST_GALLERY:
                handleGalleryResult(data);
                break;
        }
    }

    public void handleCameraResult() {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(
                    getApplication().getContentResolver().openInputStream(cameraImageUri)
            );
            Bitmap compressed = compressBitmap(bitmap);
            avatarLiveData.setValue(compressed);
            saveToDatabase(compressed);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplication(), "图片加载失败", Toast.LENGTH_SHORT).show();
        }
    }

    public void handleGalleryResult(Intent data) {
        try {
            Uri selectedImage = data.getData();
            Bitmap bitmap = BitmapFactory.decodeStream(
                    getApplication().getContentResolver().openInputStream(selectedImage)
            );
            Bitmap compressed = compressBitmap(bitmap);
            avatarLiveData.setValue(compressed);
            saveToDatabase(compressed);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplication(), "图片选择失败", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile(Context context) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "JPEG_" + timeStamp + "_";

        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            return File.createTempFile(fileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap compressBitmap(Bitmap src) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] byteArray = stream.toByteArray();
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    private void saveToDatabase(Bitmap avatar) {
        executor.execute(() -> {
            // 保存到数据库的逻辑
            UserProfile profile = currentProfile.getValue();
            if (profile != null) {
                userProfileDao.updateAvatar(profile.getId(), avatar);
            }
        });
    }

    private void showPermissionWarning(String permission) {
        new MaterialAlertDialogBuilder(getApplication())
                .setTitle("权限不足")
                .setMessage(permission + "权限被拒绝，功能无法使用")
                .setPositiveButton("设置", (d, w) -> openAppSettings())
                .setNegativeButton("取消", null)
                .show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getApplication().getPackageName(), null);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(intent);
    }

    // Getters
    public LiveData<UserProfile> getUserProfile() {
        return userProfile;
    }

    public LiveData<HealthBluetoothDevice> getConnectedDevice() {
        return connectedDevice;
    }

    public LiveData<List<HealthBluetoothDevice>> getDiscoveredDevices() {
        return discoveredDevices;
    }

}