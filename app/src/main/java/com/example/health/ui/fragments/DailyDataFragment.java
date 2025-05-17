package com.example.health.ui.fragments;

import android.graphics.Color;
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
import com.example.health.databinding.ActivityDailyDataBinding;
import com.example.health.ui.viewmodel.HealthViewModel;
import com.example.health.utils.DateUtils;
import com.example.health.widgets.DateRangePicker;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DailyDataFragment extends Fragment {
    private ActivityDailyDataBinding binding;
    private HealthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ActivityDailyDataBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HealthViewModel.class);

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setDateUtils(new DateUtils());

        setupCharts();
        setupDatePicker();
        observeData();
    }

    private void setupCharts() {
        // 初始化图表样式
        initChart(binding.stepsChart, "步数");
        initChart(binding.breathingChart, "呼吸频率（次/分钟）");
        initChart(binding.exerciseChart, "运动时长（分钟）");
        initChart(binding.speedChart, "速度（km/h）");
    }

    private void initChart(LineChart chart, String label) {
        chart.getDescription().setEnabled(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getAxisRight().setEnabled(false);
        // 更多样式配置...
    }

    private void setupDatePicker() {
        binding.dateRangePicker.setOnDateSelectedListener(new DateRangePicker.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date selectedDate) {
                // 类型安全的日期传递
                viewModel.setSelectedDate(selectedDate);
            }
        });
    }

    private void observeData() {
        // 观察步数数据
        viewModel.getDailyStepsData().observe(getViewLifecycleOwner(), entries -> {
            setChartData(binding.stepsChart, entries, "步数");
        });

        // 观察呼吸数据
        viewModel.getDailyBreathingData().observe(getViewLifecycleOwner(), entries -> {
            setChartData(binding.breathingChart, entries, "呼吸频率");
        });

        // 观察运动时长数据
        viewModel.getDailyExerciseData().observe(getViewLifecycleOwner(), entries -> {
            setChartData(binding.exerciseChart, entries, "运动时长");
        });

        // 观察速度数据
        viewModel.getDailySpeedData().observe(getViewLifecycleOwner(), entries -> {
            setChartData(binding.speedChart, entries, "速度");
        });
        viewModel.getNavigationEvent().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            switch (event) {
                case HEALTH:
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_dailyDataFragment_to_healthFragment);
                    break;
            }
            viewModel.onNavigationComplete(); // 重置事件
        });
    }

    private void setChartData(LineChart chart, List<Entry> entries, String label) {
        if (entries == null || entries.isEmpty()) {
            chart.clear();
            return;
        }

        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.RED);

        chart.setData(new LineData(dataSet));
        chart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}