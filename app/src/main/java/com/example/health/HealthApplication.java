package com.example.health;

import android.app.Application;
import com.example.health.database.HealthDatabase;
import com.example.health.utils.PermissionUtils;

public class HealthApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化数据库
        HealthDatabase.getDatabase(this);
        // 初始化权限工具
        PermissionUtils.init(this);
    }
}