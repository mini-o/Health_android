package com.example.health.model.pojo;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import com.example.health.BR;

public class HealthReport extends BaseObservable {
    // 基础信息
    private String reportId;          // 报告ID
    private long timestamp;           // 生成时间戳

    // 核心指标
    private float bmi;                // BMI指数
    private String bmiLevel;          // BMI等级
    private float bodyFatRate;        // 体脂率
    private int heartRate;            // 静息心率
    private int bloodPressureHigh;    // 血压（高压）
    private int bloodPressureLow;     // 血压（低压）
    private int dailySteps;           // 日均步数
    private float sleepDuration;      // 日均睡眠时长（小时）
    private int stressScore;
    private String heartLevel;
    private String heartStatus;
    private float sleepHours;

    // 评分系统（0-100分）
    private int overallScore;         // 综合得分
    private int bmiScore;             // BMI得分
    private int cardioScore;          // 心肺得分
    private int exerciseScore;        // 运动得分
    private int sleepScore;           // 睡眠得分
    private int nutritionScore;       // 营养得分

    // 分析结果
    private String riskAssessment;    // 健康风险评估
    private String suggestions;       // 综合建议
    private String detailedAnalysis;  // 详细分析

    // 数据绑定支持
    public HealthReport() {
        this.timestamp = System.currentTimeMillis();
    }

    @Bindable
    public int getStressScore() {
        return stressScore;
    }

    public void setStressScore(int stressScore) {
        this.stressScore = stressScore;
        notifyPropertyChanged(BR.stressScore);
    }

    @Bindable
    public String getHeartLevel() {
        return heartLevel;
    }

    public void setHeartLevel(String heartLevel) {
        this.heartLevel = heartLevel;
        notifyPropertyChanged(BR.heartLevel);
    }

    @Bindable
    public String getHeartStatus() {
        return heartStatus;
    }

    public void setHeartStatus(String heartStatus) {
        this.heartStatus = heartStatus;
        notifyPropertyChanged(BR.heartStatus);
    }

    @Bindable
    public float getSleepHours() {
        return sleepHours;
    }

    public void setSleepHours(float sleepHours) {
        this.sleepHours = sleepHours;
        notifyPropertyChanged(BR.sleepHours);
    }

    @Bindable
    public float getBmi() {
        return bmi;
    }

    public void setBmi(float bmi) {
        this.bmi = bmi;
        notifyPropertyChanged(BR.bmi);
    }

    @Bindable
    public String getBmiLevel() {
        return bmiLevel;
    }

    public void setBmiLevel(String bmiLevel) {
        this.bmiLevel = bmiLevel;
        notifyPropertyChanged(BR.bmiLevel);
    }

    @Bindable
    public int getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(int overallScore) {
        this.overallScore = overallScore;
        notifyPropertyChanged(BR.overallScore);
    }

    @Bindable
    public String getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String suggestions) {
        this.suggestions = suggestions;
        notifyPropertyChanged(BR.suggestions);
    }

    // 运动评分
    @Bindable
    public int getExerciseScore() {
        return exerciseScore;
    }

    public void setExerciseScore(int exerciseScore) {
        this.exerciseScore = exerciseScore;
        notifyPropertyChanged(BR.exerciseScore);
    }

    // 睡眠评分
    @Bindable
    public int getSleepScore() {
        return sleepScore;
    }

    public void setSleepScore(int sleepScore) {
        this.sleepScore = sleepScore;
        notifyPropertyChanged(BR.sleepScore);
    }

    // 营养评分
    @Bindable
    public int getNutritionScore() {
        return nutritionScore;
    }

    public void setNutritionScore(int nutritionScore) {
        this.nutritionScore = nutritionScore;
        notifyPropertyChanged(BR.nutritionScore);
    }

    // 其他属性的getter/setter...

    @Bindable
    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
        notifyPropertyChanged(BR.heartRate);
    }

    @Bindable
    public String getFormattedBloodPressure() {
        return bloodPressureHigh + "/" + bloodPressureLow + " mmHg";
    }

    @Bindable
    public String getFormattedSleepDuration() {
        return String.format("%.1f 小时", sleepDuration);
    }

    @Bindable
    public int getBmiScore() {
        return bmiScore;
    }

    public void setBmiScore(int bmiScore) {
        this.bmiScore = bmiScore;
        notifyPropertyChanged(BR.bmiScore);
    }

    @Bindable
    public int getCardioScore() {
        return cardioScore;
    }

    public void setCardioScore(int cardioScore) {
        this.cardioScore = cardioScore;
        notifyPropertyChanged(BR.cardioScore);
    }

    // 评分计算逻辑
    public void calculateScores() {
        // BMI评分（示例逻辑）
        this.bmiScore = calculateBmiScore();

        // 心肺评分（示例逻辑）
        this.cardioScore = calculateCardioScore();

        // 综合评分计算
        this.overallScore = (bmiScore + cardioScore + exerciseScore + sleepScore + nutritionScore) / 5;
    }

    private int calculateBmiScore() {
        if (bmi < 18.5) return 60;
        if (bmi <= 24) return 90;
        if (bmi <= 28) return 70;
        return 50;
    }

    private int calculateCardioScore() {
        if (heartRate < 60) return 80;
        if (heartRate <= 80) return 90;
        if (heartRate <= 100) return 70;
        return 50;
    }

    // 生成器模式
    public static class Builder {
        private final HealthReport report = new HealthReport();

        public Builder setBmi(float bmi) {
            report.setBmi(bmi);
            return this;
        }

        public HealthReport build() {
            report.calculateScores();
            return report;
        }
    }
}