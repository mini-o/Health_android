package com.example.health.sensor;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.MutableLiveData;
import com.example.health.ui.viewmodel.HealthViewModel;

public class SensorBackgroundService extends Service {
    private static final String CHANNEL_ID = "HealthMonitoringChannel";
    private static final int NOTIFICATION_ID = 101;
    private static final int PERMISSION_REQUEST_BACKGROUND = 1001;

    // 传感器组件
    private StepDetector stepDetector;
    private SpeedDetector speedDetector;
    private BreathingDetector breathingDetector;
    private BluetoothManager bluetoothManager;

    // ViewModel数据源
    private HealthViewModel healthViewModel;

    // 服务绑定器
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public SensorBackgroundService getService() {
            return SensorBackgroundService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 检查并请求后台权限（Android 10+）
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            if (checkBackgroundPermission()) {
                initializeSensors();
            } else {
                // 这里需要根据实际情况引导用户在设置中开启权限
                stopSelf(); // 权限不足时停止服务
            }
        } else {
            initializeSensors();
        }
        startForeground(NOTIFICATION_ID, createNotification());
    }

    private boolean checkBackgroundPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void initializeSensors() {
        Context context = getApplicationContext();

        // 初始化步数检测器（添加后台启动方法）
        stepDetector = new StepDetector();
        stepDetector.start(this); // 使用Activity上下文启动传感器

        // 初始化速度检测器（添加后台启动方法）
        speedDetector = new SpeedDetector();
        speedDetector.start(this);

        // 初始化呼吸检测器（添加后台启动方法）
        breathingDetector = new BreathingDetector(stepDetector, context);
        breathingDetector.startMonitoring((Activity) context); // 需要Activity上下文请求权限

        // 初始化蓝牙管理
        bluetoothManager = new BluetoothManager(context, new BluetoothListenerImpl());
        bluetoothManager.startDiscovery();
    }

    private Notification createNotification() {
        createNotificationChannel();

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("健康数据监测")
                .setContentText("正在后台收集传感器数据")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true) // 设置为前台服务
                .build();
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "健康监测服务",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (healthViewModel == null) {
            healthViewModel = HealthViewModel.getInstance(getApplication());
        }
        return START_STICKY;
    }

    // 蓝牙数据监听实现
    private class BluetoothListenerImpl implements BluetoothManager.BluetoothListener {
        @Override
        public void onHeartRateReceived(int heartRate) {
            healthViewModel.heartRate.postValue(heartRate);
        }

        @Override
        public void onBloodOxygenReceived(float spo2) {
            healthViewModel.bloodOxygen.postValue(String.format("%.1f%%", spo2));
        }

        // 其他蓝牙回调方法...
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseSensors();
        // 重启服务以保持后台运行（可选）
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, SensorBackgroundService.class));
        }
    }

    private void releaseSensors() {
        stepDetector.stop();
        speedDetector.stop();
        breathingDetector.stopMonitoring();
        bluetoothManager.cleanup();
    }

    public void bindViewModel(HealthViewModel viewModel) {
        this.healthViewModel = viewModel;
        setupSensorListeners();
    }

    private void setupSensorListeners() {
        // 步数监听
        stepDetector.getStepCount().observeForever(count -> {
            healthViewModel.steps.postValue(count);
        });

        // 速度监听
        speedDetector.getCurrentSpeed().observeForever(speed -> {
            healthViewModel.speed.postValue(speed);
        });

        // 呼吸监听
        breathingDetector.getBreathingData().observeForever(data -> {
            healthViewModel.breathingRate.postValue(data.getBreathingRate());
        });
    }

    // 添加权限请求方法（如需动态请求）
    public void requestBackgroundPermission(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    PERMISSION_REQUEST_BACKGROUND);
        }
    }
}
