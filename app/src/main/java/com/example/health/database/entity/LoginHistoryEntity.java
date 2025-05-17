// LoginHistoryEntity.java
package com.example.health.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.health.database.converter.Converters;
import java.util.Date;

@Entity(tableName = "login_history")
@TypeConverters(Converters.class)
public class LoginHistoryEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String phoneNumber;
    private Date loginTime;
    private String deviceInfo;
    private String ipAddress;
    private boolean loginSuccess;

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public Date getLoginTime() { return loginTime; }
    public void setLoginTime(Date loginTime) { this.loginTime = loginTime; }
    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public boolean isLoginSuccess() { return loginSuccess; }
    public void setLoginSuccess(boolean loginSuccess) { this.loginSuccess = loginSuccess; }
}