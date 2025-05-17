// UserAccountDao.java
package com.example.health.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.health.database.entity.UserAccountEntity;
import java.util.Date;
import java.util.List;

@Dao
public interface UserAccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserAccountEntity account);

    @Update
    void update(UserAccountEntity account);

    @Query("SELECT * FROM user_accounts WHERE phoneNumber = :phoneNumber LIMIT 1")
    LiveData<UserAccountEntity> getByPhoneNumber(String phoneNumber);

    @Query("SELECT * FROM user_accounts WHERE isCurrentUser = 1 LIMIT 1")
    LiveData<UserAccountEntity> getCurrentUser();

    @Query("UPDATE user_accounts SET isCurrentUser = 0")
    void clearCurrentUser();

    @Query("UPDATE user_accounts SET isCurrentUser = 1 WHERE phoneNumber = :phoneNumber")
    void setCurrentUser(String phoneNumber);

    @Query("UPDATE user_accounts SET password = :password WHERE phoneNumber = :phoneNumber")
    void updatePassword(String phoneNumber, String password);

    @Query("UPDATE user_accounts SET verificationCode = :code, codeExpiryTime = :expiryTime WHERE phoneNumber = :phoneNumber")
    void updateVerificationCode(String phoneNumber, String code, Date expiryTime);

    @Query("SELECT * FROM user_accounts")
    LiveData<List<UserAccountEntity>> getAllAccounts();
}