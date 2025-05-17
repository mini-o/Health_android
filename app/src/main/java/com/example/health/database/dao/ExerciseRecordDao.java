// ExerciseRecordDao.java
package com.example.health.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.health.database.entity.ExerciseRecordEntity;
import java.util.Date;
import java.util.List;

@Dao
public interface ExerciseRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ExerciseRecordEntity record);

    @Query("SELECT * FROM exercise_records ORDER BY startTime DESC LIMIT :limit")
    LiveData<List<ExerciseRecordEntity>> getLatestRecords(int limit);

    @Query("SELECT * FROM exercise_records ORDER BY startTime DESC")
    LiveData<List<ExerciseRecordEntity>> getAllRecords();

    @Query("SELECT MAX(distance) FROM exercise_records")
    LiveData<Float> getMaxDistance();

    @Query("SELECT MIN(averagePace) FROM exercise_records WHERE averagePace > 0")
    LiveData<Float> getMinPace();

    @Query("SELECT MAX(duration) FROM exercise_records")
    LiveData<Long> getMaxDuration();

    @Query("SELECT * FROM exercise_records WHERE id = :id")
    LiveData<ExerciseRecordEntity> getById(long id);

    // 统计记录条数
    @Query("SELECT COUNT(*) FROM exercise_records " +
            "WHERE startTime BETWEEN :start AND :end")
    LiveData<Integer> getRecordCount(long start, long end);

    @Query("SELECT COUNT(*) FROM exercise_records")
    LiveData<Integer> getCount();

    @Query("SELECT SUM(distance) FROM exercise_records")
    LiveData<Float> getTotalDistance();
}