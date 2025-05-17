// BluetoothDeviceEntity.java
package com.example.health.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.health.database.converter.Converters;
import java.util.Date;

@Entity(tableName = "bluetooth_devices")
@TypeConverters(Converters.class)
public class BluetoothDeviceEntity {
    @PrimaryKey
    private String macAddress;
    private String name;
    private boolean connected;
    private Date lastConnected;
    private boolean canReadHeartRate;
    private boolean canReadBloodPressure;
    private boolean canReadBloodOxygen;
    private boolean canReadSleep;
    private boolean canReadBloodSugar;
    private boolean canReadTemperature;

    // Getters and Setters
    public String getMacAddress() { return macAddress; }
    public void setMacAddress(String macAddress) { this.macAddress = macAddress; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isConnected() { return connected; }
    public void setConnected(boolean connected) { this.connected = connected; }
    public Date getLastConnected() { return lastConnected; }
    public void setLastConnected(Date lastConnected) { this.lastConnected = lastConnected; }
    public boolean isCanReadHeartRate() { return canReadHeartRate; }
    public void setCanReadHeartRate(boolean canReadHeartRate) { this.canReadHeartRate = canReadHeartRate; }
    public boolean isCanReadBloodPressure() { return canReadBloodPressure; }
    public void setCanReadBloodPressure(boolean canReadBloodPressure) { this.canReadBloodPressure = canReadBloodPressure; }
    public boolean isCanReadBloodOxygen() { return canReadBloodOxygen; }
    public void setCanReadBloodOxygen(boolean canReadBloodOxygen) { this.canReadBloodOxygen = canReadBloodOxygen; }
    public boolean isCanReadSleep() { return canReadSleep; }
    public void setCanReadSleep(boolean canReadSleep) { this.canReadSleep = canReadSleep; }
    public boolean isCanReadBloodSugar() { return canReadBloodSugar; }
    public void setCanReadBloodSugar(boolean canReadBloodSugar) { this.canReadBloodSugar = canReadBloodSugar; }
    public boolean isCanReadTemperature() { return canReadTemperature; }
    public void setCanReadTemperature(boolean canReadTemperature) { this.canReadTemperature = canReadTemperature; }
}