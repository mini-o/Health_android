package com.example.health.database.converter;

import androidx.room.TypeConverter;
import com.example.health.model.enums.MotionType;

public class MotionTypeConverter {
    // 将int类型的值转换为MotionType类型
    @TypeConverter
    public static MotionType toMotionType(int value) {
        return MotionType.fromValue(value);
    }

    // 将MotionType类型的值转换为int类型
    @TypeConverter
    public static int fromMotionType(MotionType type) {
        return type.getValue();
    }
}