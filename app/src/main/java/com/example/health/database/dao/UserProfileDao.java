// UserProfileDao.java
package com.example.health.database.dao;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.health.database.entity.UserProfileEntity;
import com.example.health.model.enums.Gender;

import java.util.Date;

@Dao
public interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserProfileEntity profile);

    @Update
    void update(UserProfileEntity profile);

    @Query("SELECT * FROM user_profiles LIMIT 1")
    UserProfileEntity getProfileSync();

    @Query("SELECT * FROM user_profiles LIMIT 1")
    LiveData<UserProfileEntity> getProfile();

    @Query("UPDATE user_profiles SET avatar = :avatar, lastUpdated = CURRENT_TIMESTAMP WHERE id = :id")
    void updateAvatar(long id, Bitmap avatar);

    @Query("UPDATE user_profiles SET nickname = :nickname, lastUpdated = CURRENT_TIMESTAMP WHERE id = :id")
    void updateNickname(long id, String nickname);

    @Query("UPDATE user_profiles SET gender = :gender, lastUpdated = CURRENT_TIMESTAMP WHERE id = :id")
    void updateGender(long id, Gender gender);

    @Query("UPDATE user_profiles SET birthDate = :birthDate, lastUpdated = CURRENT_TIMESTAMP WHERE id = :id")
    void updateBirthDate(long id, Date birthDate);

    @Query("UPDATE user_profiles SET height = :height, lastUpdated = CURRENT_TIMESTAMP WHERE id = :id")
    void updateHeight(long id, float height);

    @Query("UPDATE user_profiles SET weight = :weight, lastUpdated = CURRENT_TIMESTAMP WHERE id = :id")
    void updateWeight(long id, float weight);
}