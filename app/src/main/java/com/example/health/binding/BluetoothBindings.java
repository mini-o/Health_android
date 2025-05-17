package com.example.health.binding;

import android.widget.ImageView;
import androidx.databinding.BindingAdapter;

import com.example.health.R;
import com.example.health.model.pojo.HealthBluetoothDevice;

public class BluetoothBindings {
    @BindingAdapter("connectionStatus")
    public static void setConnectionStatus(ImageView view, HealthBluetoothDevice device) {
        if (device != null) {
            view.setImageResource(device.isConnected() ?
                    R.drawable.ic_bluetooth_connected : R.drawable.ic_bluetooth_disconnected);
        }
    }

    @BindingAdapter("deviceTypeIcon")
    public static void setDeviceTypeIcon(ImageView view, HealthBluetoothDevice device) {
        if (device != null) {
            int resId = R.drawable.ic_device_unknown;
            if (device.isCanReadHeartRate()) {
                resId = R.drawable.ic_device_heart_rate;
            } else if (device.isCanReadBloodPressure()) {
                resId = R.drawable.ic_device_blood_pressure;
            }
            view.setImageResource(resId);
        }
    }
}