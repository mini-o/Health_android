// RegisterFragment.java
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
import com.example.health.databinding.ActivityRegisterBinding;
import com.example.health.ui.viewmodel.LoginViewModel;

public class RegisterFragment extends Fragment {
    private ActivityRegisterBinding binding;
    private LoginViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ActivityRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        // 绑定ViewModel
        binding.setVm(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        // 观察验证码发送状态
        viewModel.getCodeSendStatus().observe(getViewLifecycleOwner(), status -> {
            if (status != null) {
                if (status.isSuccess()) {
                    binding.getCodeButton.startCountdown(60000, 1000);
                    Toast.makeText(requireContext(), "验证码已发送", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), status.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 观察验证结果
        viewModel.getVerifyResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    showPasswordInputs();
                } else {
                    Toast.makeText(requireContext(), result.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 观察注册结果
        viewModel.getRegisterResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    navigateToLoginPage();
                    Toast.makeText(requireContext(), "注册成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), result.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 观察UI状态
        viewModel.getUiState().observe(getViewLifecycleOwner(), uiState -> {
            if (uiState == LoginViewModel.UiState.SHOW_PASSWORD_INPUTS) {
                showPasswordInputs();
            } else if (uiState == LoginViewModel.UiState.SHOW_CODE_INPUTS) {
                showCodeInputs();
            }
        });
    }

    private void showPasswordInputs() {
        binding.passwordInputs.setVisibility(View.VISIBLE);
        binding.codeInputs.setVisibility(View.GONE);
    }

    private void showCodeInputs() {
        binding.passwordInputs.setVisibility(View.GONE);
        binding.codeInputs.setVisibility(View.VISIBLE);
    }

    private void navigateToLoginPage() {
        Navigation.findNavController(binding.getRoot())
                .navigate(R.id.action_registerFragment_to_loginFragment);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (binding.getCodeButton != null) {
            outState.putLong("remainingTime", binding.getCodeButton.getRemainingTime());
        }
    }

    // 恢复状态
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && binding.getCodeButton != null) {
            long remaining = savedInstanceState.getLong("remainingTime", 0);
            if (remaining > 0) {
                binding.getCodeButton.startCountdown(remaining, 1000);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (binding.getCodeButton != null) {
            binding.getCodeButton.pauseCountdown();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding.getCodeButton != null && binding.getCodeButton.getRemainingTime() > 0) {
            binding.getCodeButton.resumeCountdown(binding.getCodeButton.getRemainingTime());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}