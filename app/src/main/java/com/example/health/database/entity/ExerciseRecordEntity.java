// ExerciseRecordEntity.java
package com.example.health.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.health.database.converter.Converters;
import com.example.health.database.converter.ExerciseTypeConverter;
import com.example.health.model.enums.ExerciseType;
import java.util.Date;

@Entity(tableName = "exercise_records")
@TypeConverters({Converters.class, ExerciseTypeConverter.class})
public class ExerciseRecordEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private ExerciseType exerciseType;
    private float distance; // 公里
    private long duration; // 毫秒
    private float averagePace; // 分钟/公里
    private int averageBreathingRate; // 次/分钟
    private Date startTime;
    private Date endTime;
    private String pathData; // 轨迹数据(JSON)

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public ExerciseType getExerciseType() { return exerciseType; }
    public void setExerciseType(ExerciseType exerciseType) { this.exerciseType = exerciseType; }
    public float getDistance() { return distance; }
    public void setDistance(float distance) { this.distance = distance; }
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
    public float getAveragePace() { return averagePace; }
    public void setAveragePace(float averagePace) { this.averagePace = averagePace; }
    public int getAverageBreathingRate() { return averageBreathingRate; }
    public void setAverageBreathingRate(int averageBreathingRate) { this.averageBreathingRate = averageBreathingRate; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public String getPathData() { return pathData; }
    public void setPathData(String pathData) { this.pathData = pathData; }
}