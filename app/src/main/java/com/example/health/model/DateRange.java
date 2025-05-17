// DateRange.java
package com.example.health.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import com.example.health.BR;
import com.example.health.utils.DateUtils;
import java.util.Date;

public class DateRange extends BaseObservable {
    private Date startDate;
    private Date endDate;

    public DateRange() {
        this(new Date(), new Date());
    }

    public DateRange(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Bindable
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
        notifyPropertyChanged(BR.startDate);
        notifyPropertyChanged(BR.startDateText);
    }

    @Bindable
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
        notifyPropertyChanged(BR.endDate);
        notifyPropertyChanged(BR.endDateText);
    }

    @Bindable
    public String getStartDateText() {
        return DateUtils.formatDate(startDate);
    }

    @Bindable
    public String getEndDateText() {
        return DateUtils.formatDate(endDate);
    }
}