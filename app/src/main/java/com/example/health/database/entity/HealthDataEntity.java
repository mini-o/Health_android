// HealthDataEntity.java
package com.example.health.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.health.database.converter.Converters;
import com.example.health.database.converter.MotionTypeConverter;
import com.example.health.model.enums.MotionType;
import java.util.Date;

// 定义健康数据实体类，对应数据库中的health_data表
@Entity(tableName = "health_data")
// 指定使用的类型转换器，处理Date和MotionType类型
@TypeConverters({Converters.class, MotionTypeConverter.class})
public class HealthDataEntity {
    // 主键
    @PrimaryKey(autoGenerate = true)
    private long id;

    // 运动类型，使用MotionType枚举
    private MotionType motionType;

    private int steps;          // 步数

    private float speed;    // 速度

    private int intensity;    // 运动强度

    private float distance;    // 单次运动距离

    private int breathingRate;    // 呼吸频率

    private int breathPattern;    // 呼吸模式

    private int breathAnomaly;    // 呼吸异常情况

    private double exhaleRatio;    // 呼气比例

    private int breathSignalQuality;    // 呼吸信号质量

    private long timestamp;    // 时间戳（毫秒）

    private int exerciseTime;    // 运动时长（秒）

    private Date lastExerciseTime;    // 最后一次运动时间

    private float totalDistance;    // 累计总距离

    private int totalExerciseTime;    // 累计总运动时间

    private int totalExerciseCount;    // 累计运动次数

    private int dailySteps;    // 日步数

    private int weeklySteps;    // 周步数

    private int monthlySteps;    // 月步数

    private int yearlySteps;    // 年步数

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MotionType getMotionType() {
        return motionType;
    }

    public void setMotionType(MotionType motionType) {
        this.motionType = motionType;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getBreathingRate() {
        return breathingRate;
    }

    public void setBreathingRate(int breathingRate) {
        this.breathingRate = breathingRate;
    }

    public int getBreathPattern() {
        return breathPattern;
    }

    public void setBreathPattern(int breathPattern) {
        this.breathPattern = breathPattern;
    }

    public int getBreathAnomaly() {
        return breathAnomaly;
    }

    public void setBreathAnomaly(int breathAnomaly) {
        this.breathAnomaly = breathAnomaly;
    }

    public double getExhaleRatio() {
        return exhaleRatio;
    }

    public void setExhaleRatio(double exhaleRatio) {
        this.exhaleRatio = exhaleRatio;
    }

    public int getBreathSignalQuality() {
        return breathSignalQuality;
    }

    public void setBreathSignalQuality(int breathSignalQuality) {
        this.breathSignalQuality = breathSignalQuality;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getExerciseTime() {
        return exerciseTime;
    }

    public void setExerciseTime(int exerciseTime) {
        this.exerciseTime = exerciseTime;
    }

    public Date getLastExerciseTime() {
        return lastExerciseTime;
    }

    public void setLastExerciseTime(Date lastExerciseTime) {
        this.lastExerciseTime = lastExerciseTime;
    }

    public float getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(float totalDistance) {
        this.totalDistance = totalDistance;
    }

    public int getTotalExerciseTime() {
        return totalExerciseTime;
    }

    public void setTotalExerciseTime(int totalExerciseTime) {
        this.totalExerciseTime = totalExerciseTime;
    }

    public int getTotalExerciseCount() {
        return totalExerciseCount;
    }

    public void setTotalExerciseCount(int totalExerciseCount) {
        this.totalExerciseCount = totalExerciseCount;
    }

    public int getDailySteps() {
        return dailySteps;
    }

    public void setDailySteps(int dailySteps) {
        this.dailySteps = dailySteps;
    }

    public int getWeeklySteps() {
        return weeklySteps;
    }

    public void setWeeklySteps(int weeklySteps) {
        this.weeklySteps = weeklySteps;
    }

    public int getMonthlySteps() {
        return monthlySteps;
    }

    public void setMonthlySteps(int monthlySteps) {
        this.monthlySteps = monthlySteps;
    }

    public int getYearlySteps() {
        return yearlySteps;
    }

    public void setYearlySteps(int yearlySteps) {
        this.yearlySteps = yearlySteps;
    }
}
