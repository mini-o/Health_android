package com.example.health.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.health.R;
import com.example.health.databinding.ItemExerciseRecordBinding;
import com.example.health.model.pojo.ExerciseRecord;
import java.util.List;

public class ExerciseRecordAdapter extends RecyclerView.Adapter<ExerciseRecordAdapter.ViewHolder> {
    private List<ExerciseRecord> records;

    public ExerciseRecordAdapter(List<ExerciseRecord> records) {
        this.records = records;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemExerciseRecordBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_exercise_record,
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseRecord record = records.get(position);
        holder.binding.setRecord(record);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return records != null ? records.size() : 0;
    }

    public void updateData(List<ExerciseRecord> newRecords) {
        this.records = newRecords;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemExerciseRecordBinding binding;

        ViewHolder(ItemExerciseRecordBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}