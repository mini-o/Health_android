package com.example.health.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.health.databinding.ItemPermissionBinding;
import com.example.health.ui.viewmodel.ProfileViewModel;

// PermissionAdapter.java
public class PermissionAdapter extends
        ListAdapter<ProfileViewModel.PermissionItem, PermissionAdapter.ViewHolder> {

    public interface PermissionClickListener {
        void onPermissionClick(ProfileViewModel.PermissionItem item);
    }

    private final PermissionClickListener listener;

    public PermissionAdapter(PermissionClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPermissionBinding binding = ItemPermissionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemPermissionBinding binding;

        ViewHolder(ItemPermissionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ProfileViewModel.PermissionItem item) {
            binding.setItem(item);
            binding.getRoot().setOnClickListener(v -> listener.onPermissionClick(item));
            binding.executePendingBindings();
        }
    }

    private static final DiffUtil.ItemCallback<ProfileViewModel.PermissionItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ProfileViewModel.PermissionItem>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull ProfileViewModel.PermissionItem oldItem,
                        @NonNull ProfileViewModel.PermissionItem newItem
                ) {
                    return oldItem.permission.equals(newItem.permission);
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull ProfileViewModel.PermissionItem oldItem,
                        @NonNull ProfileViewModel.PermissionItem newItem
                ) {
                    return oldItem.granted == newItem.granted;
                }
            };
}