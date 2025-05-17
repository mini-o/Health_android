// AnomalyLevel.java
package com.example.health.model.enums;

public enum AnomalyLevel {
    NORMAL(0, "正常"),
    MILD(1, "轻度异常"),
    MODERATE(2, "中度异常"),
    SEVERE(3, "严重异常");

    private final int value;
    private final String description;

    AnomalyLevel(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static AnomalyLevel fromValue(int value) {
        for (AnomalyLevel level : AnomalyLevel.values()) {
            if (level.value == value) {
                return level;
            }
        }
        return NORMAL;
    }
}