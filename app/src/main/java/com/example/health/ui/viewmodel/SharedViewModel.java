package com.example.health.ui.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.health.database.HealthDatabase;
import com.example.health.database.entity.LoginHistoryEntity;
import com.example.health.model.pojo.UserAccount;

import java.util.List;
import java.util.concurrent.Executors;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>();

    public void checkLoginStatus(Context context) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<LoginHistoryEntity> histories = HealthDatabase.getDatabase(context)
                    .loginHistoryDao()
                    .getRecentHistorySync();
            isLoggedIn.postValue(!histories.isEmpty());
        });
    }

    public LiveData<Boolean> getLoginStatus() {
        return isLoggedIn;
    }
}