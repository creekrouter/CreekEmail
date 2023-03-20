package com.creek.mail.compose.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.creek.mail.R;
import com.creek.mail.compose.msg.ComposeHandler;
import com.creek.mail.compose.msg.EventID;

public class WriteMailTitleBar extends RelativeLayout implements View.OnClickListener {
    public WriteMailTitleBar(Context context) {
        super(context);
        initView(context);
    }

    public WriteMailTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private View mRootView;

    private TextView mTitle;
    private ProgressBar mProgressBar;
    private ImageView mBack, mSend;

    private void initView(Context context) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.compose_email_action_bar_title, this);

        mTitle = mRootView.findViewById(R.id.compose_email_action_bar__title);
        mProgressBar = mRootView.findViewById(R.id.compose_email_action_bar_progress);
        mBack = mRootView.findViewById(R.id.compose_email_action_bar_back);
        mSend = mRootView.findViewById(R.id.compose_email_action_bar_send);
//        mSend.setEnabled(false);

        mBack.setOnClickListener(this);
        mSend.setOnClickListener(this);
    }

    private ComposeHandler mHandler;

    public void setHandler(ComposeHandler handler) {
        mHandler = handler;
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setProgressShow() {
        mProgressBar.setVisibility(VISIBLE);
    }

    public void setProgressHide() {
        mProgressBar.setVisibility(GONE);
    }

    @Override
    public void onClick(View v) {
        if (mHandler == null) {
            return;
        }
        int id = v.getId();
        if (id == R.id.compose_email_action_bar_back) {
            mHandler.sendEvent(EventID.Compose_Common_Event_BackPressed);
        } else if (id == R.id.compose_email_action_bar_send) {
            mHandler.sendEvent(EventID.Compose_Mail_Action_Send);
        }

    }
}
