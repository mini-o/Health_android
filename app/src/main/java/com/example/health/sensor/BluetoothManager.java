package com.example.health.sensor;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import androidx.core.content.ContextCompat;
import com.example.health.model.pojo.HealthBluetoothDevice;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BluetoothManager {
    private static final long SCAN_PERIOD = 10000;
    private static final UUID HEART_RATE_SERVICE = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
    private static final UUID HEART_RATE_MEASUREMENT = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
    private static final UUID BLOOD_PRESSURE_SERVICE = UUID.fromString("00001810-0000-1000-8000-00805f9b34fb");
    private static final UUID BLOOD_PRESSURE_MEASUREMENT = UUID.fromString("00002a35-0000-1000-8000-00805f9b34fb");
    private static final UUID BLOOD_OXYGEN_SERVICE = UUID.fromString("00001822-0000-1000-8000-00805f9b34fb");
    private static final UUID BLOOD_OXYGEN_MEASUREMENT = UUID.fromString("00002a5e-0000-1000-8000-00805f9b34fb");
    private static final UUID TEMPERATURE_SERVICE = UUID.fromString("00001809-0000-1000-8000-00805f9b34fb");
    private static final UUID TEMPERATURE_MEASUREMENT = UUID.fromString("00002a1c-0000-1000-8000-00805f9b34fb");
    private static final UUID GLUCOSE_SERVICE = UUID.fromString("00001808-0000-1000-8000-00805f9b34fb");
    private static final UUID GLUCOSE_MEASUREMENT = UUID.fromString("00002a18-0000-1000-8000-00805f9b34fb");
    // 根据设备实际 UUID 设置睡眠服务
    private static final UUID SLEEP_SERVICE = UUID.fromString("自定义睡眠服务UUID");
    private static final UUID SLEEP_MEASUREMENT = UUID.fromString("自定义睡眠特征UUID");

    private final Context context;
    private final BluetoothAdapter bluetoothAdapter;
    private final Handler handler;
    private final List<HealthBluetoothDevice> discoveredDevices;
    private BluetoothGatt connectedGatt;
    private HealthBluetoothDevice connectedDevice;
    private BluetoothListener listener;
    private boolean isScanning;

    public interface BluetoothListener {
        void onDeviceDiscovered(HealthBluetoothDevice device);
        void onDeviceConnected(HealthBluetoothDevice device);
        void onDeviceDisconnected(HealthBluetoothDevice device);
        void onHeartRateReceived(int heartRate);
        void onBloodOxygenReceived(float spo2);
        void onBloodPressureReceived(String systolic);
        void onTemperatureReceived(float temperature);
        void onBloodSugarReceived(float glucose);
        void onSleepDataReceived(String sleepData);
        void onError(String message);
    }

    public BluetoothManager(Context context, BluetoothListener listener) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.context = context.getApplicationContext();
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.handler = new Handler(Looper.getMainLooper());
        this.discoveredDevices = new ArrayList<>();
        this.listener = listener;
    }

    public void setListener(BluetoothListener listener) {
        this.listener = listener;
    }

    public void startDiscovery() {
        if (bluetoothAdapter == null) {
            notifyError("设备不支持蓝牙");
            return;
        }

        if (isScanning) {
            stopDiscovery();
        }

        if (!checkPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            notifyError("缺少蓝牙扫描权限");
            return;
        }

        discoveredDevices.clear();
        isScanning = true;
        try {
            bluetoothAdapter.startLeScan(leScanCallback);
            handler.postDelayed(this::stopDiscovery, SCAN_PERIOD);
        } catch (SecurityException e) {
            notifyError("蓝牙扫描权限被拒绝");
        }
    }

    public void stopDiscovery() {
        if (bluetoothAdapter != null && isScanning) {
            if (!checkPermission(Manifest.permission.BLUETOOTH_SCAN)) {
                notifyError("缺少蓝牙扫描权限");
                return;
            }
            try {
                bluetoothAdapter.stopLeScan(leScanCallback);
                isScanning = false;
            } catch (SecurityException e) {
                notifyError("蓝牙扫描权限被拒绝");
            }
        }
    }

    public void connectToDevice(HealthBluetoothDevice device) {
        if (device == null) {
            notifyError("设备不能为空");
            return;
        }

        if (!checkPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            notifyError("缺少蓝牙连接权限");
            return;
        }

        try {
            if (connectedGatt != null) {
                connectedGatt.disconnect();
                connectedGatt.close();
            }

            BluetoothDevice btDevice = bluetoothAdapter.getRemoteDevice(device.getMacAddress());
            connectedGatt = btDevice.connectGatt(context, false, gattCallback);
            connectedDevice = device;
        } catch (SecurityException e) {
            notifyError("蓝牙连接权限被拒绝");
        }
    }

    public void disconnectDevice() {
        if (connectedGatt != null) {
            if (!checkPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                notifyError("缺少蓝牙连接权限");
                return;
            }
            try {
                connectedGatt.disconnect();
            } catch (SecurityException e) {
                notifyError("蓝牙断开连接失败");
            }
        }
    }

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void notifyError(String message) {
        if (listener != null) {
            listener.onError(message);
        }
    }

    private final BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            handler.post(() -> {
                HealthBluetoothDevice newDevice = new HealthBluetoothDevice();
                newDevice.setMacAddress(device.getAddress());

                try {
                    if (checkPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                        newDevice.setName(device.getName());
                    } else {
                        newDevice.setName("未知设备");
                    }
                } catch (SecurityException e) {
                    newDevice.setName("未知设备");
                }

                boolean exists = discoveredDevices.stream()
                        .anyMatch(d -> d.getMacAddress().equals(newDevice.getMacAddress()));

                if (!exists) {
                    discoveredDevices.add(newDevice);
                    if (listener != null) {
                        listener.onDeviceDiscovered(newDevice);
                    }
                }
            });
        }
    };

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (listener == null) return;

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                try {
                    if (!checkPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                        listener.onError("缺少蓝牙连接权限");
                        return;
                    }
                    gatt.discoverServices();
                    listener.onDeviceConnected(connectedDevice);
                } catch (SecurityException e) {
                    listener.onError("蓝牙服务发现失败");
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                listener.onDeviceDisconnected(connectedDevice);
                try {
                    gatt.close();
                } catch (Exception e) {
                    // Ignore close errors
                }
                connectedGatt = null;
                connectedDevice = null;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (listener == null || status != BluetoothGatt.GATT_SUCCESS) return;

            try {
                // 启用心率通知
                setupCharacteristicNotification(gatt, HEART_RATE_SERVICE, HEART_RATE_MEASUREMENT);
                // 血压
                setupCharacteristicNotification(gatt, BLOOD_PRESSURE_SERVICE, BLOOD_PRESSURE_MEASUREMENT);
                // 血氧
                setupCharacteristicNotification(gatt, BLOOD_OXYGEN_SERVICE, BLOOD_OXYGEN_MEASUREMENT);
                // 体温
                setupCharacteristicNotification(gatt, TEMPERATURE_SERVICE, TEMPERATURE_MEASUREMENT);
                // 血糖
                setupCharacteristicNotification(gatt, GLUCOSE_SERVICE, GLUCOSE_MEASUREMENT);
                // 睡眠
                setupCharacteristicNotification(gatt, SLEEP_SERVICE, SLEEP_MEASUREMENT);
            } catch (SecurityException e) {
                listener.onError("蓝牙特性通知设置失败");
            } catch (Exception e) {
                listener.onError("服务发现异常");
            }
        }

        private void setupCharacteristicNotification(BluetoothGatt gatt, UUID serviceUuid, UUID characteristicUuid) {
            BluetoothGattService service = gatt.getService(serviceUuid);
            if (service == null) return;

            if (!checkPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                listener.onError("缺少蓝牙连接权限");
                return;
            }

            BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUuid);
            if (characteristic == null) return;

            gatt.setCharacteristicNotification(characteristic, true);
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString("00002902-0000-1000-8000-00805f9b34fb") // 标准通知描述符
            );
            if (descriptor != null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            UUID uuid = characteristic.getUuid();
            byte[] data = characteristic.getValue();

            if (uuid.equals(HEART_RATE_MEASUREMENT)) {
                int heartRate = data[1] & 0xFF; // 根据规范解析
                listener.onHeartRateReceived(heartRate);
            } else if (uuid.equals(BLOOD_PRESSURE_MEASUREMENT)) {
                // 示例解析，需按规范调整
                int systolic = (data[1] & 0xFF) | (data[2] << 8);
                int diastolic = (data[3] & 0xFF) | (data[4] << 8);
                listener.onBloodPressureReceived(systolic + "/" + diastolic);
            } else if (uuid.equals(BLOOD_OXYGEN_MEASUREMENT)) {
                float spo2 = data[1] & 0xFF; // 实际可能包含更多字节
                listener.onBloodOxygenReceived(spo2);
            } else if (uuid.equals(TEMPERATURE_MEASUREMENT)) {
                // 解析温度值，例如：单位转换
                int tempValue = (data[1] & 0xFF) | (data[2] << 8);
                float celsius = tempValue * 0.1f; // 假设单位为0.1摄氏度
                listener.onTemperatureReceived(celsius);
            } else if (uuid.equals(GLUCOSE_MEASUREMENT)) {
                // 血糖解析
                float glucose = (data[1] & 0xFF) | (data[2] << 8);
                listener.onBloodSugarReceived(glucose);
            } else if (uuid.equals(SLEEP_MEASUREMENT)) {
                String sleepStage = parseSleepData(data);
                listener.onSleepDataReceived(sleepStage);
            }
        }
    };

    private String parseSleepData(byte[] data) {
        // 根据设备协议解析睡眠阶段
        return "深睡期"; // 示例返回值
    }

    public boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public void cleanup() {
        stopDiscovery();
        if (connectedGatt != null) {
            try {
                if (checkPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                    connectedGatt.disconnect();
                }
                connectedGatt.close();
            } catch (Exception e) {
                // Ignore cleanup errors
            } finally {
                connectedGatt = null;
                connectedDevice = null;
            }
        }
    }
}