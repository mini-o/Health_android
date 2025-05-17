package com.example.health.sensor;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.health.utils.PermissionUtils;

// 速度检测器类，实现了 SensorEventListener 和 LocationListener 接口
public class SpeedDetector implements SensorEventListener, LocationListener {
    // 用于低通滤波的平滑因子
    private static final float ALPHA = 0.1f;
    // GPS 精度的最小可接受值，单位为米
    private static final int MIN_GPS_ACCURACY = 10;

    // 当前速度的可变 LiveData，初始值为 0f
    private final MutableLiveData<Float> currentSpeed = new MutableLiveData<>(0f);
    // 上一次的位置坐标，长度为 2，分别存储纬度和经度
    private float[] lastPosition = new float[2];
    // 上一次更新的时间戳，单位为纳秒
    private long lastUpdateTime = 0;
    // 加速度传感器数据的缓冲区
    private float[] accelBuffer = new float[3];
    // 陀螺仪传感器数据的缓冲区
    private float[] gyroBuffer = new float[3];

    private SensorManager sensorManager;
    private LocationManager locationManager;
    private Context context;

    // 卡尔曼滤波器内部类
    private static class KalmanFilter {
        // 过程噪声协方差
        private float q;
        // 测量噪声协方差
        private float r;
        // 状态估计值
        private float x;
        // 估计误差协方差
        private float p;
        // 卡尔曼增益
        private float k;

        // 构造函数，初始化滤波器参数
        public KalmanFilter(float q, float r, float initialValue) {
            this.q = q;
            this.r = r;
            this.x = initialValue;
            this.p = 1.0f;
        }

        // 更新滤波器，返回估计值
        public float update(float measurement) {
            p = p + q;
            k = p / (p + r);
            x = x + k * (measurement - x);
            p = (1 - k) * p;
            return x;
        }
    }

    // 用于速度估计的卡尔曼滤波器实例
    private final KalmanFilter speedFilter = new KalmanFilter(0.1f, 1.0f, 0.0f);
    // 用于加速度数据滤波的卡尔曼滤波器数组
    private final KalmanFilter[] accelFilters = {
            new KalmanFilter(0.1f, 1.0f, 0.0f),
            new KalmanFilter(0.1f, 1.0f, 0.0f),
            new KalmanFilter(0.1f, 1.0f, 0.0f)
    };
    // 用于陀螺仪数据滤波的卡尔曼滤波器数组
    private final KalmanFilter[] gyroFilters = {
            new KalmanFilter(0.1f, 1.0f, 0.0f),
            new KalmanFilter(0.1f, 1.0f, 0.0f),
            new KalmanFilter(0.1f, 1.0f, 0.0f)
    };

    // 获取当前速度的 LiveData
    public LiveData<Float> getCurrentSpeed() {
        return currentSpeed;
    }

