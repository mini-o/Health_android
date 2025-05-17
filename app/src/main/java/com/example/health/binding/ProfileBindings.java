package com.example.health.binding;

import android.graphics.Bitmap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;

import com.bumptech.glide.Glide;
import com.example.health.R;
import com.example.health.model.enums.Gender;
import com.example.health.model.pojo.UserProfile;
import com.example.health.utils.ImageUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProfileBindings {
    @BindingAdapter("avatar")
    public static void setAvatar(ImageView view, Bitmap avatar) {
        if (avatar != null) {
            Glide.with(view.getContext())
                    .load(avatar)
                    .circleCrop()
                    .into(view);
        }
    }

    @BindingAdapter("genderText")
    public static void setGenderText(ImageView view, UserProfile profile) {
        if (profile != null && profile.getGender() != null) {
            switch (profile.getGender()) {
                case MALE:
                    view.setImageResource(R.drawable.ic_male);
                    break;
                default:
                    view.setImageResource(R.drawable.ic_female);
            }
        }
    }

    @BindingAdapter("ageText")
    public static void setAgeText(TextView textView, Date birthDate) {
        if (birthDate == null) {
            textView.setText("");
            return;
        }

        // 计算年龄
        Calendar dob = Calendar.getInstance();
        dob.setTime(birthDate);
        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        textView.setText(String.format(Locale.getDefault(), "%d岁", age));
    }

    @BindingAdapter("app:genderText")
    public static void setGenderText(TextView textView, Gender gender) {
        textView.setText(gender != null ? gender.getDescription() : "未设置");
    }

    @InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
    public static float getFloatValue(EditText view) {
        try {
            return Float.parseFloat(view.getText().toString());
        } catch (NumberFormatException e) {
            return 0f;
        }
    }

    @BindingAdapter("android:text")
    public static void setFloatValue(EditText view, float value) {
        if (value == 0f) {
            view.setText("");
        } else {
            view.setText(String.format(Locale.getDefault(), "%.1f", value));
        }
    }
}