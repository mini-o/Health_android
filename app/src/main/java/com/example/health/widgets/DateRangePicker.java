// DateRangePicker.java
package com.example.health.widgets;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.InverseBindingListener;
import androidx.databinding.InverseBindingMethod;
import androidx.databinding.InverseBindingMethods;
import com.example.health.R;
import com.example.health.databinding.WidgetDateRangePickerBinding;
import com.example.health.model.DateRange;
import com.example.health.utils.DateUtils;

import java.util.Calendar;
import java.util.Date;

@InverseBindingMethods({
        @InverseBindingMethod(type = DateRangePicker.class, attribute = "dateRange"),
})
public class DateRangePicker extends ConstraintLayout {
    private WidgetDateRangePickerBinding binding;
    private DateRange dateRange;
    private InverseBindingListener dateRangeAttrChanged;

    public DateRangePicker(@NonNull Context context) {
        super(context);
        init(context);
    }

    public DateRangePicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DateRangePicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.widget_date_range_picker,
                this,
                true
        );

        binding.startDate.setOnClickListener(v -> showDatePicker(true));
        binding.endDate.setOnClickListener(v -> showDatePicker(false));
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRange dateRange) {
        if (this.dateRange != dateRange) {
            this.dateRange = dateRange;
            updateDisplay();
            if (dateRangeAttrChanged != null) {
                dateRangeAttrChanged.onChange();
            }
        }
    }

    public void setDateRangeAttrChanged(InverseBindingListener listener) {
        this.dateRangeAttrChanged = listener;
    }

    private void updateDisplay() {
        if (dateRange != null) {
            binding.startDateText.setText(DateUtils.formatDate(dateRange.getStartDate()));
            binding.endDateText.setText(DateUtils.formatDate(dateRange.getEndDate()));
        } else {
            binding.startDateText.setText("");
            binding.endDateText.setText("");
        }
    }

    private void showDatePicker(boolean isStartDate) {
        // 获取当前选中的日期（开始或结束）
        Date currentDate = isStartDate ? dateRange.getStartDate() : dateRange.getEndDate();

        // 如果日期为空，使用当前日期作为默认值
        if (currentDate == null) {
            currentDate = new Date();
        }

        // 从Date对象中提取年、月、日信息
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // 创建DatePickerDialog实例
        DatePickerDialog dialog = new DatePickerDialog(
                getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // 创建选择的日期
                    Calendar selectedCal = Calendar.getInstance();
                    selectedCal.set(selectedYear, selectedMonth, selectedDay);
                    Date selectedDate = selectedCal.getTime();

                    // 更新日期范围
                    if (isStartDate) {
                        dateRange.setStartDate(selectedDate);
                    } else {
                        dateRange.setEndDate(selectedDate);
                    }
                    setDateRange(dateRange);
                },
                year,    // 初始年
                month,   // 初始月
                day      // 初始日
        );

        dialog.show();
    }
    // 定义新的监听器接口
    public interface OnDateSelectedListener {
        void onDateSelected(Date selectedDate);
    }

    private Date currentDate;
    private OnDateSelectedListener listener;

    // 添加设置监听器的方法
    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.listener = listener;
    }

    public void updateSelectedDate(Date newDate) {
        this.currentDate = newDate;
        if (listener != null) {
            listener.onDateSelected(newDate);
        }
    }

    // 在内部日期变化时触发回调
    private void handleDateSelection(Date selectedDate) {
        if (listener != null) {
            listener.onDateSelected(selectedDate);
        }
    }

    // 定义日期范围变化监听接口
    public interface OnDateRangeChangeListener {
        void onDateRangeChanged(Date startDate, Date endDate);
    }

    private OnDateRangeChangeListener listener_1;

    // 暴露设置监听器的方法
    public void setDateRangeChangeListener(OnDateRangeChangeListener listener) {
        this.listener_1 = listener_1;
    }

    // 内部触发日期范围变化的示例方法
    private void notifyDateRangeChanged(Date start, Date end) {
        if (listener_1 != null) {
            listener_1.onDateRangeChanged(start, end);
        }
    }

}