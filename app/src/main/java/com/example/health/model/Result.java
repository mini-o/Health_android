// Result.java
package com.example.health.model;

public class Result<T> {
    private final T data;
    private final String errorMessage;
    private final boolean success;

    private Result(T data, String errorMessage, boolean success) {
        this.data = data;
        this.errorMessage = errorMessage;
        this.success = success;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(data, null, true);
    }

    public static <T> Result<T> error(String errorMessage) {
        return new Result<>(null, errorMessage, false);
    }

    public T getData() {
        return data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }
}