// LoginHistoryAdapter.java
package com.example.health.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.health.R;
import com.example.health.databinding.ItemLoginHistoryBinding;
import com.example.health.model.pojo.LoginHistory;
import java.util.List;

public class LoginHistoryAdapter extends RecyclerView.Adapter<LoginHistoryAdapter.ViewHolder> {
    private List<LoginHistory> historyList;

    public LoginHistoryAdapter(List<LoginHistory> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLoginHistoryBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_login_history,
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LoginHistory history = historyList.get(position);
        holder.bind(history);
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    public void updateData(List<LoginHistory> newList) {
        this.historyList = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemLoginHistoryBinding binding;

        ViewHolder(ItemLoginHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(LoginHistory history) {
            binding.setHistory(history);
            binding.executePendingBindings();
        }
    }
}