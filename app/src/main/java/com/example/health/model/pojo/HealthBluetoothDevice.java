package com.example.health.model.pojo;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import com.example.health.BR;
import java.util.Date;
import java.util.Objects;

public class HealthBluetoothDevice extends BaseObservable {
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
    private int rssi; // 信号强度

    // Getter/Setter
    public int getRssi() { return rssi; }
    public void setRssi(int rssi) { this.rssi = rssi; }

    // 重写equals和hashCode用于去重
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HealthBluetoothDevice that = (HealthBluetoothDevice) o;
        return macAddress.equals(that.macAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(macAddress);
    }

    // 添加无参构造函数（解决参数不匹配问题）
    public HealthBluetoothDevice() {
    }

    // 保留原有带参构造函数
    public HealthBluetoothDevice(String name, String macAddress) {
        this.name = name;
        this.macAddress = macAddress;
    }

    // 其他方法和属性保持不变
    @Bindable
    public String getMacAddress() { return macAddress; }
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
        notifyPropertyChanged(BR.macAddress);
    }

    @Bindable
    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public boolean isConnected() { return connected; }
    public void setConnected(boolean connected) {
        this.connected = connected;
        notifyPropertyChanged(BR.connected);
    }

    @Bindable
    public Date getLastConnected() { return lastConnected; }
    public void setLastConnected(Date lastConnected) {
        this.lastConnected = lastConnected;
        notifyPropertyChanged(BR.lastConnected);
    }

    @Bindable
    public boolean isCanReadHeartRate() { return canReadHeartRate; }
    public void setCanReadHeartRate(boolean canReadHeartRate) {
        this.canReadHeartRate = canReadHeartRate;
        notifyPropertyChanged(BR.canReadHeartRate);
    }

    @Bindable
    public boolean isCanReadBloodPressure() { return canReadBloodPressure; }
    public void setCanReadBloodPressure(boolean canReadBloodPressure) {
        this.canReadBloodPressure = canReadBloodPressure;
        notifyPropertyChanged(BR.canReadBloodPressure);
    }

    @Bindable
    public boolean isCanReadBloodOxygen() { return canReadBloodOxygen; }
    public void setCanReadBloodOxygen(boolean canReadBloodOxygen) {
        this.canReadBloodOxygen = canReadBloodOxygen;
        notifyPropertyChanged(BR.canReadBloodOxygen);
    }

    @Bindable
    public boolean isCanReadSleep() { return canReadSleep; }
    public void setCanReadSleep(boolean canReadSleep) {
        this.canReadSleep = canReadSleep;
        notifyPropertyChanged(BR.canReadSleep);
    }

    @Bindable
    public boolean isCanReadBloodSugar() { return canReadBloodSugar; }
    public void setCanReadBloodSugar(boolean canReadBloodSugar) {
        this.canReadBloodSugar = canReadBloodSugar;
        notifyPropertyChanged(BR.canReadBloodSugar);
    }

    @Bindable
    public boolean isCanReadTemperature() { return canReadTemperature; }
    public void setCanReadTemperature(boolean canReadTemperature) {
        this.canReadTemperature = canReadTemperature;
        notifyPropertyChanged(BR.canReadTemperature);
    }
}
