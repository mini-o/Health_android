// UserProfile.java
package com.example.health.model.pojo;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import com.example.health.BR;
import com.example.health.model.enums.Gender;
import android.graphics.Bitmap;
import java.util.Date;

public class UserProfile extends BaseObservable {
    private long id;
    private Bitmap avatar;
    private String nickname;
    private Gender gender;
    private Date birthDate;
    private float height;
    private float weight;
    private Date lastUpdated;

    @Bindable
    public long getId() { return id; }
    public void setId(long id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public Bitmap getAvatar() { return avatar; }
    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
        notifyPropertyChanged(BR.avatar);
    }

    @Bindable
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) {
        this.nickname = nickname;
        notifyPropertyChanged(BR.nickname);
    }

    @Bindable
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) {
        this.gender = gender;
        notifyPropertyChanged(BR.gender);
    }

    @Bindable
    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
        notifyPropertyChanged(BR.birthDate);
    }

    @Bindable
    public float getHeight() { return height; }
    public void setHeight(float height) {
        this.height = height;
        notifyPropertyChanged(BR.height);
    }

    @Bindable
    public float getWeight() { return weight; }
    public void setWeight(float weight) {
        this.weight = weight;
        notifyPropertyChanged(BR.weight);
    }

    @Bindable
    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
        notifyPropertyChanged(BR.lastUpdated);
    }

    // 计算年龄
    @Bindable
    public int getAge() {
        if (birthDate == null) return 0;
        // 实现年龄计算逻辑
        return (int) ((System.currentTimeMillis() - birthDate.getTime()) / (1000L * 60 * 60 * 24 * 365));
    }
}