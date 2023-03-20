package com.creek.mail.home.inbox;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.creek.mail.R;

public class ComposeView extends LinearLayout {
    public ComposeView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ComposeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ComposeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private View mRootView;

    private void init(Context context) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.compose_email_button_layout, this);

    }
}
