// UserAccountEntity.java
package com.example.health.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.health.database.converter.Converters;
import com.example.health.database.converter.LoginStatusConverter;
import com.example.health.model.enums.LoginStatus;
import java.util.Date;

@Entity(tableName = "user_accounts")
@TypeConverters({Converters.class, LoginStatusConverter.class})
public class UserAccountEntity {
    @PrimaryKey
    private String phoneNumber;
    private String password;
    private String verificationCode;
    private Date codeExpiryTime;
    private LoginStatus status;
    private Date lastLoginTime;
    private Date createdTime;
    private boolean isCurrentUser;

    // Getters and Setters
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getVerificationCode() { return verificationCode; }
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }
    public Date getCodeExpiryTime() { return codeExpiryTime; }
    public void setCodeExpiryTime(Date codeExpiryTime) { this.codeExpiryTime = codeExpiryTime; }
    public LoginStatus getStatus() { return status; }
    public void setStatus(LoginStatus status) { this.status = status; }
    public Date getLastLoginTime() { return lastLoginTime; }
    public void setLastLoginTime(Date lastLoginTime) { this.lastLoginTime = lastLoginTime; }
    public Date getCreatedTime() { return createdTime; }
    public void setCreatedTime(Date createdTime) { this.createdTime = createdTime; }
    public boolean isCurrentUser() { return isCurrentUser; }
    public void setCurrentUser(boolean currentUser) { isCurrentUser = currentUser; }
}