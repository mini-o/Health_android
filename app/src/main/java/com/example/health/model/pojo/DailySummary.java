package com.example.health.model.pojo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import com.example.health.utils.DateUtils;
import java.util.Date;

/**
 * 每日数据汇总（按天聚合统计）
 */
public class DailySummary {
    @ColumnInfo(name = "date")
    @NonNull
    private String date; // 格式：YYYY-MM-DD

    @ColumnInfo(name = "steps")
    private int steps;

    @ColumnInfo(name = "distance")
    private float distance;

    @ColumnInfo(name = "duration")
    private int duration;

    private int count;
    public DailySummary() {}

    // 全参数构造函数
    public DailySummary(@NonNull String date, int steps, float distance, int duration, int count) {
        this.date = date;
        this.steps = steps;
        this.distance = distance;
        this.duration = duration;
        this.count = count;
    }

    // region 工具方法
    /**
     * 转换为日期对象
     */
    public Date getDate() {
        return DateUtils.parseDateString(date);
    }

    /**
     * 获取显示用日期（格式：MM/dd 周x）
     */
    public String getDisplayDate() {
        Date d = getDate();
        return DateUtils.formatDate(d) + " " + DateUtils.getWeekday(d);
    }

    /**
     * 获取图表X轴标签
     */
    public String getChartLabel() {
        return DateUtils.formatDate(getDate(), "MM/dd");
    }

    /**
     * 获取图表索引（日期序号）
     */
    public float getChartIndex() {
        return DateUtils.daysBetween(DateUtils.getStartOfMonth(getDate()), getDate());
    }
    // endregion

    // region Getter/Setter
    @NonNull
    public String getDateString() {
        return date;
    }

    public void setDateString(@NonNull String date) {
        this.date = date;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int duration) {
        this.count = count;
    }
    // endregion

    @Override
    public String toString() {
        return "DailySummary{" +
                "date='" + date + '\'' +
                ", steps=" + steps +
                ", distance=" + distance +
                ", duration=" + duration +
                ", count=" + count +
                '}';
    }
}