// DateBindings.java
package com.example.health.binding;

import android.widget.TextView;
import androidx.databinding.BindingAdapter;
import com.example.health.utils.DateUtils;
import java.util.Date;

public class DateBindings {
    @BindingAdapter("formattedDate")
    public static void setFormattedDate(TextView textView, Date date) {
        if (date != null) {
            textView.setText(DateUtils.formatDate(date));
        } else {
            textView.setText("");
        }
    }

    @BindingAdapter({"formattedDate", "showWeekday"})
    public static void setFormattedDateWithWeekday(TextView textView, Date date, boolean showWeekday) {
        if (date != null) {
            String text = DateUtils.formatDate(date);
            if (showWeekday) {
                text += " " + DateUtils.getWeekday(date);
            }
            textView.setText(text);
        } else {
            textView.setText("");
        }
    }

    @BindingAdapter("formattedTime")
    public static void setFormattedTime(TextView textView, Date date) {
        if (date != null) {
            textView.setText(DateUtils.formatDateTime(date));
        } else {
            textView.setText("");
        }
    }

    @BindingAdapter("formattedDuration")
    public static void setFormattedDuration(TextView textView, Integer minutes) {
        if (minutes != null) {
            int hours = minutes / 60;
            int mins = minutes % 60;
            textView.setText(String.format("%d小时%d分钟", hours, mins));
        } else {
            textView.setText("0分钟");
        }
    }
}