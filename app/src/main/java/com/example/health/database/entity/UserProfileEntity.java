// UserProfileEntity.java
package com.example.health.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.health.database.converter.BitmapConverter;
import com.example.health.database.converter.Converters;
import com.example.health.database.converter.GenderConverter;
import com.example.health.model.enums.Gender;
import android.graphics.Bitmap;
import java.util.Date;

@Entity(tableName = "user_profiles")
@TypeConverters({Converters.class, GenderConverter.class, BitmapConverter.class})
public class UserProfileEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private Bitmap avatar;
    private String nickname = "用户" + System.currentTimeMillis() % 10000;
    private Gender gender;
    private Date birthDate;
    private float height; // cm
    private float weight; // kg
    private Date lastUpdated;

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public Bitmap getAvatar() { return avatar; }
    public void setAvatar(Bitmap avatar) { this.avatar = avatar; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }
    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }
    public float getWeight() { return weight; }
    public void setWeight(float weight) { this.weight = weight; }
    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }
}