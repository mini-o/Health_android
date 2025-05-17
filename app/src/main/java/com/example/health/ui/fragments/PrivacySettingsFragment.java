package com.example.health.ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.example.health.R;
import com.example.health.databinding.ActivityEditPrivacyBinding;
import com.example.health.ui.viewmodel.ProfileViewModel;
import com.example.health.adapter.PermissionAdapter;
import java.util.Arrays;

public class PrivacySettingsFragment extends Fragment {

    private ActivityEditPrivacyBinding binding;
    private ProfileViewModel viewModel;
    private PermissionAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ActivityEditPrivacyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        binding.setViewModel(viewModel);

        setupRecyclerView();
        setupObservers();
    }

    private void setupRecyclerView() {
        adapter = new PermissionAdapter(item -> {
            if (item.granted) {
                viewModel.revokePermission(item);
            } else {
                requestPermissions(new String[]{item.permission}, item.requestCode);
            }
        });

        binding.rvPermissions.setAdapter(adapter);
        binding.rvPermissions.addItemDecoration(
                new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
    }

    private void setupObservers() {
        viewModel.getPermissionItems().observe(getViewLifecycleOwner(), items -> {
            adapter.submitList(items);
        });
        viewModel.getNavigationEvent().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            switch (event) {
                case PROFILE:
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_privacySettingsFragment_to_profileFragment);
                    break;
            }
            viewModel.onNavigationComplete(); // 重置事件
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            viewModel.updatePermissionStatus(requestCode, true);
        } else {
            viewModel.showPermissionRationale(requestCode);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}