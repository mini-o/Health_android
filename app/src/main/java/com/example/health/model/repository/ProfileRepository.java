// ProfileRepository.java
package com.example.health.model.repository;

import android.app.Application;
import android.graphics.Bitmap;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.health.database.HealthDatabase;
import com.example.health.database.dao.LoginHistoryDao;
import com.example.health.database.dao.UserProfileDao;
import com.example.health.database.entity.UserProfileEntity;
import com.example.health.model.enums.Gender;
import com.example.health.model.pojo.UserProfile;
import com.example.health.utils.ImageUtils;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileRepository {
    private final LoginHistoryDao loginHistoryDao;
    private final UserProfileDao userProfileDao;
    private final ExecutorService executor;
    private final MutableLiveData<UserProfile> currentProfile = new MutableLiveData<>();

    public ProfileRepository(Application application) {
        HealthDatabase db = HealthDatabase.getDatabase(application);
        this.userProfileDao = db.userProfileDao();
        this.loginHistoryDao = db.loginHistoryDao();
        this.executor = Executors.newSingleThreadExecutor();
        initializeProfile();
    }

    public void deleteAllHistory() {
        executor.execute(loginHistoryDao::deleteAll);
    }

    private void initializeProfile() {
        executor.execute(() -> {
            UserProfileEntity entity = userProfileDao.getProfile().getValue();
            if (entity == null) {
                entity = new UserProfileEntity();
                entity.setNickname("用户" + System.currentTimeMillis() % 10000);
                entity.setLastUpdated(new Date());
                userProfileDao.insert(entity);
                // 重新查询确保获取ID
                entity = userProfileDao.getProfileSync();
            }
            currentProfile.postValue(convertToPojo(entity));
        });
    }

    public LiveData<UserProfile> getCurrentProfile() {
        return currentProfile;
    }

    public void updateAvatar(Bitmap avatar) {
        executor.execute(() -> {
            UserProfile profile = currentProfile.getValue();
            if (profile != null) {
                Bitmap compressed = ImageUtils.compressBitmap(avatar);
                userProfileDao.updateAvatar(profile.getId(), compressed);
                profile.setAvatar(compressed);
                profile.setLastUpdated(new Date());
                currentProfile.postValue(profile);
            }
        });
    }

    public void updateNickname(String nickname) {
        executor.execute(() -> {
            UserProfile profile = currentProfile.getValue();
            if (profile != null) {
                userProfileDao.updateNickname(profile.getId(), nickname);
                profile.setNickname(nickname);
                profile.setLastUpdated(new Date());
                currentProfile.postValue(profile);
            }
        });
    }

    public void updateGender(Gender gender) {
        executor.execute(() -> {
            UserProfile profile = currentProfile.getValue();
            if (profile != null) {
                userProfileDao.updateGender(profile.getId(), gender);
                profile.setGender(gender);
                profile.setLastUpdated(new Date());
                currentProfile.postValue(profile);
            }
        });
    }

    public void updateBirthDate(Date birthDate) {
        executor.execute(() -> {
            UserProfile profile = currentProfile.getValue();
            if (profile != null) {
                userProfileDao.updateBirthDate(profile.getId(), birthDate);
                profile.setBirthDate(birthDate);
                profile.setLastUpdated(new Date());
                currentProfile.postValue(profile);
            }
        });
    }

    public void updateHeight(float height) {
        executor.execute(() -> {
            UserProfile profile = currentProfile.getValue();
            if (profile != null) {
                userProfileDao.updateHeight(profile.getId(), height);
                profile.setHeight(height);
                profile.setLastUpdated(new Date());
                currentProfile.postValue(profile);
            }
        });
    }

    public void updateWeight(float weight) {
        executor.execute(() -> {
            UserProfile profile = currentProfile.getValue();
            if (profile != null) {
                userProfileDao.updateWeight(profile.getId(), weight);
                profile.setWeight(weight);
                profile.setLastUpdated(new Date());
                currentProfile.postValue(profile);
            }
        });
    }

    private UserProfile convertToPojo(UserProfileEntity entity) {
        UserProfile profile = new UserProfile();
        profile.setId(entity.getId());
        profile.setAvatar(entity.getAvatar());
        profile.setNickname(entity.getNickname());
        profile.setGender(entity.getGender());
        profile.setBirthDate(entity.getBirthDate());
        profile.setHeight(entity.getHeight());
        profile.setWeight(entity.getWeight());
        profile.setLastUpdated(entity.getLastUpdated());
        return profile;
    }

    private UserProfileEntity convertToEntity(UserProfile profile) {
        UserProfileEntity entity = new UserProfileEntity();
        entity.setId(profile.getId());
        entity.setAvatar(profile.getAvatar());
        entity.setNickname(profile.getNickname());
        entity.setGender(profile.getGender());
        entity.setBirthDate(profile.getBirthDate());
        entity.setHeight(profile.getHeight());
        entity.setWeight(profile.getWeight());
        entity.setLastUpdated(profile.getLastUpdated());
        return entity;
    }
}