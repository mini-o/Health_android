package com.example.health.model.pojo;

public class ExerciseDurationSummary {
    private String hour;     // 小时（格式："00"~"23"）
    private int duration;    // 运动时长（单位：秒）

    // 全参构造函数（Room需要无参构造函数）
    public ExerciseDurationSummary() {}

    // 用于测试的构造函数
    public ExerciseDurationSummary(String hour, int duration) {
        this.hour = hour;
        this.duration = duration;
    }

    // Getters
    public String getHour() {
        return hour;
    }

    public int getDuration() {
        return duration;
    }

    // Setters（Room映射需要）
    public void setHour(String hour) {
        this.hour = hour;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    // 格式化显示用方法（可选）
    public String getFormattedHour() {
        return hour + ":00";
    }

    public String getFormattedDuration() {
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}