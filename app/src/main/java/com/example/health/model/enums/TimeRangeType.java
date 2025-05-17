// TimeRangeType.java
package com.example.health.model.enums;

public enum TimeRangeType {
    DAY(0),
    WEEK(1),
    MONTH(2),
    YEAR(3);

    private final int value;

    TimeRangeType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TimeRangeType fromValue(int value) {
        for (TimeRangeType type : TimeRangeType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        return DAY;
    }
}