package com.example.health.adapter;

import android.widget.ImageView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.databinding.BindingAdapter;

import com.example.health.R;
import com.example.health.ui.viewmodel.ProfileViewModel;

public class PermissionBindingAdapter {
    @BindingAdapter("app:permissionIcon")
    public static void setPermissionIcon(ImageView view,
                                         ProfileViewModel.PermissionItem item) {
        view.setImageResource(item != null ? item.iconRes : R.drawable.ic_default);
        view.setAlpha(item.granted ? 1.0f : 0.5f);
    }

    @BindingAdapter("app:permissionChecked")
    public static void setPermissionChecked(SwitchCompat view, boolean granted) {
        view.setChecked(granted);
        view.setTrackResource(granted ?
                R.drawable.switch_track_enabled : R.drawable.switch_track_disabled);
    }
}