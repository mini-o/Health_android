// LoginStatusConverter.java
package com.example.health.database.converter;

import androidx.room.TypeConverter;
import com.example.health.model.enums.LoginStatus;

public class LoginStatusConverter {
    @TypeConverter
    public static LoginStatus toLoginStatus(int value) {
        return LoginStatus.fromValue(value);
    }

    @TypeConverter
    public static int fromLoginStatus(LoginStatus status) {
        return status.getValue();
    }
}