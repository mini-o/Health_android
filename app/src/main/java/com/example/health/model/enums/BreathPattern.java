// BreathPattern.java
package com.example.health.model.enums;

public enum BreathPattern {
    NORMAL(0, "正常"),
    DEEP(1, "深呼吸"),
    RAPID(2, "急促呼吸");

    private final int value;
    private final String description;

    BreathPattern(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static BreathPattern fromValue(int value) {
        for (BreathPattern pattern : BreathPattern.values()) {
            if (pattern.value == value) {
                return pattern;
            }
        }
        return NORMAL;
    }
}