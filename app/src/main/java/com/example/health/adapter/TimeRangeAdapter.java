// TimeRangeAdapter.java
package com.example.health.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import com.example.health.R;
import com.example.health.databinding.SpinnerTimeRangeItemBinding;
import com.example.health.model.TimeRange;
import java.util.List;

public class TimeRangeAdapter extends ArrayAdapter<TimeRange> {
    private final LayoutInflater inflater;
    private final int resourceId;
    private final List<TimeRange> timeRanges;

    public TimeRangeAdapter(@NonNull Context context, int resource, @NonNull List<TimeRange> timeRanges) {
        super(context, resource, timeRanges);
        this.inflater = LayoutInflater.from(context);
        this.resourceId = resource;
        this.timeRanges = timeRanges;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        SpinnerTimeRangeItemBinding binding;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(inflater, resourceId, parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (SpinnerTimeRangeItemBinding) convertView.getTag();
        }

        TimeRange timeRange = getItem(position);
        if (timeRange != null) {
            binding.setTimeRange(timeRange);
        }

        return convertView;
    }

    public void updateData(List<TimeRange> newTimeRanges) {
        timeRanges.clear();
        timeRanges.addAll(newTimeRanges);
        notifyDataSetChanged();
    }
}