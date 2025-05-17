// BluetoothRepository.java
package com.example.health.model.repository;

import android.Manifest;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.health.database.HealthDatabase;
import com.example.health.database.dao.BluetoothDeviceDao;
import com.example.health.database.entity.BluetoothDeviceEntity;
import com.example.health.model.pojo.HealthBluetoothDevice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BluetoothRepository {
    private final BluetoothDeviceDao bluetoothDeviceDao;
    private final ExecutorService executor;
    private final MutableLiveData<List<HealthBluetoothDevice>> discoveredDevices = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<HealthBluetoothDevice> connectedDevice = new MutableLiveData<>();
    private final Context context;
    private final LiveData<List<HealthBluetoothDevice>> savedDevices;
    private final BluetoothAdapter bluetoothAdapter;

    public BluetoothRepository(Application application) {
        HealthDatabase db = HealthDatabase.getDatabase(application);
        this.bluetoothDeviceDao = db.bluetoothDeviceDao();
        this.executor = Executors.newSingleThreadExecutor();
        this.context = application.getApplicationContext();
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // 转换数据库实体到POJO
        savedDevices = Transformations.map(bluetoothDeviceDao.getAllDevices(), entities -> {
            List<HealthBluetoothDevice> devices = new ArrayList<>();
            for (BluetoothDeviceEntity entity : entities) {
                devices.add(convertToPojo(entity));
            }
            return devices;
        });
        loadConnectedDevice();
    }


    public LiveData<List<HealthBluetoothDevice>> getSavedDevices() {
        return savedDevices;
    }

    public LiveData<List<HealthBluetoothDevice>> getDiscoveredDevices() {
        return discoveredDevices;
    }

    public LiveData<HealthBluetoothDevice> getConnectedDevice() {
        return connectedDevice;
    }

    public void startDiscovery() {
        if (bluetoothAdapter == null) return;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        bluetoothAdapter.startDiscovery();
    }

    public LiveData<List<HealthBluetoothDevice>> getAllDevices() {
        return discoveredDevices;
    }

    public void addDiscoveredDevice(android.bluetooth.BluetoothDevice device) {
        List<HealthBluetoothDevice> devices = discoveredDevices.getValue();
        if (devices == null) devices = new ArrayList<>();

        boolean exists = false;
        for (HealthBluetoothDevice d : devices) {
            if (d.getMacAddress().equals(device.getAddress())) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            HealthBluetoothDevice newDevice = new HealthBluetoothDevice();
            newDevice.setMacAddress(device.getAddress());

            // 正确使用 Context 检查权限（使用成员变量 context）
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.BLUETOOTH_CONNECT) !=
                    PackageManager.PERMISSION_GRANTED) {
                // 权限未授予，根据需求处理（如返回或提示）
                return;
            }

            newDevice.setName(device.getName());
            devices.add(newDevice);
            discoveredDevices.setValue(devices);
        }
    }

    public void connectDevice(HealthBluetoothDevice device) {
        executor.execute(() -> {
            // 断开所有设备
            bluetoothDeviceDao.disconnectAll();

            // 连接新设备
            BluetoothDeviceEntity entity = new BluetoothDeviceEntity();
            entity.setMacAddress(device.getMacAddress());
            entity.setName(device.getName());
            entity.setConnected(true);
            entity.setLastConnected(new Date());
            // 设置设备能力(根据实际情况)
            entity.setCanReadHeartRate(true);
            entity.setCanReadBloodPressure(true);

            bluetoothDeviceDao.insert(entity);
            bluetoothDeviceDao.connectDevice(device.getMacAddress());

            connectedDevice.postValue(device);
        });
    }

    public void disconnectDevice() {
        executor.execute(() -> {
            bluetoothDeviceDao.disconnectAll();
            connectedDevice.postValue(null);
        });
    }

    public void deleteDevice(HealthBluetoothDevice device) {
        executor.execute(() -> {
            bluetoothDeviceDao.delete(device.getMacAddress());
            if (connectedDevice.getValue() != null &&
                    connectedDevice.getValue().getMacAddress().equals(device.getMacAddress())) {
                connectedDevice.postValue(null);
            }
        });
    }

    private void loadConnectedDevice() {
        executor.execute(() -> {
            BluetoothDeviceEntity entity = bluetoothDeviceDao.getConnectedDevice().getValue();
            if (entity != null) {
                connectedDevice.postValue(convertToPojo(entity));
            }
        });
    }

    private HealthBluetoothDevice convertToPojo(BluetoothDeviceEntity entity) {
        HealthBluetoothDevice device = new HealthBluetoothDevice();
        device.setMacAddress(entity.getMacAddress());
        device.setName(entity.getName());
        device.setConnected(entity.isConnected());
        device.setLastConnected(entity.getLastConnected());
        device.setCanReadHeartRate(entity.isCanReadHeartRate());
        device.setCanReadBloodPressure(entity.isCanReadBloodPressure());
        device.setCanReadBloodOxygen(entity.isCanReadBloodOxygen());
        device.setCanReadSleep(entity.isCanReadSleep());
        device.setCanReadBloodSugar(entity.isCanReadBloodSugar());
        device.setCanReadTemperature(entity.isCanReadTemperature());
        return device;
    }

    private BluetoothDeviceEntity convertToEntity(HealthBluetoothDevice device) {
        BluetoothDeviceEntity entity = new BluetoothDeviceEntity();
        entity.setMacAddress(device.getMacAddress());
        entity.setName(device.getName());
        entity.setConnected(device.isConnected());
        entity.setLastConnected(device.getLastConnected());
        entity.setCanReadHeartRate(device.isCanReadHeartRate());
        entity.setCanReadBloodPressure(device.isCanReadBloodPressure());
        entity.setCanReadBloodOxygen(device.isCanReadBloodOxygen());
        entity.setCanReadSleep(device.isCanReadSleep());
        entity.setCanReadBloodSugar(device.isCanReadBloodSugar());
        entity.setCanReadTemperature(device.isCanReadTemperature());
        return entity;
    }
}