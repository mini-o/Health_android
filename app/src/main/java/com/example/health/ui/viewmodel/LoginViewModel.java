// LoginViewModel.java
package com.example.health.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.health.model.Result;
import com.example.health.model.repository.AccountRepository;
import com.example.health.utils.AccountUtils;

public class LoginViewModel extends ViewModel {
    public enum NavigationEvent {
        NAVIGATE_TO_REGISTER, NAVIGATE_TO_HEALTH, NAVIGATE_TO_LOGIN
    }

    public enum UiState {
        SHOW_CODE_INPUTS,
        SHOW_PASSWORD_INPUTS
    }

    // 数据绑定字段
    public final MutableLiveData<String> mName = new MutableLiveData<>("");
    public final MutableLiveData<String> mPassword = new MutableLiveData<>("");
    public final MutableLiveData<String> mCode = new MutableLiveData<>("");
    public final MutableLiveData<String> mConfirmPassword = new MutableLiveData<>("");

    // 状态LiveData
    private final MutableLiveData<Result<Void>> loginResult = new MutableLiveData<>();
    private final MutableLiveData<NavigationEvent> navigationEvent = new MutableLiveData<>();
    private final MutableLiveData<UiState> uiState = new MutableLiveData<>(UiState.SHOW_CODE_INPUTS);
    private final MutableLiveData<Result<Void>> codeSendStatus = new MutableLiveData<>();
    private final MutableLiveData<Result<Void>> verifyResult = new MutableLiveData<>();
    private final MutableLiveData<Result<Void>> registerResult = new MutableLiveData<>();

    private final AccountRepository accountRepository;

    public LoginViewModel(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // 清除手机号
    public void cmdCleanName() {
        mName.setValue("");
    }

    // 清除密码
    public void cmdCleanPassword() {
        mPassword.setValue("");
    }

    // 清除密码
    public void cmdCleanConfirmPassword() {
        mConfirmPassword.setValue("");
    }

    public boolean isPasswordMatch() {
        return mPassword.getValue() != null &&
                mPassword.getValue().equals(mConfirmPassword.getValue());
    }

    // 登录命令
    public void cmdLogin() {
        String phone = mName.getValue();
        String password = mPassword.getValue();

        if (!AccountUtils.isValidPhoneNumber(phone)) {
            loginResult.setValue(Result.error("请输入正确的手机号"));
            return;
        }

        if (!AccountUtils.isValidPassword(password)) {
            loginResult.setValue(Result.error("密码长度应为6-20位"));
            return;
        }

        accountRepository.login(phone, password);
        navigateToHealth();
    }

    // 导航到注册页面
    public void navigateToRegister() {
        navigationEvent.setValue(NavigationEvent.NAVIGATE_TO_REGISTER);
    }

    public void navigateToHealth() {
        navigationEvent.setValue(NavigationEvent.NAVIGATE_TO_HEALTH);
    }

    public void navigateToLogin() {
        navigationEvent.setValue(NavigationEvent.NAVIGATE_TO_LOGIN);
    }

    // 发送验证码
    public void sendVerificationCode() {
        String phone = mName.getValue();
        if (!AccountUtils.isValidPhoneNumber(phone)) {
            codeSendStatus.setValue(Result.error("请输入正确的手机号"));
            return;
        }

        accountRepository.sendVerificationCode(phone);
        codeSendStatus.setValue(Result.success(null));
    }

    // 验证验证码
    public void verifyCode(String code) {
        // 实际项目中应该验证验证码是否正确
        if (code.length() == 6) {
            verifyResult.setValue(Result.success(null));
            uiState.setValue(UiState.SHOW_PASSWORD_INPUTS);
        } else {
            verifyResult.setValue(Result.error("验证码错误"));
        }
    }

    // 注册或重置密码
    public void registerOrResetPassword(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            registerResult.setValue(Result.error("两次输入的密码不一致"));
            return;
        }

        if (!AccountUtils.isValidPassword(password)) {
            registerResult.setValue(Result.error("密码长度应为6-20位"));
            return;
        }

        String phone = mName.getValue();
        accountRepository.register(phone, password);
        registerResult.setValue(Result.success(null));
        navigateToLogin();
    }

    // 完成导航
    public void onNavigationComplete() {
        navigationEvent.setValue(null);
    }

    // Getter方法
    public LiveData<Result<Void>> getLoginResult() {
        return loginResult;
    }

    public LiveData<NavigationEvent> getNavigationEvent() {
        return navigationEvent;
    }

    public LiveData<UiState> getUiState() {
        return uiState;
    }

    public LiveData<Result<Void>> getCodeSendStatus() {
        return codeSendStatus;
    }

    public LiveData<Result<Void>> getVerifyResult() {
        return verifyResult;
    }

    public LiveData<Result<Void>> getRegisterResult() {
        return registerResult;
    }
}