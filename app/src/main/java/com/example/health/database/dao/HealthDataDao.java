// HealthDataDao.java
package com.example.health.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.health.database.entity.HealthDataEntity;
import com.example.health.model.pojo.DailySummary;
import com.example.health.model.pojo.ExerciseDurationSummary;
import com.example.health.model.pojo.HourlySummary;
import com.example.health.model.pojo.MonthlySummary;

import java.util.Date;
import java.util.List;

@Dao
public interface HealthDataDao {
    // 插入数据
    @Insert
    void insert(HealthDataEntity data);

    // 查询当日数据（时间戳范围）
    @Query("SELECT * FROM health_data WHERE timestamp BETWEEN :startOfDay AND :endOfDay ORDER BY timestamp ASC")
    LiveData<List<HealthDataEntity>> getDailyData(long startOfDay, long endOfDay);

    // 查询指定日期数据（时间戳范围）
    @Query("SELECT * FROM health_data WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp ASC")
    LiveData<List<HealthDataEntity>> getDataByDateRange(long start, long end);

    // 获取某日原始数据（步数、呼吸、速度）
    @Query("SELECT timestamp, steps, breathingRate, speed, exerciseTime FROM health_data WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp ASC")
    LiveData<List<HealthDataEntity>> getRawDailyData(long start, long end);

    // 按小时聚合数据（时间范围）
    @Query("SELECT " +
            "strftime('%H', timestamp/1000, 'unixepoch') as hour, " +
            "SUM(steps) as steps, " +
            "SUM(distance) as distance, " +
            "SUM(exerciseTime) as duration " +
            "FROM health_data " +
            "WHERE timestamp BETWEEN :start AND :end " +
            "GROUP BY hour")
    LiveData<List<HourlySummary>> getHourlySummary(long start, long end);

    // 按天聚合数据（时间范围）
    @Query("SELECT strftime('%Y-%m-%d', timestamp/1000, 'unixepoch') as date, " +
            "SUM(steps) as steps, " +
            "SUM(distance) as distance, " +
            "SUM(exerciseTime) as duration " +
            "FROM health_data " +
            "WHERE timestamp BETWEEN :start AND :end " +
            "GROUP BY date")
    LiveData<List<DailySummary>> getDailySummary(long start, long end);

    // 按月聚合数据
    @Query("SELECT strftime('%Y-%m', timestamp/1000, 'unixepoch') as month, " +
            "SUM(steps) as steps, " +
            "SUM(distance) as distance, " +
            "SUM(exerciseTime) as duration " +
            "FROM health_data " +
            "WHERE timestamp BETWEEN :start AND :end " +
            "GROUP BY month")
    LiveData<List<MonthlySummary>> getMonthlySummary(long start, long end);

    // 日粒度：按小时聚合（24小时柱状图）
    @Query("SELECT " +
            "strftime('%H', timestamp/1000, 'unixepoch') AS hour, " +
            "SUM(steps) AS steps, " +
            "SUM(distance) AS distance, " +
            "SUM(exerciseTime) AS duration, " +
            "COUNT(*) AS count " +
            "FROM health_data " +
            "WHERE timestamp BETWEEN :start AND :end " +
            "GROUP BY hour " +
            "ORDER BY CAST(hour AS INTEGER) ASC")
    LiveData<List<HourlySummary>> getDailyHourlySummary(long start, long end);

    // 周粒度：按天聚合（7天柱状图）
    @Query("SELECT " +
            "strftime('%Y-%m-%d', timestamp/1000, 'unixepoch') AS date, " +
            "SUM(steps) AS steps, " +
            "SUM(distance) AS distance, " +
            "SUM(exerciseTime) AS duration, " +
            "COUNT(*) AS count " +
            "FROM health_data " +
            "WHERE timestamp BETWEEN :start AND :end " +
            "GROUP BY date " +
            "ORDER BY date ASC")
    LiveData<List<DailySummary>> getWeeklyDailySummary(long start, long end);

    // 月粒度：按天聚合（自然月天数柱状图）
    @Query("SELECT " +
            "strftime('%d', timestamp/1000, 'unixepoch') AS day, " +
            "SUM(steps) AS steps, " +
            "SUM(distance) AS distance, " +
            "SUM(exerciseTime) AS duration, " +
            "COUNT(*) AS count " +
            "FROM health_data " +
            "WHERE timestamp BETWEEN :start AND :end " +
            "GROUP BY day " +
            "ORDER BY CAST(day AS INTEGER) ASC")
    LiveData<List<DailySummary>> getMonthlyDailySummary(long start, long end);

    // 年粒度：按月聚合（12个月柱状图）
    @Query("SELECT " +
            "strftime('%m', timestamp/1000, 'unixepoch') AS month, " +
            "SUM(steps) AS steps, " +
            "SUM(distance) AS distance, " +
            "SUM(exerciseTime) AS duration, " +
            "COUNT(*) AS count " +
            "FROM health_data " +
            "WHERE timestamp BETWEEN :start AND :end " +
            "GROUP BY month " +
            "ORDER BY CAST(month AS INTEGER) ASC")
    LiveData<List<MonthlySummary>> getYearlyMonthlySummary(long start, long end);

    // 运动类型2（跑步）的时长统计（按小时）
    @Query("SELECT " +
            "strftime('%H', timestamp/1000, 'unixepoch') AS hour, " +
            "SUM(exerciseTime) AS duration " +
            "FROM health_data " +
            "WHERE motionType = 2 AND timestamp BETWEEN :start AND :end " +
            "GROUP BY hour " +
            "ORDER BY CAST(hour AS INTEGER) ASC")
    LiveData<List<ExerciseDurationSummary>> getExerciseDurationHourly(long start, long end);

    // 总距离
    @Query("SELECT totalDistance FROM health_data ORDER BY timestamp DESC LIMIT 1")
    LiveData<Float> getTotalDistance();

    // 运动类型2的总时长
    @Query("SELECT SUM(exerciseTime) FROM health_data WHERE motionType = 2")
    LiveData<Integer> getExerciseType2Duration();

    @Query("SELECT SUM(steps) FROM health_data WHERE timestamp BETWEEN :start AND :end")
    LiveData<Integer> getDailyStepsSum(long start, long end);

    @Query("SELECT SUM(exerciseTime) FROM health_data WHERE motionType = 2 AND timestamp BETWEEN :start AND :end")
    LiveData<Integer> getExerciseDurationSum(long start, long end);

    @Query("SELECT SUM(steps) FROM health_data WHERE timestamp BETWEEN :start AND :end")
    LiveData<Integer> getStepsSum(long start, long end);

    @Query("SELECT SUM(exerciseTime) FROM health_data WHERE timestamp BETWEEN :start AND :end")
    LiveData<Integer> getDurationSum(long start, long end);

    @Query("SELECT SUM(distance) FROM health_data WHERE timestamp BETWEEN :start AND :end")
    LiveData<Float> getDistanceSum(long start, long end);

    @Query("SELECT AVG(breathingRate) FROM health_data WHERE timestamp BETWEEN :start AND :end")
    Float getAverageBreathingRateSync(long start, long end);

}
