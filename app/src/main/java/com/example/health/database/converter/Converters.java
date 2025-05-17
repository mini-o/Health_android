package com.example.health.database.converter;

import androidx.room.TypeConverter;

import java.util.Date;

public class Converters {
    //将Long类型的时间戳转换为Date类型
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    //将Date类型转换为Long类型的时间戳
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}