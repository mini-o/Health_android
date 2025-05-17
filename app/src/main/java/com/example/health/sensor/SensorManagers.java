package com.example.health.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import java.util.List;

public class SensorManagers implements SensorEventListener {
    private static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_NORMAL;

    private final android.hardware.SensorManager sensorManager;
    private final List<Sensor> registeredSensors = new ArrayList<>();
    private final MutableLiveData<float[]> accelerometerData = new MutableLiveData<>();
    private final MutableLiveData<float[]> gyroscopeData = new MutableLiveData<>();
    private final MutableLiveData<Integer> stepCount = new MutableLiveData<>(0);

    public SensorManagers(Context context) {
        sensorManager = (android.hardware.SensorManager)
                context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void registerSensors() {
        registerSensor(Sensor.TYPE_ACCELEROMETER);
        registerSensor(Sensor.TYPE_GYROSCOPE);
        registerSensor(Sensor.TYPE_STEP_COUNTER);
    }

    public void unregisterSensors() {
        for (Sensor sensor : registeredSensors) {
            sensorManager.unregisterListener(this, sensor);
        }
        registeredSensors.clear();
    }

    private void registerSensor(int sensorType) {
        Sensor sensor = sensorManager.getDefaultSensor(sensorType);
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SENSOR_DELAY);
            registeredSensors.add(sensor);
        }
    }

    public LiveData<float[]> getAccelerometerData() {
        return accelerometerData;
    }

    public LiveData<float[]> getGyroscopeData() {
        return gyroscopeData;
    }

    public LiveData<Integer> getStepCount() {
        return stepCount;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerData.postValue(event.values.clone());
                break;
            case Sensor.TYPE_GYROSCOPE:
                gyroscopeData.postValue(event.values.clone());
                break;
            case Sensor.TYPE_STEP_COUNTER:
                stepCount.postValue((int) event.values[0]);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}