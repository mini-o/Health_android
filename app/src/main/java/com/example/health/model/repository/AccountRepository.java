// AccountRepository.java
package com.example.health.model.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.health.database.HealthDatabase;
import com.example.health.database.dao.LoginHistoryDao;
import com.example.health.database.dao.UserAccountDao;
import com.example.health.database.dao.UserProfileDao;
import com.example.health.database.entity.LoginHistoryEntity;
import com.example.health.database.entity.UserAccountEntity;
import com.example.health.database.entity.UserProfileEntity;
import com.example.health.model.enums.LoginStatus;
import com.example.health.model.pojo.LoginHistory;
import com.example.health.model.pojo.UserAccount;
import com.example.health.model.pojo.UserProfile;
import com.example.health.utils.AccountUtils;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountRepository {
    private final UserAccountDao userAccountDao;
    private final LoginHistoryDao loginHistoryDao;
    private final UserProfileDao userProfileDao;
    private final ExecutorService executor;
    private final MutableLiveData<UserAccount> currentAccount = new MutableLiveData<>();

    public AccountRepository(Application application) {
        HealthDatabase db = HealthDatabase.getDatabase(application);
        this.userAccountDao = db.userAccountDao();
        this.loginHistoryDao = db.loginHistoryDao();
        this.userProfileDao = db.userProfileDao();
        this.executor = Executors.newSingleThreadExecutor();
        loadCurrentAccount();
    }

    private void loadCurrentAccount() {
        executor.execute(() -> {
            UserAccountEntity entity = userAccountDao.getCurrentUser().getValue();
            if (entity != null) {
                currentAccount.postValue(convertToPojo(entity));
            }
        });
    }

    public LiveData<UserAccount> getCurrentAccount() {
        return currentAccount;
    }

    public LiveData<UserAccountEntity> getAccountByPhone(String phoneNumber) {
        return userAccountDao.getByPhoneNumber(phoneNumber);
    }

    public void login(String phoneNumber, String password) {
        executor.execute(() -> {
            UserAccountEntity account = userAccountDao.getByPhoneNumber(phoneNumber).getValue();
            boolean success = false;

            // 记录登录历史
            LoginHistoryEntity history = new LoginHistoryEntity();
            history.setPhoneNumber(phoneNumber);
            history.setLoginTime(new Date());
            history.setDeviceInfo(AccountUtils.getDeviceInfo());
            history.setIpAddress(AccountUtils.getIpAddress());

            if (account != null && account.getPassword().equals(password)) {
                success = true;
                // 设置为当前用户
                userAccountDao.clearCurrentUser();
                userAccountDao.setCurrentUser(phoneNumber);

                // 更新登录时间
                account.setLastLoginTime(new Date());
                userAccountDao.update(account);

                currentAccount.postValue(convertToPojo(account));
            }

            history.setLoginSuccess(success);
            loginHistoryDao.insert(history);
        });
    }

    public void register(String phoneNumber, String password) {
        executor.execute(() -> {
            // 创建账户
            UserAccountEntity account = new UserAccountEntity();
            account.setPhoneNumber(phoneNumber);
            account.setPassword(password);
            account.setStatus(LoginStatus.REGISTERED);
            account.setCreatedTime(new Date());
            account.setLastLoginTime(new Date());
            account.setCurrentUser(true);

            userAccountDao.insert(account);

            // 创建用户资料
            UserProfileEntity profile = new UserProfileEntity();
            profile.setNickname("用户" + phoneNumber.substring(phoneNumber.length() - 4));
            profile.setLastUpdated(new Date());
            userProfileDao.insert(profile);

            currentAccount.postValue(convertToPojo(account));
        });
    }

    public void sendVerificationCode(String phoneNumber) {
        executor.execute(() -> {
            String code = AccountUtils.generateVerificationCode();
            Date expiryTime = new Date(System.currentTimeMillis() + 60000); // 1分钟后过期

            UserAccountEntity account = userAccountDao.getByPhoneNumber(phoneNumber).getValue();
            if (account == null) {
                account = new UserAccountEntity();
                account.setPhoneNumber(phoneNumber);
                account.setStatus(LoginStatus.UNREGISTERED);
                account.setCreatedTime(new Date());
            }

            account.setVerificationCode(code);
            account.setCodeExpiryTime(expiryTime);
            userAccountDao.insert(account);

            // 实际项目中这里应该调用短信服务发送验证码
            // SmsUtils.sendVerificationCode(phoneNumber, code);
        });
    }

    public void resetPassword(String phoneNumber, String newPassword) {
        executor.execute(() -> {
            userAccountDao.updatePassword(phoneNumber, newPassword);
        });
    }

    private UserAccount convertToPojo(UserAccountEntity entity) {
        UserAccount account = new UserAccount();
        account.setPhoneNumber(entity.getPhoneNumber());
        account.setPassword(entity.getPassword());
        account.setVerificationCode(entity.getVerificationCode());
        account.setCodeExpiryTime(entity.getCodeExpiryTime());
        account.setStatus(entity.getStatus());
        account.setLastLoginTime(entity.getLastLoginTime());
        account.setCreatedTime(entity.getCreatedTime());
        account.setCurrentUser(entity.isCurrentUser());
        return account;
    }

    private UserAccountEntity convertToEntity(UserAccount account) {
        UserAccountEntity entity = new UserAccountEntity();
        entity.setPhoneNumber(account.getPhoneNumber());
        entity.setPassword(account.getPassword());
        entity.setVerificationCode(account.getVerificationCode());
        entity.setCodeExpiryTime(account.getCodeExpiryTime());
        entity.setStatus(account.getStatus());
        entity.setLastLoginTime(account.getLastLoginTime());
        entity.setCreatedTime(account.getCreatedTime());
        entity.setCurrentUser(account.isCurrentUser());
        return entity;
    }

    private LoginHistory convertToPojo(LoginHistoryEntity entity) {
        LoginHistory history = new LoginHistory();
        history.setId(entity.getId());
        history.setPhoneNumber(entity.getPhoneNumber());
        history.setLoginTime(entity.getLoginTime());
        history.setDeviceInfo(entity.getDeviceInfo());
        history.setIpAddress(entity.getIpAddress());
        history.setLoginSuccess(entity.isLoginSuccess());
        return history;
    }

    private LoginHistoryEntity convertToEntity(LoginHistory history) {
        LoginHistoryEntity entity = new LoginHistoryEntity();
        entity.setId(history.getId());
        entity.setPhoneNumber(history.getPhoneNumber());
        entity.setLoginTime(history.getLoginTime());
        entity.setDeviceInfo(history.getDeviceInfo());
        entity.setIpAddress(history.getIpAddress());
        entity.setLoginSuccess(history.isLoginSuccess());
        return entity;
    }
}