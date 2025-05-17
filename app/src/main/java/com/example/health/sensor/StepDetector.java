package com.example.health.sensor;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.health.utils.PermissionUtils;

import java.util.LinkedList;

public class StepDetector implements SensorEventListener {
    // 步数检测阈值，用于判断是否是一步
    private static final float STEP_THRESHOLD = 1.5f;
    // 滑动窗口大小，用于计算动态阈值
    private static final int WINDOW_SIZE = 50;
    // 运动时间阈值，用于判断是否在运动
    private static final long MOVEMENT_THRESHOLD_MS = 2000;

    // 步数的可变 LiveData，初始值为 0
    private final MutableLiveData<Integer> stepCount = new MutableLiveData<>(0);
    // 是否在运动的可变 LiveData，初始值为 false
    private final MutableLiveData<Boolean> isMoving = new MutableLiveData<>(false);

    // 重力加速度数据数组
    private float[] gravity = new float[3];
    // 线性加速度数据数组
    private float[] linearAccel = new float[3];
    // 用于存储加速度数据的滑动窗口
    private LinkedList<Float> accelWindow = new LinkedList<>();
    // 记录上一次步数增加的时间戳
    private long lastStepTime = 0;
    // 动态阈值，根据滑动窗口内的数据动态计算
    private float dynamicThreshold = STEP_THRESHOLD;
    // 记录上一次检测到运动的时间戳
    private long lastMovementTime = 0;

    private SensorManager sensorManager;
    private Context context;


    // 获取步数的 LiveData
    public LiveData<Integer> getStepCount() {
        return stepCount;
    }

    // 获取是否在运动的 LiveData
    public LiveData<Boolean> getIsMoving() {
        return isMoving;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 如果不是加速度传感器的数据，直接返回
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;

        // 低通滤波分离重力
        final float alpha = 0.8f;
        for (int i = 0; i < 3; i++) {
            // 应用低通滤波算法，更新重力加速度数据
            gravity[i] = alpha * gravity[i] + (1 - alpha) * event.values[i];
            // 计算线性加速度数据
            linearAccel[i] = event.values[i] - gravity[i];
        }

        // 计算加速度的大小
        float magnitude = (float) Math.sqrt(
                linearAccel[0] * linearAccel[0] +
                        linearAccel[1] * linearAccel[1] +
                        linearAccel[2] * linearAccel[2]);

        // 更新滑动窗口
        accelWindow.add(magnitude);
        // 如果滑动窗口已满，移除最早的数据
        if (accelWindow.size() > WINDOW_SIZE) {
            accelWindow.removeFirst();
        }

        // 更新动态阈值
        updateDynamicThreshold();
        // 检测是否是一步
        detectStep(magnitude, event.timestamp);
        // 更新运动状态
        updateMovementState(event.values, event.timestamp);
    }

    // 更新动态阈值的方法
    private void updateDynamicThreshold() {
        float sum = 0, max = 0;
        // 遍历滑动窗口内的数据
        for (float val : accelWindow) {
            // 计算数据总和
            sum += val;
            // 记录最大值
            if (val > max) max = val;
        }
        // 计算动态阈值
        dynamicThreshold = sum / accelWindow.size() + (max - sum / accelWindow.size()) * 0.5f;
    }

    // 检测是否是一步的方法
    private void detectStep(float magnitude, long timestamp) {
        // 如果滑动窗口未满，不进行步数检测
        if (accelWindow.size() < WINDOW_SIZE) return;

        float current = magnitude;
        float previous = accelWindow.get(accelWindow.size() - 2);

        // 如果当前加速度大于动态阈值且上一个加速度小于当前加速度
        if (current > dynamicThreshold && previous < current) {
            // 如果是第一次记录步数或者距离上次记录步数的时间间隔大于 200 毫秒
            if (lastStepTime == 0 || (timestamp - lastStepTime) > 200_000_000) {
                // 步数加 1
                stepCount.postValue(stepCount.getValue() + 1);
                // 更新上一次步数增加的时间戳
                lastStepTime = timestamp;
            }
        }
    }

    // 更新运动状态的方法
    private void updateMovementState(float[] acceleration, long timestamp) {
        // 计算加速度的大小
        float accelMagnitude = (float) Math.sqrt(
                acceleration[0] * acceleration[0] +
                        acceleration[1] * acceleration[2] +
                        acceleration[2] * acceleration[2]);

        // 如果加速度大小大于动态阈值的 0.7 倍，认为在运动
        if (accelMagnitude > dynamicThreshold * 0.7f) {
            // 更新运动状态为 true，并记录运动时间戳
            isMoving.postValue(true);
            lastMovementTime = timestamp;
        }
        // 如果距离上次检测到运动的时间间隔大于运动时间阈值，认为停止运动
        else if (timestamp - lastMovementTime > MOVEMENT_THRESHOLD_MS) {
            // 更新运动状态为 false
            isMoving.postValue(false);
        }
    }

    public void start(Activity activity) {
        this.context = activity.getApplicationContext();

        if (!PermissionUtils.checkActivityPermission(context)) {
            PermissionUtils.requestActivityPermission(activity);
            return;
        }

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void stop() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    // 重置步数的方法
    public void resetStepCount() {
        stepCount.postValue(0);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 当传感器精度变化时的回调，目前为空实现
    }
}
