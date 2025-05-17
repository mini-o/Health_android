package com.example.health.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.health.database.converter.Converters;
import java.util.Date;

// 定义一个表示每日总结数据的实体类，映射到名为"daily_summary"的数据库表
@Entity(tableName = "daily_summary")
@TypeConverters(Converters.class)
public class DailySummaryEntity {
    @PrimaryKey
    private Date date;

    private int steps;    // 步数

    private float distance;    // 距离

    private int exerciseTime;    // 运动时间

    private int exerciseCount;    // 运动次数

    private int breathingRateAvg;    // 平均呼吸频率

    private int breathingRateMax;    // 最大呼吸频率

    private int breathingRateMin;    // 最小呼吸频率

    private float speedAvg;    // 平均速度

    private float speedMax;    // 最大速度

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getExerciseTime() {
        return exerciseTime;
    }

    public void setExerciseTime(int exerciseTime) {
        this.exerciseTime = exerciseTime;
    }

    public int getExerciseCount() {
        return exerciseCount;
    }

    public void setExerciseCount(int exerciseCount) {
        this.exerciseCount = exerciseCount;
    }

    public int getBreathingRateAvg() {
        return breathingRateAvg;
    }

    public void setBreathingRateAvg(int breathingRateAvg) {
        this.breathingRateAvg = breathingRateAvg;
    }

    public int getBreathingRateMax() {
        return breathingRateMax;
    }

    public void setBreathingRateMax(int breathingRateMax) {
        this.breathingRateMax = breathingRateMax;
    }

    public int getBreathingRateMin() {
        return breathingRateMin;
    }

    public void setBreathingRateMin(int breathingRateMin) {
        this.breathingRateMin = breathingRateMin;
    }

    public float getSpeedAvg() {
        return speedAvg;
    }

    public void setSpeedAvg(float speedAvg) {
        this.speedAvg = speedAvg;
    }

    public float getSpeedMax() {
        return speedMax;
    }

    public void setSpeedMax(float speedMax) {
        this.speedMax = speedMax;
    }
}