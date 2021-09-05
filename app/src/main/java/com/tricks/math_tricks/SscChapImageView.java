package com.tricks.math_tricks;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

public class SscChapImageView extends androidx.appcompat.widget.AppCompatImageView {
    private float radius = 10.0f;

    public SscChapImageView(Context context) {
        super(context);
    }

    public SscChapImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SscChapImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int savedState = canvas.save();
        Path path = new Path();
        path.addRoundRect(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), radius, radius, Path.Direction.CW);
        canvas.clipPath(path);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(savedState);
    }
}
