// SmsUtils.java
package com.example.health.utils;

import android.content.Context;
import android.telephony.SmsManager;
import java.util.ArrayList;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class SmsUtils {
    private static final String EMERGENCY_NUMBER = "120"; // 急救号码

    public static void sendEmergencySms(Context context, String message) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                != PERMISSION_GRANTED) {
            Toast.makeText(context, "无短信发送权限", Toast.LENGTH_SHORT).show();
            return;
        }

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(
                EMERGENCY_NUMBER,
                null,
                "[健康警报] " + message,
                null,
                null
        );
    }
    public static void sendVerificationCode(Context context, String phoneNumber, String code) {
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> parts = smsManager.divideMessage("您的验证码是：" + code + "，有效期5分钟");
        smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
    }

}