// ExerciseType.java
package com.example.health.model.enums;

public enum ExerciseType {
    OUTDOOR_RUNNING(0, "户外跑步"),
    INDOOR_RUNNING(1, "室内跑步");

    private final int value;
    private final String description;

    ExerciseType(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() { return value; }
    public String getDescription() { return description; }

    public static ExerciseType fromValue(int value) {
        for (ExerciseType type : ExerciseType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        return OUTDOOR_RUNNING;
    }
}