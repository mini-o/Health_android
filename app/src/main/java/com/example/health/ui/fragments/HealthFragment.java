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

import com.example.health.R;
import com.example.health.databinding.ActivityMainBinding;
import com.example.health.ui.viewmodel.HealthViewModel;
import com.example.health.utils.DateUtils;

public class HealthFragment extends Fragment {

    private ActivityMainBinding binding;
    private HealthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ActivityMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(HealthViewModel.class);

        // 设置绑定变量
        binding.setViewModel(viewModel);
        binding.setDateUtils(new DateUtils());

        setupObservers();
    }

    private void setupObservers() {
        // 监听导航事件
        viewModel.getNavigationEvent().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            switch (event) {
                case DAILY_DATA:
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_healthFragment_to_dailyDataFragment);
                    break;
                case TOTAL_DATA:
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_healthFragment_to_totalDataFragment);
                    break;
                case EXERCISE:
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_healthFragment_to_exerciseFragment);
                    break;
                case PROFILE:
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_healthFragment_to_profileFragment);
                    break;
            }
            viewModel.onNavigationComplete(); // 重置事件
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        // 如果需要在此处启动传感器
        if (viewModel != null) {
            viewModel.startSensors(requireActivity());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 如果需要在此处停止传感器
        if (viewModel != null) {
            viewModel.stopSensors();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}