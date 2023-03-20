package com.creek.common.loadview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;


public class LoadingLine extends View {
    private Context mContext;
    private Paint paint;
    private ValueAnimator valueA;
    private int delayTime = 800;
    private boolean initView = false;

    public LoadingLine(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public LoadingLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.parseColor("#ff808080"));
        paint.setStrokeWidth(8);

    }

    float f = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!initView) {
            start();
            initView = true;
        }
        int height = getMeasuredHeight();
        int width = getScreenWidth();
        int length = width / 20;

        if (valueA.isRunning()) {
            f = (float) valueA.getAnimatedValue();
        }
        canvas.drawLines(new float[]{yValue(f, length), height / 2, length * 4 + yValue(f, length), height / 2}, paint);
        canvas.drawLines(new float[]{yValue(f, length) - width, height / 2, length * 4 + yValue(f, length) - width, height / 2}, paint);
        if (valueA.isRunning()) {
            invalidate();
        }
    }

    private void start() {
        if (valueA == null) {
            valueA = getValueAnimator();
        } else {
            valueA.start();
        }
        invalidate();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                start();
                invalidate();
            }
        }, valueA.getDuration());
    }

    private ValueAnimator getValueAnimator() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(delayTime);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.start();
        invalidate();
        return valueAnimator;
    }

    private float yValue(float x, int length) {
        float s = length / getScreenWidth();
        float allLength = getScreenWidth();
        return allLength / 1 * x;
    }

    public int getScreenWidth() {
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    private long startTime = 0l;

    public void show() {
        startTime = System.currentTimeMillis();
        setVisibility(VISIBLE);
    }

    public void hide() {
        if (System.currentTimeMillis() - startTime > 1000) {
            setVisibility(GONE);
        } else {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setVisibility(GONE);
                }
            }, 1000);
        }

    }
}
