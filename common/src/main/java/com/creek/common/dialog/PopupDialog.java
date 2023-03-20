package com.creek.common.dialog;

import static com.creek.common.pop.window.PopAlertDialog.getScreenHeight;
import static com.creek.common.pop.window.PopAlertDialog.getStatusBarHeight;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;


import com.creek.common.R;

/**
 * 实际使用的的弹出窗口
 * Created by CodingEnding on 2018/7/24.
 */
public class PopupDialog extends Dialog {
    private static final int DEFAULT_CONTENT_LAYOUT = R.layout.layout_dialog_default;
    private int mContentLayoutId = -1;//弹出窗体的内容layout
    private View mContentLayout;//弹出窗体的内容布局
    private int mGravity = Gravity.BOTTOM;//窗体的弹出位置
    private boolean mUseRadius = true;//是否使用圆角效果
    private int mWindowWidth = -1;//窗体宽度(px)，-1代表外界并未设置，直接使用默认设置
    private int mWindowHeight = -1;//窗体高度(px)

    private Context mContext;

    public PopupDialog(@NonNull Context context, int contentLayoutId) {
        super(context);
        mContext = context;
        this.mContentLayoutId = contentLayoutId;
        init();
    }

    public PopupDialog(@NonNull Context context, View contentView) {
        super(context);
        mContext = context;
        this.mContentLayout = contentView;
        init();
    }

    private void init() {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        if (mContentLayout != null) {//优先使用View作为内容布局
            setContentView(mContentLayout);
        } else {
            setContentView(getContentLayoutId());
        }


        configWindowBackground();//配置窗体背景
        configWindowAnimations();//配置动画效果
        initLayoutParams();
    }


    private int getContentLayoutId() {
        if (mContentLayoutId <= 0) {
            return DEFAULT_CONTENT_LAYOUT;
        }
        return mContentLayoutId;
    }


    //设置窗体的弹出位置
    protected void setWindowGravity(int gravity) {
        this.mGravity = gravity;
    }

    //是否使用圆角特性
    protected void setUseRadius(boolean useRadius) {
        this.mUseRadius = useRadius;
    }


    //设置窗体宽度(px)
    protected void setWindowWidth(int width) {
        this.mWindowWidth = width;
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = mWindowWidth;
        getWindow().setAttributes(params);
    }

    //设置窗体高度(px)
    protected void setWindowHeight(int height) {
        this.mWindowHeight = height;
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = mWindowHeight;
        getWindow().setAttributes(params);

    }

    //配置窗体布局参数
    public void initLayoutParams() {
        Window window = getWindow();

        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = mGravity;
        window.setAttributes(params);
        int w = -1, h = -1;
        if (mGravity == Gravity.LEFT || mGravity == Gravity.RIGHT) {
            w = WindowManager.LayoutParams.WRAP_CONTENT;
            h = getScreenHeight(mContext) - getStatusBarHeight(mContext);

        } else if (mGravity == Gravity.TOP || mGravity == Gravity.BOTTOM) {
            w = WindowManager.LayoutParams.MATCH_PARENT;
            h = WindowManager.LayoutParams.WRAP_CONTENT;
        } else {
            w = WindowManager.LayoutParams.WRAP_CONTENT;
            h = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        window.setLayout(w, h);
        //window.getDecorView().setPadding(0,300,0,0);
    }

    //获取宽度布局参数（取决于外界是否设置了宽度）
    private int getWidthParams(int defaultParams) {
        if (mWindowWidth >= 0) {//此时宽度已被赋值
            return mWindowWidth;
        }
        return defaultParams;
    }

    //获取高度布局参数（取决于外界是否设置了高度）
    private int getHeightParams(int defaultParams) {
        if (mWindowHeight >= 0) {//此时高度已被赋值
            return mWindowHeight;
        }
        return defaultParams;
    }

    //配置动画效果
    private void configWindowAnimations() {
        Window window = getWindow();
        switch (mGravity) {
            case Gravity.LEFT:
                window.setWindowAnimations(R.style.LeftDialogAnimation);
                break;
            case Gravity.RIGHT:
                window.setWindowAnimations(R.style.RightDialogAnimation);
                break;
            case Gravity.CENTER:
                //从中心弹出使用默认动画，不作额外处理
                break;
            case Gravity.TOP:
                window.setWindowAnimations(R.style.TopDialogAnimation);
                break;
            case Gravity.BOTTOM:
            default:
                window.setWindowAnimations(R.style.BottomDialogAnimation);
                break;
        }
    }

    //配置窗体背景
    private void configWindowBackground() {

        if (!mUseRadius) {
            return;
        }
        switch (mGravity) {
            case Gravity.LEFT:
                mContentLayout.setBackgroundResource(R.drawable.background_left);
                break;
            case Gravity.RIGHT:
                mContentLayout.setBackgroundResource(R.drawable.background_right);
                break;
            case Gravity.CENTER:
                mContentLayout.setBackgroundResource(R.drawable.background_center);
                break;
            case Gravity.TOP:
                mContentLayout.setBackgroundResource(R.drawable.background_top);
                break;
            case Gravity.BOTTOM:
            default:
                mContentLayout.setBackgroundResource(R.drawable.background_bottom);
                break;
        }
    }

}
