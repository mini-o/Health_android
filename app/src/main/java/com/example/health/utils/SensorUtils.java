// SensorUtils.java
package com.example.health.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import java.util.List;

public class SensorUtils {
    public static boolean hasAccelerometer(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        return sensorManager != null && sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null;
    }

    public static boolean hasGyroscope(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        return sensorManager != null && sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null;
    }

    public static boolean hasStepCounter(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        return sensorManager != null && sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null;
    }

    public static boolean hasHeartRateSensor(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager == null) return false;

        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_HEART_RATE);
        return sensors != null && !sensors.isEmpty();
    }

    public static boolean hasAllRequiredSensors(Context context) {
        return hasAccelerometer(context) && hasGyroscope(context);
    }

    public static float calculateCalories(float weight, float speed, long duration) {
        // METs公式: 3.5 * speed (km/h) / 3.6
        float mets = 3.5f * speed / 3.6f;
        // 卡路里 = METs * 体重(kg) * 时间(小时)
        return mets * weight * (duration / 3600000f);
    }

    public static float calculateDistance(int steps, float strideLength) {
        return steps * strideLength / 1000f; // 转换为公里
    }
}