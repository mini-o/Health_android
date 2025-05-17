// HealthDatabase.java
package com.example.health.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.health.database.converter.BitmapConverter;
import com.example.health.database.converter.BluetoothTypeConverter;
import com.example.health.database.converter.Converters;
import com.example.health.database.converter.Converters;
import com.example.health.database.converter.ExerciseTypeConverter;
import com.example.health.database.converter.GenderConverter;
import com.example.health.database.converter.LoginStatusConverter;
import com.example.health.database.converter.MotionTypeConverter;
import com.example.health.database.dao.BluetoothDeviceDao;
import com.example.health.database.dao.BreathBaselineDao;
import com.example.health.database.dao.DailySummaryDao;
import com.example.health.database.dao.ExerciseRecordDao;
import com.example.health.database.dao.HealthDataDao;
import com.example.health.database.dao.LoginHistoryDao;
import com.example.health.database.dao.UserAccountDao;
import com.example.health.database.dao.UserProfileDao;
import com.example.health.database.entity.BluetoothDeviceEntity;
import com.example.health.database.entity.BreathBaseline;
import com.example.health.database.entity.DailySummaryEntity;
import com.example.health.database.entity.ExerciseRecordEntity;
import com.example.health.database.entity.HealthDataEntity;
import com.example.health.database.entity.LoginHistoryEntity;
import com.example.health.database.entity.UserProfileEntity;
import com.example.health.database.entity.UserAccountEntity;

@Database(entities = {
        HealthDataEntity.class,
        DailySummaryEntity.class,
        BreathBaseline.class,
        ExerciseRecordEntity.class,
        UserProfileEntity.class,
        BluetoothDeviceEntity.class,
        UserAccountEntity.class,
        LoginHistoryEntity.class},
        version = 1, exportSchema = false)
@TypeConverters({Converters.class, MotionTypeConverter.class,
        ExerciseTypeConverter.class, GenderConverter.class,
        BitmapConverter.class, LoginStatusConverter.class,
        BluetoothTypeConverter.class})
public abstract class HealthDatabase extends RoomDatabase {
    private static volatile HealthDatabase INSTANCE;

    public abstract HealthDataDao healthDataDao();
    public abstract DailySummaryDao dailySummaryDao();
    public abstract BreathBaselineDao breathBaselineDao();
    public abstract ExerciseRecordDao exerciseRecordDao();
    public abstract UserProfileDao userProfileDao();
    public abstract BluetoothDeviceDao bluetoothDeviceDao();
    public abstract UserAccountDao userAccountDao();
    public abstract LoginHistoryDao loginHistoryDao();

    public static HealthDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (HealthDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    HealthDatabase.class, "health_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}