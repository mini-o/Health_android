// ExerciseFragment.java
package com.example.health.ui.fragments;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.health.R;
import com.example.health.databinding.ActivityExerciseBinding;
import com.example.health.ui.viewmodel.ExerciseViewModel;

public class ExerciseFragment extends Fragment {

    private ActivityExerciseBinding binding;
    private ExerciseViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = ActivityExerciseBinding.bind(view);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        setupObservers();
    }

    private void setupObservers() {
        // 处理导航事件
        viewModel.getNavigationEvent().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            switch (event) {
                case EXERCISE_RECORD:
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_exerciseFragment_to_exerciseHistoryFragment);
                    break;
                case RUNNING:
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_exerciseFragment_to_outdoorExerciseFragment);
                    break;
                case HEALTH:
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_exerciseFragment_to_healthFragment);
                    break;
                case PROFILE:
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_exerciseFragment_to_profileFragment);
                    break;
            }
            viewModel.onNavigationComplete(); // 重置事件
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}