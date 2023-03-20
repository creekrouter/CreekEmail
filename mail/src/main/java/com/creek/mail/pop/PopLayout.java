package com.creek.mail.pop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.creek.common.pop.PopWindow;
import com.creek.mail.R;

public class PopLayout extends RelativeLayout {


    public PopLayout(Context context) {
        super(context);
        init(context);
    }

    private View mRootView;
    private TextView tvTile;
    private TextView tvContent;
    private TextView tvLeft;
    private TextView tvRight;
    private View vLine;
    private PopWindow mPopWindow;
    private View.OnClickListener listenerLeft, listenerRight;

    private void init(Context context) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.pop_middle_two_btn, this);

        tvTile = findViewById(R.id.compose_email_action_bar__title);
        tvContent = findViewById(R.id.tv_content);
        tvLeft = findViewById(R.id.tv_left_cc);
        tvRight = findViewById(R.id.tv_right_cc);
        vLine = findViewById(R.id.v_cc);

        tvLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listenerLeft != null) {
                    listenerLeft.onClick(v);
                }
                if (mPopWindow != null) {
                    mPopWindow.dismiss();
                }
            }
        });

        tvRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listenerRight != null) {
                    listenerRight.onClick(v);
                }
                if (mPopWindow != null) {
                    mPopWindow.dismiss();
                }
            }
        });
    }

    public PopLayout setTitle(String title) {
        tvTile.setText(title);
        return this;
    }

    public PopLayout setContent(String content) {
        tvContent.setText(content);
        tvContent.setVisibility(VISIBLE);
        return this;
    }

    public PopLayout setLeftBtn(String s) {
        tvLeft.setText(s);
        return this;
    }

    public PopLayout setRightBtn(String s) {
        tvRight.setText(s);
        return this;
    }

    public PopLayout setLeftClick(View.OnClickListener listener) {
        listenerLeft = listener;
        return this;
    }

    public PopLayout setRightClick(View.OnClickListener listener) {
        listenerRight = listener;
        return this;
    }

    public PopLayout setPopWindow(PopWindow popWindow) {
        mPopWindow = popWindow;
        return this;
    }
}
