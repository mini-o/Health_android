// LoginBindings.java
package com.example.health.binding;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import com.bumptech.glide.Glide;
import com.example.health.R;
import com.example.health.widgets.CountdownButton;

public class LoginBindings {

    @BindingAdapter("app:errorText")
    public static void setErrorText(EditText view, String errorMessage) {
        if (TextUtils.isEmpty(errorMessage)) {
            view.setError(null);
        } else {
            view.setError(errorMessage);
        }
    }

    @BindingAdapter("app:countdownEnabled")
    public static void setCountdownEnabled(CountdownButton button, boolean enabled) {
        button.setEnabled(enabled);
    }

    @BindingAdapter("app:startCountdown")
    public static void startCountdown(CountdownButton button, boolean start) {
        if (start) {
            button.startCountdown(60000, 1000); // 60秒倒计时，间隔1秒
        }
    }

    @BindingAdapter("app:srcCompat")
    public static void setSrcCompat(ImageView view, Drawable drawable) {
        view.setImageDrawable(drawable);
    }

    @BindingAdapter("app:srcCompat")
    public static void setSrcCompat(ImageView view, int resId) {
        view.setImageResource(resId);
    }

    @BindingAdapter({"app:imageUrl", "app:placeholder"})
    public static void loadImage(ImageView view, String imageUrl, Drawable placeholder) {
        Glide.with(view.getContext())
                .load(imageUrl)
                .placeholder(placeholder)
                .error(R.drawable.default_avatar)
                .circleCrop()
                .into(view);
    }

    @BindingAdapter("app:visibleOrGone")
    public static void setVisibleOrGone(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("app:onTextChanged")
    public static void setTextWatcher(EditText view, final InverseBindingListener textChanged) {
        if (textChanged == null) {
            view.setOnFocusChangeListener(null);
        } else {
            view.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    textChanged.onChange();
                }
            });
        }
    }

    @InverseBindingAdapter(attribute = "app:text", event = "app:onTextChanged")
    public static String getText(EditText view) {
        return view.getText().toString();
    }
    @BindingAdapter("app:onClickClean")
    public static void setOnClickClean(ImageView view, Runnable command) {
        view.setOnClickListener(v -> {
            if (command != null) {
                command.run();
            }
        });
    }

    @BindingAdapter("app:onClickSendCode")
    public static void setOnClickSendCode(CountdownButton button, Runnable command) {
        button.setOnClickListener(v -> {
            if (command != null) {
                command.run();
            }
        });
    }
}