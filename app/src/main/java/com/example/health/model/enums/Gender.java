// Gender.java
package com.example.health.model.enums;

public enum Gender {
    MALE(0, "男"),
    FEMALE(1, "女"),
    OTHER(2, "其他");

    private final int value;
    private final String description;

    Gender(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() { return value; }
    public String getDescription() { return description; }

    public static Gender fromValue(int value) {
        for (Gender gender : Gender.values()) {
            if (gender.value == value) {
                return gender;
            }
        }
        return OTHER;
    }
}