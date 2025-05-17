package com.example.health.model.pojo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

public class HourlySummary {
    @ColumnInfo(name = "hour")
    @NonNull
    private String hour; // 24小时制格式（00-23）

    @ColumnInfo(name = "steps")
    private int steps; // 该小时总步数

    @ColumnInfo(name = "distance")
    private float distance; // 该小时总距离（公里）

    @ColumnInfo(name = "duration")
    private int duration; // 该小时运动总时长（秒）

    private int count;
    public HourlySummary() {}

    // 全参数构造函数
    public HourlySummary(@NonNull String hour, int steps, float distance, int duration, int count) {
        this.hour = hour;
        this.steps = steps;
        this.distance = distance;
        this.duration = duration;
        this.count = count;
    }

    @NonNull
    public String getHour() {
        return hour;
    }

    public void setHour(@NonNull String hour) {
        this.hour = hour;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDisplayHour() {
        return String.format("%s:00", hour);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int duration) {
        this.count = count;
    }

    public float getHourValue() {
        try {
            return Integer.parseInt(hour);
        } catch (NumberFormatException e) {
            return 0f;
        }
    }

    public float getDurationMinutes() {
        return duration / 60f;
    }

    @Override
    public String toString() {
        return "HourlySummary{" +
                "hour='" + hour + '\'' +
                ", steps=" + steps +
                ", distance=" + distance +
                ", duration=" + duration +
                ", count=" + count +
                '}';
    }
}
