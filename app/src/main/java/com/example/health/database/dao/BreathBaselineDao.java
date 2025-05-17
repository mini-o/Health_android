// BreathBaselineDao.java
package com.example.health.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.health.database.entity.BreathBaseline;
import java.util.List;

@Dao
public interface BreathBaselineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BreathBaseline baseline);

    @Query("SELECT * FROM breath_baseline WHERE motionType = :type")
    LiveData<BreathBaseline> getByType(int type);

    @Query("SELECT * FROM breath_baseline")
    LiveData<List<BreathBaseline>> getAll();

    @Query("SELECT * FROM breath_baseline")
    List<BreathBaseline> getAllBaselinesSync();
}