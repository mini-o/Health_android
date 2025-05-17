// GenderConverter.java
package com.example.health.database.converter;

import androidx.room.TypeConverter;
import com.example.health.model.enums.Gender;

public class GenderConverter {
    @TypeConverter
    public static Gender toGender(int value) {
        return Gender.fromValue(value);
    }

    @TypeConverter
    public static int fromGender(Gender gender) {
        return gender.getValue();
    }
}