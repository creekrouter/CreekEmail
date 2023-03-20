package com.creek.mail.login;

import android.animation.ValueAnimator;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.mail.tools.ToolInput;

public class AnimatorHandler extends Handler {
    public static final int Key_Board_Show = 0;
    public static final int Key_Board_Hide = 1;

    private ValueAnimator animatorShow, animatorHide;
    private View mView;

    public AnimatorHandler(int h, int w, View view) {
        super(Looper.getMainLooper());

        mView = view;
        animatorShow = ValueAnimator.ofFloat(1, 0);
        animatorShow.setDuration(400).setInterpolator(new DecelerateInterpolator());
        animatorShow.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = (int) (h * animatedValue);
                layoutParams.width = (int) (w * animatedValue);
                view.requestLayout();
                view.setAlpha(animatedValue);
            }
        });

        animatorHide = ValueAnimator.ofFloat(0, 1);
        animatorHide.setDuration(400).setInterpolator(new DecelerateInterpolator());
        animatorHide.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = (int) (h * animatedValue);
                layoutParams.width = (int) (w * animatedValue);
                view.requestLayout();
                view.setAlpha(animatedValue);
            }
        });

    }

    private void hide() {
        if (animatorHide.isRunning()) {
            animatorHide.cancel();
        }
        if (animatorShow.isRunning()) {
            animatorShow.cancel();
        }
        animatorHide.start();
    }

    private void show() {
        if (animatorHide.isRunning()) {
            animatorHide.cancel();
        }
        if (animatorShow.isRunning()) {
            animatorShow.cancel();
        }
        animatorShow.start();
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        if (msg.what == Key_Board_Show) {
            hide();
        } else if (msg.what == Key_Board_Hide) {
            show();
        }
    }

    public void keyBoardShow() {
        this.removeMessages(Key_Board_Show);
//        this.removeMessages(Key_Board_Hide);
        sendEmptyMessageDelayed(Key_Board_Hide, 100);
    }

    public void keyBoardHide() {
//        this.removeMessages(Key_Board_Show);
        this.removeMessages(Key_Board_Hide);
        sendEmptyMessageDelayed(Key_Board_Show, 100);
    }
}
