package com.example.health.utils;

import android.content.Context;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.widget.Toast;
import com.example.health.model.enums.AnomalyLevel;

public class AlertUtils {
    private static final String EMERGENCY_PHONE = "紧急联系电话";

    public static void handleBreathingAlert(Context context, AnomalyLevel level) {
        switch (level) {
            case MILD:
                showToast(context, "呼吸轻度异常，请注意调整呼吸节奏");
                break;
            case MODERATE:
                sendSMS(context, "检测到中度呼吸异常，请关注健康状况");
                break;
            case SEVERE:
                triggerVibration(context);
                sendSMS(context, "检测到严重呼吸异常，需要立即关注！");
                break;
        }
    }

    private static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    private static void sendSMS(Context context, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(EMERGENCY_PHONE, null, message, null, null);
        } catch (Exception e) {
            Toast.makeText(context, "短信发送失败", Toast.LENGTH_SHORT).show();
        }
    }

    private static void triggerVibration(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if(vibrator != null) {
            long[] pattern = {0, 1000, 500, 1000, 500, 1000};
            vibrator.vibrate(pattern, 0);
        }
    }
}