// DailySummaryDao.java
package com.example.health.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.health.database.entity.DailySummaryEntity;
import java.util.Date;
import java.util.List;

@Dao
public interface DailySummaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DailySummaryEntity summary);

    @Query("SELECT * FROM daily_summary WHERE date = :date")
    LiveData<DailySummaryEntity> getByDate(Date date);

    @Query("SELECT * FROM daily_summary WHERE date BETWEEN :start AND :end ORDER BY date ASC")
    LiveData<List<DailySummaryEntity>> getBetween(Date start, Date end);

    @Query("SELECT * FROM daily_summary WHERE strftime('%Y-%m', date/1000, 'unixepoch') = strftime('%Y-%m', :date/1000, 'unixepoch') ORDER BY date ASC")
    LiveData<List<DailySummaryEntity>> getByMonth(Date date);

    @Query("SELECT * FROM daily_summary WHERE strftime('%Y', date/1000, 'unixepoch') = strftime('%Y', :date/1000, 'unixepoch') ORDER BY date ASC")
    LiveData<List<DailySummaryEntity>> getByYear(Date date);
}