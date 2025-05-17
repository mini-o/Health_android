package com.example.health.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExerciseUtils {
    private static final DecimalFormat paceFormat = new DecimalFormat("#0'':''00");
    private static final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault());

    public static String formatPace(float pace) {
        if (pace <= 0) return "0:00";
        float minutesPerKm = 60 / pace;
        int minutes = (int) minutesPerKm;
        int seconds = (int) ((minutesPerKm - minutes) * 60);
        return minutes + "'" + String.format(Locale.getDefault(), "%02d\"", seconds);
    }

    public static String formatDateTime(Date date) {
        return date != null ? dateFormat.format(date) : "";
    }

    public static String formatDuration(long durationMs) {
        long seconds = durationMs / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs);
    }

    public static float calculatePace(float distanceKm, long durationMs) {
        if (distanceKm <= 0 || durationMs <= 0) return 0;
        float hours = durationMs / (1000f * 3600f);
        return distanceKm / hours; // km/h
    }
}