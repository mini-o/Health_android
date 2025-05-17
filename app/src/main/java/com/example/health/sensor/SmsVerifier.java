// SmsVerifier.java
package com.example.health.sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import androidx.lifecycle.MutableLiveData;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsVerifier {
    private static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private final Context context;
    private final MutableLiveData<String> verificationCode = new MutableLiveData<>();
    private BroadcastReceiver smsReceiver;

    public SmsVerifier(Context context) {
        this.context = context.getApplicationContext();
    }

    public void startListening() {
        if (smsReceiver != null) return;

        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (SMS_RECEIVED_ACTION.equals(intent.getAction())) {
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        if (pdus != null) {
                            for (Object pdu : pdus) {
                                SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);
                                String content = message.getMessageBody();
                                parseVerificationCode(content);
                            }
                        }
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter(SMS_RECEIVED_ACTION);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        context.registerReceiver(smsReceiver, filter);
    }

    public void stopListening() {
        if (smsReceiver != null) {
            context.unregisterReceiver(smsReceiver);
            smsReceiver = null;
        }
    }

    private void parseVerificationCode(String smsContent) {
        Pattern pattern = Pattern.compile("(\\d{6})");
        Matcher matcher = pattern.matcher(smsContent);
        if (matcher.find()) {
            verificationCode.postValue(matcher.group(0));
        }
    }

    public MutableLiveData<String> getVerificationCode() {
        return verificationCode;
    }
}