// TotalDataFragment.java
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
import com.example.health.databinding.ActivityTotalDataBinding;
import com.example.health.model.DateRange;
import com.example.health.model.enums.TimeRangeType;
import com.example.health.ui.viewmodel.HealthViewModel;
import com.example.health.widgets.CustomBarChartView;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

public class TotalDataFragment extends Fragment {
    private ActivityTotalDataBinding binding;
    private HealthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ActivityTotalDataBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(HealthViewModel.class);

        binding.setVm(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        setupTimeRangeSelector();
        setupChartConfig();
        setupObservers();
        setupDateRangePicker();
    }

    private void setupTimeRangeSelector() {
        // 初始化默认时间范围
        viewModel.setTimeRangeType(TimeRangeType.DAY);
    }

    private void setupChartConfig() {
        configureChartAppearance(binding.stepsChart);
        configureChartAppearance(binding.distanceChart);
        configureChartAppearance(binding.durationChart);
        configureChartAppearance(binding.countChart);
    }

    private void configureChartAppearance(CustomBarChartView chart) {
        chart.getDescription().setEnabled(false);
        chart.getXAxis().setGranularity(1f);
        chart.getAxisLeft().setGranularity(1f);
        chart.getAxisRight().setEnabled(false);
        chart.setFitBars(true);
    }

    private void setupObservers() {
        // 观察时间范围变化
        viewModel.getTimeRangeType().observe(getViewLifecycleOwner(), type -> {
            updateDateRangePickerVisibility(type);
            viewModel.loadSummaryData();
        });

        // 观察图表数据
        viewModel.stepsChartData.observe(getViewLifecycleOwner(),
                entries -> updateBarChart(binding.stepsChart, entries, "步数"));

        viewModel.distanceChartData.observe(getViewLifecycleOwner(),
                entries -> updateBarChart(binding.distanceChart, entries, "距离"));

        viewModel.durationChartData.observe(getViewLifecycleOwner(),
                entries -> updateBarChart(binding.durationChart, entries, "时长"));

        viewModel.countChartData.observe(getViewLifecycleOwner(),
                entries -> updateBarChart(binding.countChart, entries, "次数"));
        viewModel.getNavigationEvent().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            switch (event) {
                case HEALTH:
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_totalDataFragment_to_healthFragment);
                    break;
            }
            viewModel.onNavigationComplete(); // 重置事件
        });
    }

    private void updateBarChart(CustomBarChartView chart, List<BarEntry> entries, String label) {
        if (entries == null || entries.isEmpty()) {
            chart.clear();
            return;
        }

        BarDataSet dataSet = new BarDataSet(entries, label);
        dataSet.setColor(getColorForChart(label));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        chart.setData(barData);
        chart.invalidate();
    }

    private int getColorForChart(String label) {
        switch (label) {
            case "步数": return Color.parseColor("#4CAF50");
            case "距离": return Color.parseColor("#2196F3");
            case "时长": return Color.parseColor("#FF9800");
            case "次数": return Color.parseColor("#9C27B0");
            default: return Color.GRAY;
        }
    }

    private void setupDateRangePicker() {
        binding.dateRangePicker.setDateRangeChangeListener((start, end) -> {
            viewModel.setDateRange(start,end);
            viewModel.loadSummaryData();
        });
    }

    private void updateDateRangePickerVisibility(TimeRangeType type) {
        binding.dateRangePicker.setVisibility(
                type == TimeRangeType.DAY ? View.GONE : View.VISIBLE
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadSummaryData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}