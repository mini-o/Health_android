// ChartBindings.java
package com.example.health.binding;

import android.widget.TextView;
import androidx.databinding.BindingAdapter;

import com.example.health.model.pojo.DailySummary;
import com.example.health.model.pojo.HealthData;
import com.example.health.utils.DateUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChartBindings {
    @BindingAdapter("lineChartData")
    public static void setLineChartData(LineChart chart, List<HealthData> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            chart.clear();
            return;
        }

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            HealthData data = dataList.get(i);
            entries.add(new Entry(
                    data.getTimestamp().getTime(),
                    data.getSteps()
            ));
        }

        LineDataSet dataSet = new LineDataSet(entries, "步数");
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
    public static void setBarChartData(BarChart chart, List<DailySummary> summaries) {
        if (summaries == null || summaries.isEmpty()) {
            chart.clear();
            return;
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < summaries.size(); i++) {
            DailySummary summary = summaries.get(i);
            entries.add(new BarEntry(i, summary.getSteps()));
            labels.add(DateUtils.formatDate(summary.getDate()));
        }

        BarDataSet dataSet = new BarDataSet(entries, "步数统计");
        dataSet.setColor(0xFF6200EE);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        chart.setData(barData);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.invalidate();
    }

    @BindingAdapter("dateText")
    public static void setDateText(TextView view, Date date) {
        view.setText(DateUtils.formatDate(date));
    }

    @BindingAdapter("timeText")
    public static void setTimeText(TextView view, Date date) {
        view.setText(DateUtils.formatDateTime(date));
    }
}