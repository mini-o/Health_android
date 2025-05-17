package com.example.health.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.health.R;
import com.example.health.databinding.ItemBluetoothDeviceBinding;
import com.example.health.model.pojo.HealthBluetoothDevice;

import java.util.List;

public class BluetoothDeviceAdapter extends ListAdapter<HealthBluetoothDevice, BluetoothDeviceAdapter.ViewHolder> {
    private List<HealthBluetoothDevice> devices; // 修改为自定义类型
    // 添加接口定义
    public interface OnDeviceActionListener {
        void onConnectClick(HealthBluetoothDevice device);
        void onDeleteClick(HealthBluetoothDevice device);
    }

    private final OnDeviceActionListener listener;

    // 修改构造函数
    public BluetoothDeviceAdapter(OnDeviceActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private OnDeviceClickListener listener;

    public interface OnDeviceClickListener {
        void onDeviceClick(HealthBluetoothDevice device); // 修改为自定义类型
        void onDeviceLongClick(HealthBluetoothDevice device); // 修改为自定义类型
    }

    public BluetoothDeviceAdapter(OnDeviceClickListener listener){
        super(DIFF_CALLBACK);
        this.devices = devices;
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<HealthBluetoothDevice> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<HealthBluetoothDevice>() {
                @Override
                public boolean areItemsTheSame(@NonNull HealthBluetoothDevice oldItem, @NonNull HealthBluetoothDevice newItem) {
                    return oldItem.getMacAddress().equals(newItem.getMacAddress());
                }

                @Override
                public boolean areContentsTheSame(@NonNull HealthBluetoothDevice oldItem, @NonNull HealthBluetoothDevice newItem) {
                    return oldItem.equals(newItem);
                }
            };

    public void updateDevices(List<HealthBluetoothDevice> newDevices) {
        this.devices = newDevices;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBluetoothDeviceBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_bluetooth_device,
                parent,
                false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HealthBluetoothDevice device = devices.get(position); // 修改为自定义类型
        holder.binding.setDevice(device); // 类型匹配
        holder.binding.getRoot().setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeviceClick(device); // 修改为自定义类型
            }
        });
        holder.binding.getRoot().setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onDeviceLongClick(device); // 修改为自定义类型
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return devices != null ? devices.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemBluetoothDeviceBinding binding;

        ViewHolder(ItemBluetoothDeviceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
