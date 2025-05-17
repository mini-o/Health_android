// UserAccount.java
package com.example.health.model.pojo;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import com.example.health.BR;
import com.example.health.model.enums.LoginStatus;
import java.util.Date;

public class UserAccount extends BaseObservable {
    private String phoneNumber;
    private String password;
    private String verificationCode;
    private Date codeExpiryTime;
    private LoginStatus status;
    private Date lastLoginTime;
    private Date createdTime;
    private boolean isCurrentUser;

    @Bindable
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        notifyPropertyChanged(BR.phoneNumber);
    }

    @Bindable
    public String getPassword() { return password; }
    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    @Bindable
    public String getVerificationCode() { return verificationCode; }
    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
        notifyPropertyChanged(BR.verificationCode);
    }

    @Bindable
    public Date getCodeExpiryTime() { return codeExpiryTime; }
    public void setCodeExpiryTime(Date codeExpiryTime) {
        this.codeExpiryTime = codeExpiryTime;
        notifyPropertyChanged(BR.codeExpiryTime);
    }

    @Bindable
    public LoginStatus getStatus() { return status; }
    public void setStatus(LoginStatus status) {
        this.status = status;
        notifyPropertyChanged(BR.status);
    }

    @Bindable
    public Date getLastLoginTime() { return lastLoginTime; }
    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
        notifyPropertyChanged(BR.lastLoginTime);
    }

    @Bindable
    public Date getCreatedTime() { return createdTime; }
    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
        notifyPropertyChanged(BR.createdTime);
    }

    @Bindable
    public boolean isCurrentUser() { return isCurrentUser; }
    public void setCurrentUser(boolean currentUser) {
        this.isCurrentUser = currentUser;
        notifyPropertyChanged(BR.currentUser);
    }
}