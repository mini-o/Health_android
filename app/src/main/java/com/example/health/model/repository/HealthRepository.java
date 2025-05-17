// HealthRepository.java
package com.example.health.model.repository;

import android.app.Application;
import android.icu.util.Calendar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.health.database.HealthDatabase;
import com.example.health.database.dao.BluetoothDeviceDao;
import com.example.health.database.dao.ExerciseRecordDao;
import com.example.health.database.dao.HealthDataDao;
import com.example.health.database.entity.HealthDataEntity;
import com.example.health.model.DateRange;
import com.example.health.model.enums.AnomalyLevel;
import com.example.health.model.enums.BreathPattern;
import com.example.health.model.pojo.DailySummary;
import com.example.health.model.pojo.ExerciseDurationSummary;
import com.example.health.model.pojo.HealthData;
import com.example.health.model.pojo.HourlySummary;
import com.example.health.model.pojo.MonthlySummary;
import com.example.health.sensor.BluetoothManager;
import com.example.health.utils.DateUtils;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HealthRepository {
    private final HealthDataDao healthDataDao;
    private final ExerciseRecordDao exerciseRecordDao;
    private final ExecutorService executor;
    private final MutableLiveData<Integer> currentBreathingRate = new MutableLiveData<>();
    private final MutableLiveData<Float> currentSpeed = new MutableLiveData<>();

    public HealthRepository(Application application) {
        HealthDatabase db = HealthDatabase.getDatabase(application);
        this.healthDataDao = db.healthDataDao();
        this.exerciseRecordDao = db.exerciseRecordDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    // 添加聚合数据访问方法
    public LiveData<List<HourlySummary>> getDailyHourlySummary(long start, long end) {
        return healthDataDao.getDailyHourlySummary(start, end);
    }

    public LiveData<List<DailySummary>> getWeeklyDailySummary(long start, long end) {
        return healthDataDao.getWeeklyDailySummary(start, end);
    }

    public LiveData<List<DailySummary>> getMonthlyDailySummary(long start, long end) {
        return healthDataDao.getMonthlyDailySummary(start, end);
    }

    public LiveData<List<MonthlySummary>> getYearlyMonthlySummary(long start, long end) {
        return healthDataDao.getYearlyMonthlySummary(start, end);
    }

    public LiveData<List<ExerciseDurationSummary>> getExerciseDurationHourly(long start, long end) {
        return healthDataDao.getExerciseDurationHourly(start, end);
    }

    // 实时数据更新
    public void updateRealtimeData(int breathingRate, float speed) {
        currentBreathingRate.postValue(breathingRate);
        currentSpeed.postValue(speed);
    }

    public LiveData<List<HealthDataEntity>> getDataByDateRange(long start, long end) {
        return healthDataDao.getDataByDateRange(start, end);
    }

    // 获取实时呼吸频率
    public LiveData<Integer> getCurrentBreathingRate() {
        return currentBreathingRate;
    }

    // 获取实时速度
    public LiveData<Float> getCurrentSpeed() {
        return currentSpeed;
    }

    // 获取今日总步数
    public LiveData<Integer> getTodaySteps() {
        Date today = new Date();
        return healthDataDao.getDailyStepsSum(
                DateUtils.getStartOfDay(today).getTime(),
                DateUtils.getEndOfDay(today).getTime()
        );
    }

    public LiveData<Integer> getTodayExerciseDuration() {
        Date today = new Date();
        return healthDataDao.getDailyStepsSum(
                DateUtils.getStartOfDay(today).getTime(),
                DateUtils.getEndOfDay(today).getTime()
        );
    }

    // 获取某日期的步数总和
    public LiveData<Integer> getStepsByDate(Date date) {
        return healthDataDao.getDailyStepsSum(
                DateUtils.getStartOfDay(date).getTime(),
                DateUtils.getEndOfDay(date).getTime()
        );
    }

    // 获取某日期范围内原始数据（用于折线图）
    public LiveData<List<HealthDataEntity>> getRawDataByDate(Date date) {
        return healthDataDao.getDataByDateRange(
                DateUtils.getStartOfDay(date).getTime(),
                DateUtils.getEndOfDay(date).getTime()
        );
    }

    // 获取某日期运动类型2的总时长
    public LiveData<Integer> getExerciseDurationByDate(Date date) {
        return healthDataDao.getExerciseDurationSum(
                DateUtils.getStartOfDay(date).getTime(),
                DateUtils.getEndOfDay(date).getTime()
        );
    }

    // 获取累计总距离
    public LiveData<Float> getTotalDistance() {
        return healthDataDao.getTotalDistance();
    }

    // 获取累计运动总次数
    public LiveData<Integer> getTotalExerciseCount() {
        return exerciseRecordDao.getCount();
    }

    public LiveData<Integer> getTotalExerciseDuration() {
        return healthDataDao.getExerciseType2Duration();
    }

    // 按小时聚合数据
    public LiveData<List<HourlySummary>> getHourlySummary(DateRange range) {
        return healthDataDao.getHourlySummary(
                range.getStartDate().getTime(),
                range.getEndDate().getTime()
        );
    }

    // 按天聚合数据
    public LiveData<List<DailySummary>> getDailySummary(DateRange range) {
        return healthDataDao.getDailySummary(
                range.getStartDate().getTime(),
                range.getEndDate().getTime()
        );
    }

    // 按月聚合数据
    public LiveData<List<MonthlySummary>> getMonthlySummary(DateRange range) {
        return healthDataDao.getMonthlySummary(
                range.getStartDate().getTime(),
                range.getEndDate().getTime()
        );
    }

    // 获取运动记录总数（时间段）
    public LiveData<Integer> getExerciseCountByRange(DateRange range) {
        return exerciseRecordDao.getRecordCount(
                range.getStartDate().getTime(),
                range.getEndDate().getTime()
        );
    }

    // 获取某时间段的步数总和
    public LiveData<Integer> getStepsByRange(DateRange range) {
        return healthDataDao.getStepsSum(
                range.getStartDate().getTime(),
                range.getEndDate().getTime()
        );
    }

    // 获取某时间段的运动时长总和
    public LiveData<Integer> getDurationByRange(DateRange range) {
        return healthDataDao.getDurationSum(
                range.getStartDate().getTime(),
                range.getEndDate().getTime()
        );
    }

    // 获取某时间段的距离总和
    public LiveData<Float> getDistanceByRange(DateRange range) {
        return healthDataDao.getDistanceSum(
                range.getStartDate().getTime(),
                range.getEndDate().getTime()
        );
    }

    private HealthDataEntity convertToEntity(HealthData data) {
        HealthDataEntity entity = new HealthDataEntity();
        entity.setMotionType(data.getMotionType());
        entity.setSteps(data.getSteps());
        entity.setSpeed(data.getSpeed());
        entity.setIntensity(data.getIntensity());
        entity.setDistance(data.getDistance());
        entity.setBreathingRate(data.getBreathingRate());
        entity.setBreathPattern(data.getBreathPattern().getValue());
        entity.setBreathAnomaly(data.getBreathAnomaly().getValue());
        entity.setExhaleRatio(data.getExhaleRatio());
        entity.setBreathSignalQuality(data.getBreathSignalQuality());
        entity.setTimestamp(data.getTimestamp().getTime());
        entity.setExerciseTime(data.getExerciseTime());
        entity.setLastExerciseTime(data.getLastExerciseTime());
        entity.setTotalDistance(data.getTotalDistance());
        entity.setTotalExerciseTime(data.getTotalExerciseTime());
        entity.setTotalExerciseCount(data.getTotalExerciseCount());
        entity.setDailySteps(data.getDailySteps());
        entity.setWeeklySteps(data.getWeeklySteps());
        entity.setMonthlySteps(data.getMonthlySteps());
        entity.setYearlySteps(data.getYearlySteps());
        return entity;
    }

    private HealthData convertToPojo(HealthDataEntity entity) {
        HealthData data = new HealthData();
        data.setMotionType(entity.getMotionType());
        data.setSteps(entity.getSteps());
        data.setSpeed(entity.getSpeed());
        data.setIntensity(entity.getIntensity());
        data.setDistance(entity.getDistance());
        data.setBreathingRate(entity.getBreathingRate());
        data.setBreathPattern(BreathPattern.fromValue(entity.getBreathPattern()));
        data.setBreathAnomaly(AnomalyLevel.fromValue(entity.getBreathAnomaly()));
        data.setExhaleRatio(entity.getExhaleRatio());
        data.setBreathSignalQuality(entity.getBreathSignalQuality());
        data.setTimestamp(new Date(entity.getTimestamp()));
        data.setExerciseTime(entity.getExerciseTime());
        data.setLastExerciseTime(entity.getLastExerciseTime());
        data.setTotalDistance(entity.getTotalDistance());
        data.setTotalExerciseTime(entity.getTotalExerciseTime());
        data.setTotalExerciseCount(entity.getTotalExerciseCount());
        data.setDailySteps(entity.getDailySteps());
        data.setWeeklySteps(entity.getWeeklySteps());
        data.setMonthlySteps(entity.getMonthlySteps());
        data.setYearlySteps(entity.getYearlySteps());
        return data;
    }
}