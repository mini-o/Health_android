// LoginStatus.java
package com.example.health.model.enums;

public enum LoginStatus {
    UNREGISTERED(0, "未注册"),
    REGISTERED(1, "已注册"),
    LOCKED(2, "已锁定");

    private final int value;
    private final String description;

    LoginStatus(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() { return value; }
    public String getDescription() { return description; }

    public static LoginStatus fromValue(int value) {
        for (LoginStatus status : LoginStatus.values()) {
            if (status.value == value) {
                return status;
            }
        }
        return UNREGISTERED;
    }
}