package com.example.health.widgets;

import android.content.Context;
import android.util.AttributeSet;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.example.health.model.enums.TimeRangeType;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CustomBarChartView extends BarChart {
    private TimeRangeType currentType = TimeRangeType.DAY;
    private final SimpleDateFormat dayFormat = new SimpleDateFormat("HH:00", Locale.getDefault());
    private final SimpleDateFormat weekFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
    private final SimpleDateFormat monthFormat = new SimpleDateFormat("dd", Locale.getDefault());
    private final SimpleDateFormat yearFormat = new SimpleDateFormat("MMM", Locale.getDefault());

    public CustomBarChartView(Context context) {
        super(context);
        initChart();
    }

    public CustomBarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initChart();
    }

    public CustomBarChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initChart();
    }

    private void initChart() {
        setFitBars(true);
        getDescription().setEnabled(false);
        setDrawGridBackground(false);
        setPinchZoom(false);
        setDoubleTapToZoomEnabled(false);

        // X轴配置
        XAxis xAxis = getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        // Y轴配置
        getAxisRight().setEnabled(false);
        getAxisLeft().setGranularity(1f);
    }

    // 核心方法：设置时间粒度类型
    public void setTimeRangeType(TimeRangeType type) {
        this.currentType = type;
        configureXAxis();
    }

    private void configureXAxis() {
        getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return formatXValue(value);
            }
        });

        // 根据粒度调整标签数量
        switch (currentType) {
            case DAY:
                getXAxis().setLabelCount(24, true);
                break;
            case WEEK:
                getXAxis().setLabelCount(7, true);
                break;
            case MONTH:
                getXAxis().setLabelCount(31, false);
                break;
            case YEAR:
                getXAxis().setLabelCount(12, true);
                break;
        }
    }

    private String formatXValue(float value) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());

        switch (currentType) {
            case DAY:
                cal.set(Calendar.HOUR_OF_DAY, (int) value);
                return dayFormat.format(cal.getTime());

            case WEEK:
                cal.add(Calendar.DAY_OF_YEAR, (int) value - 7);
                return weekFormat.format(cal.getTime());

            case MONTH:
                cal.set(Calendar.DAY_OF_MONTH, (int) value);
                return monthFormat.format(cal.getTime());

            case YEAR:
                cal.set(Calendar.MONTH, (int) value);
                return yearFormat.format(cal.getTime());
        }
        return "";
    }

    // 配置柱状图样式
    public void configureChartStyle(int color) {
        if (getData() != null && getData().getDataSetCount() > 0) {
            BarDataSet set = (BarDataSet) getData().getDataSetByIndex(0);
            set.setColor(color);
        }
    }

    // 处理日期范围更新
    public void updateDateRange(Date startDate, Date endDate) {
        // 这里可以添加日期范围验证逻辑
        invalidate();
    }

    // 显示数值提示
    public void enableValueHighlight() {
        setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                showValuePopup(e.getY(), (int) e.getX());
            }

            @Override
            public void onNothingSelected() {
                // 处理无选择状态
            }
        });
    }

    private void showValuePopup(float value, int position) {
        String formattedValue = String.format(Locale.getDefault(), "%.0f", value);
        String label = formatXValue(position) + ": " + formattedValue;
        // 这里可以替换为自定义Toast或对话框
        android.widget.Toast.makeText(getContext(), label, android.widget.Toast.LENGTH_SHORT).show();
    }
}