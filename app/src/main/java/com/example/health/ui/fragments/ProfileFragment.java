package com.example.health.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.health.R;
import com.example.health.databinding.ActivityProfileBinding;
import com.example.health.model.pojo.HealthBluetoothDevice;
import com.example.health.adapter.BluetoothDeviceAdapter;
import com.example.health.ui.viewmodel.ProfileViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProfileFragment extends Fragment implements BluetoothDeviceAdapter.OnDeviceActionListener {

    private ActivityProfileBinding binding;
    private ProfileViewModel viewModel;
    private BluetoothDeviceAdapter deviceAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ActivityProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViewModel();
        setupRecyclerView();
        setupObservers();
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    private void setupRecyclerView() {
        deviceAdapter = new BluetoothDeviceAdapter((BluetoothDeviceAdapter.OnDeviceClickListener) this);
        binding.rvDevices.setAdapter(deviceAdapter);
        binding.rvDevices.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvDevices.addItemDecoration(
                new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
    }

    private void setupObservers() {
        // 观察用户资料数据
        viewModel.getUserProfile().observe(getViewLifecycleOwner(), profile -> {
            binding.setProfile(profile);
            binding.executePendingBindings();
        });

        // 观察蓝牙设备列表
        viewModel.getSavedDevices().observe(getViewLifecycleOwner(), devices -> {
            if (devices != null && !devices.isEmpty()) {
                deviceAdapter.submitList(devices);
            }
        });

        // 观察当前连接设备
        viewModel.getConnectedDevice().observe(getViewLifecycleOwner(), device -> {
            binding.bluetoothContainer.setVisibility(
                    device != null ? View.VISIBLE : View.GONE);
        });

        // 处理导航事件
        // 导航事件观察
        viewModel.getNavigationEvent().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            switch (event) {
                case EDIT_PRIVACY:
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_profileFragment_to_privacySettingsFragment);
                    break;
                case EDIT_PROFILE:
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_profileFragment_to_editProfileFragment);
                    break;
                case HEALTH_REPORT:
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_profileFragment_to_healthReportFragment);
                    break;
                case LOGIN:
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_profileFragment_to_loginFragment);
                    break;
                case HEALTH:
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_profileFragment_to_healthFragment);
                    break;
                case EXERCISE:
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_profileFragment_to_exerciseFragment);
                    break;
            }
            viewModel.onNavigationComplete(); // 重置事件
        });
    }

    @Override
    public void onConnectClick(HealthBluetoothDevice device) {
        if (device.isConnected()) {
            viewModel.disconnectDevice();
        } else {
            viewModel.connectDevice(device);
        }
    }

    @Override
    public void onDeleteClick(HealthBluetoothDevice device) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("删除设备")
                .setMessage("确定要删除 " + device.getName() + " 吗？")
                .setPositiveButton("确定", (d, w) -> viewModel.deleteDevice(device))
                .setNegativeButton("取消", null)
                .show();
    }

    private void showLogoutConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("退出登录")
                .setMessage("确定要退出当前账号吗？")
                .setPositiveButton("确定", (dialog, which) -> viewModel.logout())
                .setNegativeButton("取消", null)
                .show();
    }

    private void navigateTo(int destinationId) {
        Navigation.findNavController(binding.getRoot()).navigate(destinationId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}