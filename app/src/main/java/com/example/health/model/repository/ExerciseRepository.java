// ExerciseRepository.java
package com.example.health.model.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.health.database.HealthDatabase;
import com.example.health.database.dao.ExerciseRecordDao;
import com.example.health.database.entity.ExerciseRecordEntity;
import com.example.health.model.enums.ExerciseType;
import com.example.health.model.pojo.ExerciseRecord;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.example.health.database.dao.HealthDataDao;

public class ExerciseRepository {
    private final ExerciseRecordDao exerciseRecordDao;
    private final HealthDataDao healthDataDao; // 新增
    private final ExecutorService executor;
    private final MutableLiveData<ExerciseRecord> currentExercise = new MutableLiveData<>();

    public ExerciseRepository(Application application) {
        HealthDatabase db = HealthDatabase.getDatabase(application);
        this.exerciseRecordDao = db.exerciseRecordDao();
        this.healthDataDao = db.healthDataDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<ExerciseRecord> getCurrentExercise() {
        return currentExercise;
    }

    public void startNewExercise(ExerciseType type) {
        ExerciseRecord record = new ExerciseRecord();
        record.setExerciseType(type);
        record.setStartTime(new Date());
        currentExercise.setValue(record);
    }

    public void updateCurrentExercise(ExerciseRecord record) {
        currentExercise.setValue(record);
    }

    public LiveData<List<ExerciseRecordEntity>> getLatestRecords(int limit) {
        return exerciseRecordDao.getLatestRecords(limit);
    }

    public LiveData<List<ExerciseRecordEntity>> getAllRecords() {
        return exerciseRecordDao.getAllRecords();
    }

    public LiveData<Float> getMaxDistance() {
        return exerciseRecordDao.getMaxDistance();
    }

    public LiveData<Float> getMinPace() {
        return exerciseRecordDao.getMinPace();
    }

    public LiveData<Long> getMaxDuration() {
        return exerciseRecordDao.getMaxDuration();
    }

    public LiveData<Float> getTotalDistance() {
        return exerciseRecordDao.getTotalDistance();
    }

    public void saveExercise(ExerciseRecord record) {
        executor.execute(() -> {
            // 计算平均呼吸频率
            long start = record.getStartTime().getTime();
            long end = record.getEndTime().getTime();
            Float avgBreathing = healthDataDao.getAverageBreathingRateSync(start, end);
            record.setAverageBreathingRate(avgBreathing != null ? Math.round(avgBreathing) : 0);

            ExerciseRecordEntity entity = convertToEntity(record);
            exerciseRecordDao.insert(entity);
            currentExercise.postValue(null);
        });
    }

    private ExerciseRecordEntity convertToEntity(ExerciseRecord record) {
        ExerciseRecordEntity entity = new ExerciseRecordEntity();
        entity.setId(record.getId());
        entity.setExerciseType(record.getExerciseType());
        entity.setDistance(record.getDistance());
        entity.setDuration(record.getDuration());
        entity.setAveragePace(record.getAveragePace());
        entity.setAverageBreathingRate(record.getAverageBreathingRate());
        entity.setStartTime(record.getStartTime());
        entity.setEndTime(record.getEndTime());
        entity.setPathData(record.getPathData());
        return entity;
    }

    private ExerciseRecord convertToPojo(ExerciseRecordEntity entity) {
        ExerciseRecord record = new ExerciseRecord();
        record.setId(entity.getId());
        record.setExerciseType(entity.getExerciseType());
        record.setDistance(entity.getDistance());
        record.setDuration(entity.getDuration());
        record.setAveragePace(entity.getAveragePace());
        record.setAverageBreathingRate(entity.getAverageBreathingRate());
        record.setStartTime(entity.getStartTime());
        record.setEndTime(entity.getEndTime());
        record.setPathData(entity.getPathData());
        return record;
    }
}