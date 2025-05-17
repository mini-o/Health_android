package com.example.health.model.pojo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import com.example.health.utils.DateUtils;
import java.util.Date;

/**
 * 月度数据汇总（按月聚合统计）
 * 对应SQL查询：
 * SELECT strftime('%Y-%m', timestamp/1000, 'unixepoch') as month,
 *        SUM(steps) as steps,
 *        SUM(distance) as distance,
 *        SUM(exerciseTime) as duration
 * FROM HealthDataEntity
 * GROUP BY month
 */
public class MonthlySummary {
    @ColumnInfo(name = "month")
    @NonNull
    private String month; // 格式：YYYY-MM

    @ColumnInfo(name = "steps")
    private int steps;

    @ColumnInfo(name = "distance")
    private float distance;

    @ColumnInfo(name = "duration")
    private int duration;

    private int count;
    public MonthlySummary() {}

    // 全参数构造函数
    public MonthlySummary(@NonNull String hour, int steps, float distance, int duration, int count) {
        this.month = month;
        this.steps = steps;
        this.distance = distance;
        this.duration = duration;
        this.count = count;
    }

    // region 工具方法
    /**
     * 转换为日期对象（当月第一天）
     */
    public Date getMonthDate() {
        return DateUtils.parseMonthString(month);
    }

    /**
     * 获取显示用月份（格式：YYYY年M月）
     */
    public String getDisplayMonth() {
        Date d = getMonthDate();
        return DateUtils.formatDate(d, "yyyy年M月");
    }

    /**
     * 获取图表X轴标签
     */
    public String getChartLabel() {
        return DateUtils.formatDate(getMonthDate(), "MMM");
    }

    /**
     * 获取图表索引（月份序号）
     */
    public float getChartIndex() {
        return DateUtils.monthsBetween(DateUtils.getStartOfYear(getMonthDate()), getMonthDate());
    }
    // endregion

    // region Getter/Setter
    // 必须添加 getMonth() 方法
    public String getMonth() {
        return month;
    }

    // 必须添加 setter（Room 需要）
    public void setMonth(String month) {
        this.month = month;
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
        return "MonthlySummary{" +
                "month='" + month + '\'' +
                ", steps=" + steps +
                ", distance=" + distance +
                ", duration=" + duration +
                ", count=" + count +
                '}';
    }
}