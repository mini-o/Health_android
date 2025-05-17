// MotionType.java
package com.example.health.model.enums;

public enum MotionType {
    STILL(0, "静止"),
    WALKING(1, "步行"),
    RUNNING(2, "跑步"),
    OTHER(3, "其他运动");

    private final int value;
    private final String description;

    MotionType(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static MotionType fromValue(int value) {
        for (MotionType type : MotionType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        return STILL;
    }
}