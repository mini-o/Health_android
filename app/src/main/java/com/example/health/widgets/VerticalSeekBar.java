// VerticalSeekBar.java
package com.example.health.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

public class VerticalSeekBar extends AppCompatSeekBar {
    private InverseBindingListener progressAttrChanged;

    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.rotate(-90);
        canvas.translate(-getHeight(), 0);
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                int progress = getMax() - (int) (getMax() * event.getY() / getHeight());
                setProgress(progress);
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                if (progressAttrChanged != null) {
                    progressAttrChanged.onChange();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    @BindingAdapter("progress")
    public static void setProgress(VerticalSeekBar view, int progress) {
        if (view.getProgress() != progress) {
            view.setProgress(progress);
        }
    }

    @InverseBindingAdapter(attribute = "progress", event = "progressAttrChanged")
    public static int getProgress(VerticalSeekBar view) {
        return view.getProgress();
    }

    @BindingAdapter("progressAttrChanged")
    public static void setProgressAttrChanged(
            VerticalSeekBar view,
            final InverseBindingListener progressAttrChanged
    ) {
        view.progressAttrChanged = progressAttrChanged;
    }
}