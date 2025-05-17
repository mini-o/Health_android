package com.example.health.adapter;

import android.widget.TextView;
import androidx.databinding.BindingAdapter;

import com.example.health.utils.DateUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.Date;
import java.util.List;

public class ChartBindingAdapters {

    @BindingAdapter("lineChartData")
    public static void setLineChartData(LineChart chart, List<Entry> entries) {
        if(entries == null || entries.isEmpty()) {
            chart.clear();
            return;
        }

        LineDataSet dataSet = new LineDataSet(entries, "数据");
        dataSet.setColor(0xFF6200EE);
        dataSet.setCircleColor(0xFF6200EE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }

    @BindingAdapter("barChartData")
    public static void setBarChartData(BarChart chart, List<BarEntry> entries) {
        if(entries == null || entries.isEmpty()) {
            chart.clear();
            return;
        }

        BarDataSet dataSet = new BarDataSet(entries, "数据");
        dataSet.setColor(0xFF6200EE);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);
        chart.setData(barData);
        chart.invalidate();
    }

    @BindingAdapter("formattedDate")
    public static void setFormattedDate(TextView view, Date date) {
        view.setText(DateUtils.formatDate(date));
    }
}