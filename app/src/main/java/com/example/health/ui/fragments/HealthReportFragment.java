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
import com.example.health.databinding.ActivityHealthReportBinding;
import com.example.health.model.pojo.HealthReport;
import com.example.health.ui.viewmodel.ProfileViewModel;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HealthReportFragment extends Fragment {

    private ActivityHealthReportBinding binding;
    private ProfileViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ActivityHealthReportBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        viewModel.getNavigationEvent().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            switch (event) {
                case PROFILE:
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_healthReportFragment_to_profileFragment);
                    break;
            }
            viewModel.onNavigationComplete(); // 重置事件
        });
        setupChart();
        observeData();
    }

    private void setupChart() {
        binding.radarChart.getDescription().setEnabled(false);
        binding.radarChart.setWebLineWidth(1f);
        binding.radarChart.setWebColor(Color.LTGRAY);
        binding.radarChart.setWebLineWidthInner(1f);
        binding.radarChart.setWebColorInner(Color.LTGRAY);
        binding.radarChart.setWebAlpha(100);
        binding.radarChart.getYAxis().setAxisMinimum(0f);
        binding.radarChart.getYAxis().setAxisMaximum(100f);

        // 设置标签格式
        binding.radarChart.getXAxis().setValueFormatter(new ValueFormatter() {
            private final String[] LABELS = {"BMI", "心肺", "运动", "睡眠", "压力"};

            @Override
            public String getFormattedValue(float value) {
                return LABELS[(int) value % LABELS.length];
            }
        });
    }

    private void observeData() {
        viewModel.getHealthReport().observe(getViewLifecycleOwner(), report -> {
            if (report != null) {
                updateChartData(report);
            }
        });
    }

    private void updateChartData(HealthReport report) {
        List<RadarEntry> entries = Arrays.asList(
                new RadarEntry(report.getBmiScore()),
                new RadarEntry(report.getCardioScore()),
                new RadarEntry(report.getExerciseScore()),
                new RadarEntry(report.getSleepScore()),
                new RadarEntry(report.getStressScore())
        );

        RadarDataSet dataSet = new RadarDataSet(entries, "健康指标");
        dataSet.setColor(Color.parseColor("#4CAF50"));
        dataSet.setFillColor(Color.parseColor("#81C784"));
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(100);
        dataSet.setLineWidth(2f);
        dataSet.setDrawHighlightCircleEnabled(true);

        RadarData radarData = new RadarData(dataSet);
        radarData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.0f", value);
            }
        });
        radarData.setValueTextSize(10f);

        binding.radarChart.setData(radarData);
        binding.radarChart.animateXY(800, 800, Easing.EaseInOutQuad);
        binding.radarChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.radarChart.clear();
        binding = null;
    }
}