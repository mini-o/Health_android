// CustomChartView.java
package com.example.health.widgets;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import com.example.health.utils.ChartUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class CustomChartView extends LineChart {
    public CustomChartView(@NonNull Context context) {
        super(context);
        init();
    }

    public CustomChartView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomChartView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        getDescription().setEnabled(false);
        setTouchEnabled(true);
        setDragEnabled(true);
        setScaleEnabled(true);
        setPinchZoom(true);
        setDrawGridBackground(false);
        setHighlightPerDragEnabled(true);
    }

    @BindingAdapter("chartData")
    public static void setChartData(CustomChartView chart, List<Entry> entries) {
        if (entries == null || entries.isEmpty()) {
            chart.clear();
            return;
        }

        LineDataSet dataSet = new LineDataSet(entries, "数据");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(0xFF6200EE);
        dataSet.setCircleColor(0xFF6200EE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(3f);
        dataSet.setFillAlpha(65);
        dataSet.setFillColor(0xFF6200EE);
        dataSet.setHighLightColor(0xFF03DAC5);
        dataSet.setValueTextColor(0xFF000000);
        dataSet.setValueTextSize(9f);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        LineData lineData = new LineData(dataSets);
        chart.setData(lineData);
        chart.invalidate();
    }

    @BindingAdapter("chartXFormatter")
    public static void setXAxisFormatter(CustomChartView chart, String formatType) {
        if ("day".equals(formatType)) {
            ChartUtils.setupXAxisForDay(chart.getXAxis());
        } else if ("week".equals(formatType)) {
            ChartUtils.setupXAxisForWeek(chart.getXAxis());
        } else if ("month".equals(formatType)) {
            ChartUtils.setupXAxisForMonth(chart.getXAxis());
        } else if ("year".equals(formatType)) {
            ChartUtils.setupXAxisForYear(chart.getXAxis());
        }
    }
}