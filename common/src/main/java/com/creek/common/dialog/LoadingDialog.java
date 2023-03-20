package com.creek.common.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.creek.common.R;
import com.creek.common.view.LoadingView;


public class LoadingDialog extends Dialog implements DialogInterface.OnKeyListener {
    private boolean mCancelAble;
    private String mText;
    private LoadingView mLoading;
    private TextView mTextView;
    private boolean backPressDismiss = true;
    private Context mContext;

    public LoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    public LoadingDialog(Context context, Builder builder, int style) {
        this(context, style);
        this.mText = builder.text;
        this.mCancelAble = builder.cancelAble;
        this.setOnKeyListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading_layout);

        mTextView = findViewById(R.id.tv_loading_text);
        if (!TextUtils.isEmpty(mText)) {
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText(mText);
        }
        mLoading = findViewById(R.id.cv_loading);

        WindowManager windowManager = getWindow().getWindowManager();
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.alpha = 1f;
        attributes.width = screenWidth * 2 / 7;
        attributes.height = screenWidth * 2 / 7;
        getWindow().setAttributes(attributes);
        setCancelable(mCancelAble);
    }

    public void setBackPressDismiss(boolean backPressAble) {
        backPressDismiss = backPressAble;
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && backPressDismiss) {
            this.dismiss();
            return true;
        }
        return false;
    }

    public static class Builder {
        /**
         * 对话框是否可以取消
         */
        private boolean cancelAble;

        private String text;

        public Builder() {
        }

        public Builder(LoadingDialog dialog) {
            this.cancelAble = dialog.mCancelAble;
        }


        public Builder cancelAble(boolean cancelAble) {
            this.cancelAble = cancelAble;
            return this;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }


        public LoadingDialog build(Context context) {
            return new LoadingDialog(context, this, R.style.loading_dialog);
        }
    }

    /**
     * 更新提示文字，适用于在一个类中只初始化一个Dialog时，需要显示不同提示语的场景
     */
    public LoadingDialog updateText(String text) {
        if (!TextUtils.isEmpty(text) && mTextView != null) {
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText(text);
        }
        return this;
    }

    @Override
    public void show() {
        if (mContext != null && mContext instanceof Activity && !((Activity) mContext).isFinishing() && !isShowing()) {
            super.show();
            mLoading.startAnimation();
        }
    }

    public void dismiss() {
        if (mContext != null && mContext instanceof Activity && !((Activity) mContext).isFinishing() && isShowing()) {
            if (mLoading.isAnimationStart()) {
                mLoading.stopAnimation();
            }
            super.dismiss();
        }
    }

    /**
     * 销毁对话框时调用，主动释放内存
     */
    public void onDestroy() {
        if (mLoading != null) {
            mLoading.detachView();
        }
    }
}
