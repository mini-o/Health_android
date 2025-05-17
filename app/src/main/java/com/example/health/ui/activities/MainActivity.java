package com.example.health.ui.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.example.health.R;
import com.example.health.database.HealthDatabase;
import com.example.health.database.dao.LoginHistoryDao;
import com.example.health.database.entity.LoginHistoryEntity;
import com.example.health.databinding.ActivityMainBinding;
import com.example.health.ui.fragments.*;
import com.example.health.ui.viewmodel.SharedViewModel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private ExecutorService dbExecutor = Executors.newSingleThreadExecutor();
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 数据绑定初始化
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 初始化SharedViewModel
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // 检查登录状态
        checkLoginStatus();
    }

    private void checkLoginStatus() {
        dbExecutor.execute(() -> {
            // 同步查询最近登录记录
            LoginHistoryDao dao = HealthDatabase.getDatabase(this).loginHistoryDao();
            List<LoginHistoryEntity> histories = dao.getRecentHistorySync();

            runOnUiThread(() -> {
                if (histories != null && !histories.isEmpty()) {
                    navigateToHealth();
                } else {
                    navigateToLogin();
                }
            });
        });
    }

    private void navigateToLogin() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.loginFragment, new LoginFragment())
                .commit();
    }

    private void navigateToHealth() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.healthFragment, new HealthFragment())
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbExecutor.shutdownNow();
    }
}