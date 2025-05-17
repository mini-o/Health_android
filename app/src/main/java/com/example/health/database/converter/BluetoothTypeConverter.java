package com.example.health.database.converter;

import androidx.room.TypeConverter;
import com.example.health.database.entity.BluetoothDeviceEntity;
import com.example.health.model.pojo.HealthBluetoothDevice;

public class BluetoothTypeConverter {
    @TypeConverter
    public static HealthBluetoothDevice entityToPojo(BluetoothDeviceEntity entity) {
        if (entity == null) return null;
        HealthBluetoothDevice device = new HealthBluetoothDevice(entity.getName(), entity.getMacAddress());
        device.setConnected(entity.isConnected());
        return device;
    }

    @TypeConverter
    public static BluetoothDeviceEntity pojoToEntity(HealthBluetoothDevice device) {
        if (device == null) return null;
        BluetoothDeviceEntity entity = new BluetoothDeviceEntity();
        entity.setMacAddress(device.getMacAddress());
        entity.setName(device.getName());
        entity.setConnected(device.isConnected());
        return entity;
    }
}