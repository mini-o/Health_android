package com.example.health.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionUtils {
    private static Context appContext;

    // 定义所有需要的权限组
    public static final String[] LOCATION_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @RequiresApi(api = Build.VERSION_CODES.S)
    public static final String[] BLUETOOTH_PERMISSIONS = {
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    public static final String[] STORAGE_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final String[] SENSOR_PERMISSIONS = {
            Manifest.permission.BODY_SENSORS
    };

    public static final String[] AUDIO_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
    };

    public static final String[] CAMERA_PERMISSIONS = {
            Manifest.permission.CAMERA
    };

    public static final String[] SMS_PERMISSIONS = {
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS
    };

    // 初始化方法
    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    // 通用权限检查方法
    public static boolean checkPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(appContext, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkPermissions(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // 创建权限请求启动器
    public static ActivityResultLauncher<String[]> createPermissionLauncher(
            FragmentActivity activity,
            PermissionCallback callback) {

        return activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Map<String, Boolean> finalResult = new HashMap<>();
                    for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                        finalResult.put(entry.getKey(),
                                entry.getValue() && isPermissionGranted(entry.getKey()));
                    }
                    callback.onPermissionResult(finalResult);
                });
    }

    // 请求权限组
    public static void requestPermissions(
            FragmentActivity activity,
            ActivityResultLauncher<String[]> launcher,
            String[] permissions,
            PermissionCallback callback) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (!isPermissionGranted(permission)) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            launcher.launch(permissionsToRequest.toArray(new String[0]));
        } else {
            callback.onPermissionResult(createGrantedResult(permissions));
        }
    }

    // 检查单个权限
    private static boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(appContext, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    // 创建已授权结果
    private static Map<String, Boolean> createGrantedResult(String[] permissions) {
        Map<String, Boolean> result = new HashMap<>();
        for (String permission : permissions) {
            result.put(permission, true);
        }
        return result;
    }

    // 检查并请求特定功能权限
    public static void checkAndRequestFeaturePermissions(
            FragmentActivity activity,
            ActivityResultLauncher<String[]> launcher,
            FeatureType feature,
            PermissionCallback callback) { // Added callback parameter

        String[] permissions = getPermissionsForFeature(feature);
        if (checkPermissions(permissions)) {
            // Directly invoke the callback since all permissions are granted
            callback.onPermissionResult(createGrantedResult(permissions));
        } else {
            // Pass the callback to requestPermissions
            requestPermissions(activity, launcher, permissions, callback);
        }
    }

    // 获取功能对应的权限组
    private static String[] getPermissionsForFeature(FeatureType feature) {
        switch (feature) {
            case LOCATION:
                return LOCATION_PERMISSIONS;
            case BLUETOOTH:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    return BLUETOOTH_PERMISSIONS;
                } else {
                    return new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
                }
            case STORAGE:
                return STORAGE_PERMISSIONS;
            case SENSORS:
                return SENSOR_PERMISSIONS;
            case AUDIO:
                return AUDIO_PERMISSIONS;
            case CAMERA:
                return CAMERA_PERMISSIONS;
            case SMS:
                return SMS_PERMISSIONS;
            default:
                return new String[]{};
        }
    }

    public static void requestFeaturePermissions(
            Activity activity,
            PermissionCallback callback,
            FeatureType featureType,
            int requestCode) {

        // 参数校验
        if (activity == null || callback == null) {
            throw new IllegalArgumentException("Activity and callback cannot be null");
        }

        // 获取对应权限组
        String[] permissions = getPermissionsForFeature(featureType);

        // 创建启动器
        ActivityResultLauncher<String[]> launcher = ((FragmentActivity) activity)
                .registerForActivityResult(
                        new ActivityResultContracts.RequestMultiplePermissions(),
                        result -> {
                            Map<String, Boolean> finalResult = new HashMap<>();
                            for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                                finalResult.put(entry.getKey(),
                                        entry.getValue() && isPermissionGranted(entry.getKey()));
                            }
                            callback.onPermissionResult(finalResult);
                        });

        // 检查并请求权限
        if (checkPermissions(permissions)) {
            callback.onPermissionResult(createGrantedResult(permissions));
        } else {
            launcher.launch(permissions);
        }
    }

    // 权限回调接口
    public interface PermissionCallback {
        void onPermissionResult(Map<String, Boolean> results);

        void onPermissionResult(boolean granted);
    }

    // 功能类型枚举
    public enum FeatureType {
        LOCATION,
        BLUETOOTH,
        STORAGE,
        SENSORS,
        AUDIO,
        CAMERA,
        SMS
    }

    // 跳转到应用设置页面
    public static void openAppSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }

    // 检查是否所有权限都已授予
    public static boolean allPermissionsGranted(Object results) {
        for (Boolean granted : results.values()) {
            if (!granted) return false;
        }
        return true;
    }

    // 获取未授予的权限列表
    public static List<String> getDeniedPermissions(Map<String, Boolean> results) {
        List<String> denied = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : results.entrySet()) {
            if (!entry.getValue()) {
                denied.add(entry.getKey());
            }
        }
        return denied;
    }
    public static final int REQUEST_CODE = 100;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACTIVITY_RECOGNITION
    };

    public static boolean checkAllPermissions(Context context) {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkAudioPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    // 相机权限请求
    public static void requestCameraPermission(Activity activity, PermissionCallback callback, int requestCode) {
        checkAndRequestFeaturePermissions((FragmentActivity) activity,
                createPermissionLauncher((FragmentActivity) activity, callback),
                FeatureType.CAMERA,
                callback);
    }

    public static boolean isAllGranted(Map<String, Boolean> results) {
        for (Boolean value : results.values()) {
            if (!Boolean.TRUE.equals(value)) return false;
        }
        return true;
    }

    // 存储权限请求
    public static void requestStoragePermission(Activity activity, PermissionCallback callback, int requestCode) {
        String[] permissions = {
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        handlePermissionRequest(activity, permissions, callback, requestCode);
    }

    private static void handlePermissionRequest(Activity activity, String[] permissions,
                                                PermissionCallback callback, int requestCode) {
        // 修改实现逻辑以匹配新的接口
        requestPermissions((FragmentActivity) activity,
                createPermissionLauncher((FragmentActivity) activity, callback),
                permissions,
                callback);
    }

    public static boolean checkActivityPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestAllPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, REQUIRED_PERMISSIONS, REQUEST_CODE);
    }

    public static void requestAudioPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE);
    }

    public static boolean checkStoragePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public static void requestLocationPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    public static void requestActivityPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, REQUEST_CODE);
    }

    public static void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                  int[] grantResults, Runnable onGranted, Runnable onDenied) {
        if (requestCode == REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted && onGranted != null) {
                onGranted.run();
            } else if (!allGranted && onDenied != null) {
                onDenied.run();
            }
        }
    }
}