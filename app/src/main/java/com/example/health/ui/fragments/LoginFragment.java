// LoginFragment.java
package com.example.health.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.health.R;
import com.example.health.databinding.ActivityLoginBinding;
import com.example.health.ui.viewmodel.LoginViewModel;

public class LoginFragment extends Fragment {
    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ActivityLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // 绑定ViewModel
        binding.setVm(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        // 观察登录结果
        viewModel.getLoginResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    navigateToHealthPage();
                } else {
                    showLoginError(result.getErrorMessage());
                }
            }
        });

        // 观察导航事件
        viewModel.getNavigationEvent().observe(getViewLifecycleOwner(), navEvent -> {
            if (navEvent != null && navEvent == LoginViewModel.NavigationEvent.NAVIGATE_TO_REGISTER) {
                navigateToRegisterPage();
                viewModel.onNavigationComplete();
            }
        });
    }

    private void navigateToHealthPage() {
        Navigation.findNavController(binding.getRoot())
                .navigate(R.id.action_loginFragment_to_healthFragment);
    }

    private void navigateToRegisterPage() {
        Navigation.findNavController(binding.getRoot())
                .navigate(R.id.action_loginFragment_to_registerFragment);
    }

    private void showLoginError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        binding.errorText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}