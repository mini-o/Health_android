// AccountUtils.java
package com.example.health.utils;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AccountUtils {
    public static String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    public static String getDeviceInfo() {
        return Build.BRAND + " " + Build.MODEL + " (" + Build.VERSION.RELEASE + ")";
    }

    public static String getIpAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':') < 0;
                        if (isIPv4) {
                            return sAddr;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("^1[3-9]\\d{9}$");
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6 && password.length() <= 20;
    }
}