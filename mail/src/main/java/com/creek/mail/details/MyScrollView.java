package com.creek.mail.details;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/*
 * ScrollView并没有实现滚动监听，所以我们必须自行实现对ScrollView的监听，
 * 我们很自然的想到在onTouchEvent()方法中实现对滚动Y轴进行监听
 * ScrollView的滚动Y值进行监听
 *
 * 用在mail的详情界面，和webview一起使用
 */
public class MyScrollView extends ScrollView {
    private OnScrollListener listener;

    public void setOnScrollListener(OnScrollListener listener) {
        this.listener = listener;
    }

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface OnScrollListener {
        void onScroll(int scrollY, int oldscrollY, boolean isUp);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (oldt > t) {// 向下
            if (listener != null) {
                listener.onScroll(t, oldt, false);
            }
        } else if (oldt < t) {// 向上
            if (listener != null) {
                listener.onScroll(t, oldt, true);
            }
        }

    }


    boolean isTwoFinger = false;
    float oldY,newY,oldX,newX;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()& MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_POINTER_DOWN:
                isTwoFinger = true;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                isTwoFinger = false;
                break;
            case MotionEvent.ACTION_DOWN:
                oldY = ev.getY();
                newY = oldY;
                oldX = ev.getX();
                newX = oldX;
                break;
            case MotionEvent.ACTION_MOVE:
                oldY = newY;
                newY = ev.getY();
                oldX = newX;
                newX = ev.getX();
                break;
        }
        if (isTwoFinger){
            return false;
        }
        if (this.getScrollY()==0 && newY-oldY>0){
            return false;
        }
        if (newY-oldY<5 || oldY-newY<5){
            if (oldX-newX>5 || newX-oldX>5){
                return false;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

}
