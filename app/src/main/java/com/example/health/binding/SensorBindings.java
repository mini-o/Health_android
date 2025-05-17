// SensorBindings.java
package com.example.health.binding;

import android.widget.TextView;
import androidx.databinding.BindingAdapter;
import com.example.health.model.enums.AnomalyLevel;
import com.example.health.model.enums.BreathPattern;
import com.example.health.model.enums.MotionType;

public class SensorBindings {
    @BindingAdapter("motionTypeText")
    public static void setMotionTypeText(TextView textView, MotionType motionType) {
        textView.setText(motionType != null ? motionType.getDescription() : "");
    }

    @BindingAdapter("breathPatternText")
    public static void setBreathPatternText(TextView textView, BreathPattern pattern) {
        textView.setText(pattern != null ? pattern.getDescription() : "");
    }

    @BindingAdapter("anomalyLevelText")
    public static void setAnomalyLevelText(TextView textView, AnomalyLevel level) {
        textView.setText(level != null ? level.getDescription() : "");
    }

    @BindingAdapter({"anomalyLevelText", "showColor"})
    public static void setAnomalyLevelWithColor(TextView textView, AnomalyLevel level, boolean showColor) {
        if (level == null) {
            textView.setText("");
            return;
        }

        textView.setText(level.getDescription());
        if (showColor) {
            switch (level) {
                case MILD:
                    textView.setTextColor(0xFFFFA000); // 橙色
                    break;
                case MODERATE:
                    textView.setTextColor(0xFFF44336); // 红色
                    break;
                case SEVERE:
                    textView.setTextColor(0xFFD32F2F); // 深红
                    break;
                default:
                    textView.setTextColor(0xFF4CAF50); // 绿色
            }
        }
    }

    @BindingAdapter("speedText")
    public static void setSpeedText(TextView textView, Float speed) {
        if (speed != null) {
            textView.setText(String.format("%.1f km/h", speed));
        } else {
            textView.setText("-- km/h");
        }
    }

    @BindingAdapter("distanceText")
    public static void setDistanceText(TextView textView, Float distance) {
        if (distance != null) {
            if (distance < 1) {
                textView.setText(String.format("%.0f 米", distance * 1000));
            } else {
                textView.setText(String.format("%.2f 公里", distance));
            }
        } else {
            textView.setText("--");
        }
    }

    @BindingAdapter("heartRateText")
    public static void setHeartRateText(TextView textView, Integer rate) {
        textView.setText(rate != null ? rate + " bpm" : "--");
    }

    @BindingAdapter("bloodOxygenText")
    public static void setBloodOxygenText(TextView textView, Float value) {
        textView.setText(value != null ? String.format("%.1f%%", value) : "--");
    }

    @BindingAdapter("bloodPressureText")
    public static void setBloodPressureText(TextView textView, String value) {
        textView.setText(value != null ? value : "--/--");
    }

    @BindingAdapter("sleepText")
    public static void setSleepText(TextView textView, String value) {
        textView.setText(value != null ? value : "--");
    }

    @BindingAdapter("bloodSugarText")
    public static void setBloodSugarText(TextView textView, Float value) {
        textView.setText(value != null ? String.format("%.1f mmol/L", value) : "--");
    }

    @BindingAdapter("temperatureText")
    public static void setTemperatureText(TextView textView, Float value) {
        textView.setText(value != null ? String.format("%.1f ℃", value) : "--");
    }
}