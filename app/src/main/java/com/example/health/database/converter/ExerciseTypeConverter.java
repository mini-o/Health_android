// ExerciseTypeConverter.java
package com.example.health.database.converter;

import androidx.room.TypeConverter;
import com.example.health.model.enums.ExerciseType;

public class ExerciseTypeConverter {
    @TypeConverter
    public static ExerciseType toExerciseType(int value) {
        return ExerciseType.fromValue(value);
    }

    @TypeConverter
    public static int fromExerciseType(ExerciseType type) {
        return type.getValue();
    }
}