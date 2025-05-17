// DeviceInfoCollector.java
package com.example.health.sensor;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import java.util.Locale;

public class DeviceInfoCollector {
    public static String getDeviceModel() {
        return Build.BRAND + " " + Build.MODEL;
    }

    public static String getOSVersion() {
        return "Android " + Build.VERSION.RELEASE;
    }

    public static String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getNetworkOperator(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null ? tm.getNetworkOperatorName() : "unknown";
    }
}