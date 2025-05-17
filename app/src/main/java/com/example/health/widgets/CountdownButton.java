// CountdownButton.java
package com.example.health.widgets;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatButton;

public class CountdownButton extends AppCompatButton {
    private CountDownTimer timer;
    private String originalText;
    private long remainingMillis = 0; // 新增：记录剩余时间

    public CountdownButton(Context context) {
        super(context);
    }

    public CountdownButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CountdownButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // 添加暂停和恢复方法
    public void pauseCountdown() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void resumeCountdown(long remainingMillis) {
        startCountdown(remainingMillis, 1000);
    }

    // 添加获取剩余时间方法
    public long getRemainingTime() {
        return remainingMillis;
    }

    public void startCountdown(long millisInFuture, long countDownInterval) {
        if (timer != null) {
            timer.cancel();
        }

        setEnabled(false);
        originalText = getText().toString();
        remainingMillis = millisInFuture; // 初始化剩余时间

        timer = new CountDownTimer(millisInFuture, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingMillis = millisUntilFinished; // 更新剩余时间
                setText(millisUntilFinished / 1000 + "s后重新获取");
            }

            @Override
            public void onFinish() {
                remainingMillis = 0; // 重置剩余时间
                setEnabled(true);
                setText(originalText);
            }
        }.start();
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}