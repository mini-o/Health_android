// SummaryRepository.java
package com.example.health.model.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.health.database.HealthDatabase;
import com.example.health.database.dao.DailySummaryDao;
import com.example.health.database.entity.DailySummaryEntity;
import com.example.health.model.pojo.DailySummary;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SummaryRepository {
    private final DailySummaryDao dailySummaryDao;
    private final ExecutorService executor;

    public SummaryRepository(Application application) {
        HealthDatabase db = HealthDatabase.getDatabase(application);
        this.dailySummaryDao = db.dailySummaryDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<DailySummaryEntity> getByDate(Date date) {
        return dailySummaryDao.getByDate(date);
    }

    public LiveData<List<DailySummaryEntity>> getBetween(Date start, Date end) {
        return dailySummaryDao.getBetween(start, end);
    }

    public LiveData<List<DailySummaryEntity>> getByMonth(Date date) {
        return dailySummaryDao.getByMonth(date);
    }

    public LiveData<List<DailySummaryEntity>> getByYear(Date date) {
        return dailySummaryDao.getByYear(date);
    }

    public void insert(DailySummary summary) {
        executor.execute(() -> {
            DailySummaryEntity entity = convertToEntity(summary);
            dailySummaryDao.insert(entity);
        });
    }

    private DailySummaryEntity convertToEntity(DailySummary summary) {
        DailySummaryEntity entity = new DailySummaryEntity();
        entity.setDate(summary.getDate());
        entity.setSteps(summary.getSteps());
        entity.setDistance(summary.getDistance());
        entity.setExerciseTime(summary.getExerciseTime());
        entity.setExerciseCount(summary.getExerciseCount());
        entity.setBreathingRateAvg(summary.getBreathingRateAvg());
        entity.setBreathingRateMax(summary.getBreathingRateMax());
        entity.setBreathingRateMin(summary.getBreathingRateMin());
        entity.setSpeedAvg(summary.getSpeedAvg());
        entity.setSpeedMax(summary.getSpeedMax());
        return entity;
    }

    private DailySummary convertToPojo(DailySummaryEntity entity) {
        DailySummary summary = new DailySummary();
        summary.setDate(entity.getDate());
        summary.setSteps(entity.getSteps());
        summary.setDistance(entity.getDistance());
        summary.setExerciseTime(entity.getExerciseTime());
        summary.setExerciseCount(entity.getExerciseCount());
        summary.setBreathingRateAvg(entity.getBreathingRateAvg());
        summary.setBreathingRateMax(entity.getBreathingRateMax());
        summary.setBreathingRateMin(entity.getBreathingRateMin());
        summary.setSpeedAvg(entity.getSpeedAvg());
        summary.setSpeedMax(entity.getSpeedMax());
        return summary;
    }
}