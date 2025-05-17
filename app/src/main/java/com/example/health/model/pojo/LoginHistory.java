// LoginHistory.java
package com.example.health.model.pojo;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import com.example.health.BR;
import java.util.Date;

public class LoginHistory extends BaseObservable {
    private long id;
    private String phoneNumber;
    private Date loginTime;
    private String deviceInfo;
    private String ipAddress;
    private boolean loginSuccess;

    @Bindable
    public long getId() { return id; }
    public void setId(long id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        notifyPropertyChanged(BR.phoneNumber);
    }

    @Bindable
    public Date getLoginTime() { return loginTime; }
    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
        notifyPropertyChanged(BR.loginTime);
    }

    @Bindable
    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
        notifyPropertyChanged(BR.deviceInfo);
    }

    @Bindable
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        notifyPropertyChanged(BR.ipAddress);
    }

    @Bindable
    public boolean isLoginSuccess() { return loginSuccess; }
    public void setLoginSuccess(boolean loginSuccess) {
        this.loginSuccess = loginSuccess;
        notifyPropertyChanged(BR.loginSuccess);
    }
}