    // 当传感器数据变化时的回调方法
    @Override
    public void onSensorChanged(SensorEvent event) {
        // 根据传感器类型进行不同的处理
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                // 滤波后的加速度数据数组
                float[] filteredAccel = new float[3];
                for (int i = 0; i < 3; i++) {
                    // 使用卡尔曼滤波器对加速度数据进行滤波
                    filteredAccel[i] = accelFilters[i].update(event.values[i]);
                }
                // 将滤波后的加速度数据复制到缓冲区
                System.arraycopy(filteredAccel, 0, accelBuffer, 0, 3);
                break;
            case Sensor.TYPE_GYROSCOPE:
                // 滤波后的陀螺仪数据数组
                float[] filteredGyro = new float[3];
                for (int i = 0; i < 3; i++) {
                    // 使用卡尔曼滤波器对陀螺仪数据进行滤波
                    filteredGyro[i] = gyroFilters[i].update(event.values[i]);
                }
                // 将滤波后的陀螺仪数据复制到缓冲区
                System.arraycopy(filteredGyro, 0, gyroBuffer, 0, 3);
                break;
        }
    }

    // 当位置发生变化时的回调方法
    @Override
    public void onLocationChanged(Location location) {
        // 如果 GPS 精度大于最小可接受精度，则不进行处理
        if (location.getAccuracy() > MIN_GPS_ACCURACY) return;

        // 当前位置坐标数组，分别存储纬度和经度
        float[] newPosition = {(float) location.getLatitude(), (float) location.getLongitude()};

        // 如果上一次更新时间不为 0，说明不是第一次获取位置数据
        if (lastUpdateTime != 0) {
            // 计算当前位置与上一次位置在纬度和经度上的差值
            float dx = newPosition[0] - lastPosition[0];
            float dy = newPosition[1] - lastPosition[1];
            // 根据经纬度差值计算实际距离，乘以 111319.5 将经纬度差值转换为距离（单位：米）
            float distance = (float) Math.sqrt(dx * dx + dy * dy) * 111319.5f;
            // 计算时间间隔，单位为秒（将纳秒转换为秒）
            float dt = (location.getTime() - lastUpdateTime) / 1e9f;

            // 如果时间间隔大于 0，说明有有效的时间差
            if (dt > 0) {
                // 计算原始速度（距离除以时间）
                float rawSpeed = distance / dt;
                // 将 GPS 速度与加速度数据融合
                float fusedSpeed = fuseWithAccelerometer(rawSpeed, dt);
                // 更新当前速度的 LiveData
                currentSpeed.postValue(speedFilter.update(fusedSpeed));
            }
        }

        // 将当前位置复制到上一次位置数组
        System.arraycopy(newPosition, 0, lastPosition, 0, 2);
        // 更新上一次更新时间为当前位置的时间
        lastUpdateTime = location.getTime();
    }

    // 将 GPS 速度与加速度数据融合的方法
    private float fuseWithAccelerometer(float gpsSpeed, float dt) {
        // 应用方向补偿，返回调整后的加速度数据
        float[] adjusted = applyOrientationCompensation(accelBuffer, gyroBuffer);
        // 计算调整后加速度的大小
        float accelMagnitude = (float) Math.sqrt(
                adjusted[0] * adjusted[0] + adjusted[1] * adjusted[1] + adjusted[2] * adjusted[2]);
        // 使用加权平均的方式融合 GPS 速度和加速度数据得到融合后的速度
        return 0.7f * gpsSpeed + 0.3f * (gpsSpeed + accelMagnitude * dt);
    }

    // 应用方向补偿的方法，根据陀螺仪数据调整加速度数据
    private float[] applyOrientationCompensation(float[] accel, float[] gyro) {
        // 陀螺仪数据中的 roll（横滚角）乘以 0.1，用于调整加速度数据
        float roll = gyro[0] * 0.1f;
        // 陀螺仪数据中的 pitch（俯仰角）乘以 0.1，用于调整加速度数据
        float pitch = gyro[1] * 0.1f;
        // 调整后的加速度数据数组
        float[] adjusted = new float[3];

        adjusted[0] = accel[0];
        // 根据 roll 调整加速度在 y 和 z 轴上的值
        adjusted[1] = (float) (accel[1] * Math.cos(roll) - accel[2] * Math.sin(roll));
        adjusted[2] = (float) (accel[1] * Math.sin(roll) + accel[2] * Math.cos(roll));

        // 临时存储调整后的 y 轴加速度值
        float tempY = adjusted[1];
        // 根据 pitch 进一步调整加速度在 x 和 z 轴上的值
        adjusted[0] = (float) (adjusted[0] * Math.cos(pitch) + adjusted[2] * Math.sin(pitch));
        adjusted[1] = tempY;
        adjusted[2] = (float) (-adjusted[0] * Math.sin(pitch) + adjusted[2] * Math.cos(pitch));

        return adjusted;
    }

    // 启动传感器和位置监听的方法
    public void start(Activity activity) {
        this.context = activity.getApplicationContext();

        if (!PermissionUtils.checkLocationPermission(activity)) {
            return;
        }

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }

        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    1,
                    this,
                    Looper.getMainLooper()
            );
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        if (locationManager != null) {
            try {
                locationManager.removeUpdates(this);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    // 当传感器精度发生变化时的回调方法，目前为空实现
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    // 当位置提供器状态发生变化时的回调方法，目前为空实现
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    // 当位置提供器启用时的回调方法，目前为空实现
    @Override
    public void onProviderEnabled(String provider) {}

    // 当位置提供器禁用时的回调方法，目前为空实现
    @Override
    public void onProviderDisabled(String provider) {}
}
