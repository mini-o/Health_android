// TimeRange.java
package com.example.health.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import com.example.health.BR;

public class TimeRange extends BaseObservable {
    public static final int TYPE_DAY = 0;
    public static final int TYPE_WEEK = 1;
    public static final int TYPE_MONTH = 2;
    public static final int TYPE_YEAR = 3;

    private final int type;
    private final String displayName;

    public TimeRange(int type, String displayName) {
        this.type = type;
        this.displayName = displayName;
    }

    @Bindable
    public int getType() {
        return type;
    }

    @Bindable
    public String getDisplayName() {
        return displayName;
    }
}