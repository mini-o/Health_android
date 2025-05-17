package com.example.health.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

public class AvatarImageView extends AppCompatImageView {
    private Paint paint;
    private Path path;

    public AvatarImageView(Context context) {
        super(context);
        init();
    }

    public AvatarImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AvatarImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2;

        path.reset();
        path.addCircle(width / 2f, height / 2f, radius, Path.Direction.CW);

        canvas.save();
        canvas.clipPath(path);
        super.onDraw(canvas);
        canvas.restore();

        // 绘制圆形边框
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        paint.setColor(0xFFDDDDDD);
        canvas.drawCircle(width / 2f, height / 2f, radius - 2, paint);
    }

    public void setAvatar(Bitmap bitmap) {
        setImageBitmap(bitmap);
        invalidate();
    }
}