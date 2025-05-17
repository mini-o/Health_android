// ExerciseHistoryFragment.java
package com.example.health.ui.fragments;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.health.R;
import com.example.health.databinding.ActivityExerciseHistoryBinding;
import com.example.health.ui.viewmodel.ExerciseViewModel;

public class ExerciseHistoryFragment extends Fragment {

    private ActivityExerciseHistoryBinding binding;
    private ExerciseViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ExerciseViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = ActivityExerciseHistoryBinding.bind(view);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        setupObservers();
    }

    private void setupObservers() {
        // 处理返回导航
        viewModel.getNavigationEvent().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            switch (event) {
                case EXERCISE:
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_exerciseHistoryFragment_to_exerciseFragment);
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