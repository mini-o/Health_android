// BluetoothDeviceDao.java
package com.example.health.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.health.database.entity.BluetoothDeviceEntity;
import java.util.List;

@Dao
public interface BluetoothDeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BluetoothDeviceEntity device);

    @Update
    void update(BluetoothDeviceEntity device);

    @Query("DELETE FROM bluetooth_devices WHERE macAddress = :macAddress")
    void delete(String macAddress);

    @Query("SELECT * FROM bluetooth_devices ORDER BY lastConnected DESC")
    LiveData<List<BluetoothDeviceEntity>> getAllDevices();

    @Query("SELECT * FROM bluetooth_devices WHERE connected = 1 LIMIT 1")
    LiveData<BluetoothDeviceEntity> getConnectedDevice();

    @Query("UPDATE bluetooth_devices SET connected = 0")
    void disconnectAll();

    @Query("UPDATE bluetooth_devices SET connected = 1, lastConnected = CURRENT_TIMESTAMP WHERE macAddress = :macAddress")
    void connectDevice(String macAddress);

    @Query("SELECT * FROM bluetooth_devices ORDER BY lastConnected DESC")
    List<BluetoothDeviceEntity> getAllDevicesSync();
}