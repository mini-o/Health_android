// DateSpinnerAdapter.java
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
import com.example.health.databinding.SpinnerDateItemBinding;
import com.example.health.utils.DateUtils;
import java.util.Date;
import java.util.List;

public class DateSpinnerAdapter extends ArrayAdapter<Date> {
    private final LayoutInflater inflater;
    private final int resourceId;
    private final List<Date> dates;

    public DateSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<Date> dates) {
        super(context, resource, dates);
        this.inflater = LayoutInflater.from(context);
        this.resourceId = resource;
        this.dates = dates;
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
        SpinnerDateItemBinding binding;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(inflater, resourceId, parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (SpinnerDateItemBinding) convertView.getTag();
        }

        Date date = getItem(position);
        if (date != null) {
            binding.setDate(DateUtils.formatDate(date));
            binding.setWeekday(DateUtils.getWeekday(date));
        }

        return convertView;
    }

    public void updateData(List<Date> newDates) {
        dates.clear();
        dates.addAll(newDates);
        notifyDataSetChanged();
    }
}