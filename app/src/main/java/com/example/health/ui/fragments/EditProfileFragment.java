package com.example.health.ui.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.health.R;
import com.example.health.databinding.ActivityEditProfileBinding;
import com.example.health.model.enums.Gender;
import com.example.health.ui.viewmodel.ProfileViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.Calendar;

public class EditProfileFragment extends Fragment {

    private ActivityEditProfileBinding binding;
    private ProfileViewModel viewModel;
    private static final int REQUEST_CAMERA = 1001;
    private static final int REQUEST_GALLERY = 1002;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ActivityEditProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        setupObservers();
    }

    private void setupObservers() {
        viewModel.getShowGenderDialog().observe(getViewLifecycleOwner(), show -> {
            if (show) showGenderDialog();
        });

        viewModel.getShowDatePicker().observe(getViewLifecycleOwner(), show -> {
            if (show) showDatePickerDialog();
        });

        viewModel.getNavigationEvent().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            switch (event) {
                case PROFILE:
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_editProfileFragment_to_profileFragment);
                    break;
            }
            viewModel.onNavigationComplete(); // 重置事件
        });
    }

    private void showGenderDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("选择性别")
                .setItems(new String[]{"男", "女"}, (dialog, which) -> {
                    viewModel.updateGender(which == 0 ? Gender.MALE : Gender.FEMALE);
                })
                .setOnDismissListener(dialog -> viewModel.onDialogDismissed())
                .show();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    viewModel.updateBirthDate(year, month, dayOfMonth);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != getActivity().RESULT_OK) return;

        if (requestCode == REQUEST_CAMERA) {
            viewModel.handleCameraResult();
        } else if (requestCode == REQUEST_GALLERY && data != null) {
            Uri uri = data.getData();
            viewModel.handleGalleryResult(data);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}