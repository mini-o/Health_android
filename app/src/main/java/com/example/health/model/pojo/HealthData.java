// HealthData.java
package com.example.health.model.pojo;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import com.example.health.BR;
import com.example.health.model.enums.AnomalyLevel;
import com.example.health.model.enums.BreathPattern;
import com.example.health.model.enums.MotionType;
import java.util.Date;

public class HealthData extends BaseObservable {
    // === 基础运动数据 ===
    private MotionType motionType = MotionType.STILL;
    private int steps = 0;
    private float speed = 0;
    private int intensity = 0;
    private float distance = 0;

    // === 呼吸数据 ===
    private int breathingRate = 0;
    private BreathPattern breathPattern = BreathPattern.NORMAL;
    private AnomalyLevel breathAnomaly = AnomalyLevel.NORMAL;
    private double exhaleRatio = 0.4;
    private int breathSignalQuality = 100;

    // === 其他健康指标 ===
    private Integer heartRate = null;
    private Float bloodPressure = null;
    private Float bloodOxygen = null;
    private String sleepState = null;
    private Float bloodSugar = null;
    private Float temperature = null;

    // === 时间相关 ===
    private Date timestamp = new Date();
    private int exerciseTime = 0;
    private Date lastExerciseTime = null;

    // === 统计字段 ===
    private float totalDistance = 0;
    private int totalExerciseTime = 0;
    private int totalExerciseCount = 0;
    private int dailySteps = 0;
    private int weeklySteps = 0;
    private int monthlySteps = 0;
    private int yearlySteps = 0;

    // === 数据绑定方法 ===
    @Bindable
    public MotionType getMotionType() { return motionType; }
    public void setMotionType(MotionType motionType) {
        this.motionType = motionType;
        notifyPropertyChanged(BR.motionType);
    }

    @Bindable
    public int getSteps() { return steps; }
    public void setSteps(int steps) {
        this.steps = steps;
        notifyPropertyChanged(BR.steps);
    }

    @Bindable
    public float getSpeed() { return speed; }
    public void setSpeed(float speed) {
        this.speed = speed;
        notifyPropertyChanged(BR.speed);
    }

    @Bindable
    public int getIntensity() { return intensity; }
    public void setIntensity(int intensity) {
        this.intensity = intensity;
        notifyPropertyChanged(BR.intensity);
    }

    @Bindable
    public float getDistance() { return distance; }
    public void setDistance(float distance) {
        this.distance = distance;
        notifyPropertyChanged(BR.distance);
    }

    @Bindable
    public int getBreathingRate() { return breathingRate; }
    public void setBreathingRate(int breathingRate) {
        this.breathingRate = breathingRate;
        notifyPropertyChanged(BR.breathingRate);
    }

    @Bindable
    public BreathPattern getBreathPattern() { return breathPattern; }
    public void setBreathPattern(BreathPattern breathPattern) {
        this.breathPattern = breathPattern;
        notifyPropertyChanged(BR.breathPattern);
    }

    @Bindable
    public AnomalyLevel getBreathAnomaly() { return breathAnomaly; }
    public void setBreathAnomaly(AnomalyLevel breathAnomaly) {
        this.breathAnomaly = breathAnomaly;
        notifyPropertyChanged(BR.breathAnomaly);
    }

    @Bindable
    public double getExhaleRatio() { return exhaleRatio; }
    public void setExhaleRatio(double exhaleRatio) {
        this.exhaleRatio = exhaleRatio;
        notifyPropertyChanged(BR.exhaleRatio);
    }

    @Bindable
    public int getBreathSignalQuality() { return breathSignalQuality; }
    public void setBreathSignalQuality(int breathSignalQuality) {
        this.breathSignalQuality = breathSignalQuality;
        notifyPropertyChanged(BR.breathSignalQuality);
    }

    @Bindable
    public Integer getHeartRate() { return heartRate; }
    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
        notifyPropertyChanged(BR.heartRate);
    }

    @Bindable
    public Float getBloodPressure() { return bloodPressure; }
    public void setBloodPressure(Float bloodPressure) {
        this.bloodPressure = bloodPressure;
        notifyPropertyChanged(BR.bloodPressure);
    }

    @Bindable
    public Float getBloodOxygen() { return bloodOxygen; }
    public void setBloodOxygen(Float bloodOxygen) {
        this.bloodOxygen = bloodOxygen;
        notifyPropertyChanged(BR.bloodOxygen);
    }

    @Bindable
    public String getSleepState() { return sleepState; }
    public void setSleepState(String sleepState) {
        this.sleepState = sleepState;
        notifyPropertyChanged(BR.sleepState);
    }

    @Bindable
    public Float getBloodSugar() { return bloodSugar; }
    public void setBloodSugar(Float bloodSugar) {
        this.bloodSugar = bloodSugar;
        notifyPropertyChanged(BR.bloodSugar);
    }

    @Bindable
    public Float getTemperature() { return temperature; }
    public void setTemperature(Float temperature) {
        this.temperature = temperature;
        notifyPropertyChanged(BR.temperature);
    }

    @Bindable
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
        notifyPropertyChanged(BR.timestamp);
    }

    @Bindable
    public int getExerciseTime() { return exerciseTime; }
    public void setExerciseTime(int exerciseTime) {
        this.exerciseTime = exerciseTime;
        notifyPropertyChanged(BR.exerciseTime);
    }

    @Bindable
    public Date getLastExerciseTime() { return lastExerciseTime; }
    public void setLastExerciseTime(Date lastExerciseTime) {
        this.lastExerciseTime = lastExerciseTime;
        notifyPropertyChanged(BR.lastExerciseTime);
    }

    @Bindable
    public float getTotalDistance() { return totalDistance; }
    public void setTotalDistance(float totalDistance) {
        this.totalDistance = totalDistance;
        notifyPropertyChanged(BR.totalDistance);
    }

    @Bindable
    public int getTotalExerciseTime() { return totalExerciseTime; }
    public void setTotalExerciseTime(int totalExerciseTime) {
        this.totalExerciseTime = totalExerciseTime;
        notifyPropertyChanged(BR.totalExerciseTime);
    }

    @Bindable
    public int getTotalExerciseCount() { return totalExerciseCount; }
    public void setTotalExerciseCount(int totalExerciseCount) {
        this.totalExerciseCount = totalExerciseCount;
        notifyPropertyChanged(BR.totalExerciseCount);
    }

    @Bindable
    public int getDailySteps() { return dailySteps; }
    public void setDailySteps(int dailySteps) {
        this.dailySteps = dailySteps;
        notifyPropertyChanged(BR.dailySteps);
    }

    @Bindable
    public int getWeeklySteps() { return weeklySteps; }
    public void setWeeklySteps(int weeklySteps) {
        this.weeklySteps = weeklySteps;
        notifyPropertyChanged(BR.weeklySteps);
    }

    @Bindable
    public int getMonthlySteps() { return monthlySteps; }
    public void setMonthlySteps(int monthlySteps) {
        this.monthlySteps = monthlySteps;
        notifyPropertyChanged(BR.monthlySteps);
    }

    @Bindable
    public int getYearlySteps() { return yearlySteps; }
    public void setYearlySteps(int yearlySteps) {
        this.yearlySteps = yearlySteps;
        notifyPropertyChanged(BR.yearlySteps);
    }
}