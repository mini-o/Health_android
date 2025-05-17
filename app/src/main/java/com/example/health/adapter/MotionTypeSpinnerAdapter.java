// MotionTypeSpinnerAdapter.java
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
import com.example.health.databinding.SpinnerMotionTypeItemBinding;
import com.example.health.model.enums.MotionType;
import java.util.List;

public class MotionTypeSpinnerAdapter extends ArrayAdapter<MotionType> {
    private final LayoutInflater inflater;
    private final int resourceId;
    private final List<MotionType> motionTypes;

    public MotionTypeSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<MotionType> motionTypes) {
        super(context, resource, motionTypes);
        this.inflater = LayoutInflater.from(context);
        this.resourceId = resource;
        this.motionTypes = motionTypes;
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
        SpinnerMotionTypeItemBinding binding;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(inflater, resourceId, parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (SpinnerMotionTypeItemBinding) convertView.getTag();
        }

        MotionType motionType = getItem(position);
        if (motionType != null) {
            binding.setMotionType(motionType);
        }

        return convertView;
    }

    public void updateData(List<MotionType> newMotionTypes) {
        motionTypes.clear();
        motionTypes.addAll(newMotionTypes);
        notifyDataSetChanged();
    }
}