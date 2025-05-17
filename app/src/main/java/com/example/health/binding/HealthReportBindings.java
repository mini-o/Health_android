package com.example.health.binding;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.widget.ProgressBar;

import androidx.databinding.BindingAdapter;

import com.example.health.model.pojo.HealthReport;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class HealthReportBindings {

    @BindingAdapter("app:radarChartData")
    public static void loadRadarChartData(RadarChart chart, HealthReport report) {
        if (report == null) return;

        List<RadarEntry> entries = new ArrayList<>();
        entries.add(new RadarEntry(report.getBmiScore()));
        entries.add(new RadarEntry(report.getCardioScore()));
        entries.add(new RadarEntry(report.getExerciseScore()));
        entries.add(new RadarEntry(report.getSleepScore()));
        entries.add(new RadarEntry(report.getNutritionScore()));

        RadarDataSet dataSet = new RadarDataSet(entries, "健康指标");
        dataSet.setColor(Color.parseColor("#4CAF50"));
        dataSet.setFillColor(Color.parseColor("#81C784"));
        dataSet.setDrawFilled(true);

        RadarData radarData = new RadarData(dataSet);
        radarData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        chart.setData(radarData);
        chart.invalidate();
    }

    @BindingAdapter("app:healthScoreProgress")
    public static void setScoreProgress(ProgressBar progressBar, Integer score) {
        if (score != null) {
            progressBar.setProgress(score);
            progressBar.setProgressTintList(ColorStateList.valueOf(
                    score > 80 ? Color.GREEN :
                            score > 60 ? Color.YELLOW : Color.RED
            ));
        }
    }
}
