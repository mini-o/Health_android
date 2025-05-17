// LoginHistoryDao.java
package com.example.health.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.health.database.entity.LoginHistoryEntity;
import java.util.List;

@Dao
public interface LoginHistoryDao {
    @Insert
    void insert(LoginHistoryEntity history);

    @Query("SELECT * FROM login_history ORDER BY loginTime DESC LIMIT 10")
    LiveData<List<LoginHistoryEntity>> getRecentHistory();

    @Query("SELECT * FROM login_history WHERE phoneNumber = :phoneNumber ORDER BY loginTime DESC")
    LiveData<List<LoginHistoryEntity>> getHistoryByPhone(String phoneNumber);

    @Query("DELETE FROM login_history")
    void deleteAll();

    @Query("SELECT * FROM login_history ORDER BY loginTime DESC LIMIT 10")
    List<LoginHistoryEntity> getRecentHistorySync();
}