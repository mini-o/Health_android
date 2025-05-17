// ExerciseRecord.java
package com.example.health.model.pojo;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import com.example.health.BR;
import com.example.health.model.enums.ExerciseType;
import java.util.Date;

public class ExerciseRecord extends BaseObservable {
    private long id;
    private ExerciseType exerciseType;
    private float distance;
    private long duration;
    private float averagePace;
    private int averageBreathingRate;
    private Date startTime;
    private Date endTime;
    private String pathData;

    @Bindable
    public long getId() { return id; }
    public void setId(long id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public ExerciseType getExerciseType() { return exerciseType; }
    public void setExerciseType(ExerciseType exerciseType) {
        this.exerciseType = exerciseType;
        notifyPropertyChanged(BR.exerciseType);
    }

    private boolean running; // 新增运行状态字段

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Bindable
    public float getDistance() { return distance; }
    public void setDistance(float distance) {
        this.distance = distance;
        notifyPropertyChanged(BR.distance);
    }

    @Bindable
    public long getDuration() { return duration; }
    public void setDuration(long duration) {
        this.duration = duration;
        notifyPropertyChanged(BR.duration);
    }

    @Bindable
    public float getAveragePace() { return averagePace; }
    public void setAveragePace(float averagePace) {
        this.averagePace = averagePace;
        notifyPropertyChanged(BR.averagePace);
    }

    @Bindable
    public int getAverageBreathingRate() { return averageBreathingRate; }
    public void setAverageBreathingRate(int averageBreathingRate) {
        this.averageBreathingRate = averageBreathingRate;
        notifyPropertyChanged(BR.averageBreathingRate);
    }

    @Bindable
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
        notifyPropertyChanged(BR.startTime);
    }

    @Bindable
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
        notifyPropertyChanged(BR.endTime);
    }

    @Bindable
    public String getPathData() { return pathData; }
    public void setPathData(String pathData) {
        this.pathData = pathData;
        notifyPropertyChanged(BR.pathData);
    }
}