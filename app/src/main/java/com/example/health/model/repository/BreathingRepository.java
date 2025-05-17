// BreathingRepository.java
package com.example.health.model.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.health.database.HealthDatabase;
import com.example.health.database.dao.BreathBaselineDao;
import com.example.health.database.entity.BreathBaseline;
import com.example.health.model.enums.MotionType;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BreathingRepository {
    private final BreathBaselineDao breathBaselineDao;
    private final ExecutorService executor;

    public BreathingRepository(Application application) {
        HealthDatabase db = HealthDatabase.getDatabase(application);
        this.breathBaselineDao = db.breathBaselineDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void insertBaseline(MotionType motionType, double baseline) {
        executor.execute(() -> {
            BreathBaseline entity = new BreathBaseline();
            entity.setMotionType(motionType);
            entity.setBaselineValue(baseline);
            entity.setTimestamp(new Date());
            breathBaselineDao.insert(entity);
        });
    }

    public LiveData<List<BreathBaseline>> getAllBaselines() {
        return breathBaselineDao.getAll();
    }

    public LiveData<BreathBaseline> getBaselineByType(int type) {
        return breathBaselineDao.getByType(type);
    }
}