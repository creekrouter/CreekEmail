package com.creek.mail.pop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.creek.common.MailBean;
import com.creek.common.interfaces.ConfirmCallBack;
import com.creek.common.pop.PopWindow;
import com.creek.mail.R;
import com.creek.mail.details.msg.EvenId;

public class PopMoreAction extends RelativeLayout implements View.OnClickListener {
    public PopMoreAction(Context context) {
        super(context);
        init(context);
    }

    private View vReplyAll, vReply, vForward, vFlag, vRead, vDelete, vMove;
    private TextView tvFlag, tvRead;
    private ConfirmCallBack<Integer> callBack;
    private PopWindow mPopWindow;

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.dialog_mail_operate_layout, this);

        vReplyAll = findViewById(R.id.pop_more_action_replay_all);
        vReply = findViewById(R.id.pop_more_action_replay);
        vForward = findViewById(R.id.pop_more_action_forward);
        vFlag = findViewById(R.id.pop_more_action_flag);
        vRead = findViewById(R.id.pop_more_action_read);
        vDelete = findViewById(R.id.pop_more_action_delete);
        vMove = findViewById(R.id.pop_more_action_move);

        vReplyAll.setOnClickListener(this);
        vReply.setOnClickListener(this);
        vForward.setOnClickListener(this);
        vFlag.setOnClickListener(this);
        vRead.setOnClickListener(this);
        vDelete.setOnClickListener(this);
        vMove.setOnClickListener(this);

        vReplyAll.setTag(EvenId.MAIL_REPLAY_ALL);
        vReply.setTag(EvenId.MAIL_REPLAY_SENDER);
        vForward.setTag(EvenId.POP_WINDOW_FORWARD);
        vFlag.setTag(EvenId.MAIL_TOGGLE_RED_FLAG);
        vRead.setTag(EvenId.MAIL_TOGGLE_READ);
        vDelete.setTag(EvenId.MAIL_DELETE);
        vMove.setTag(EvenId.MAIL_MOVE);

        tvFlag = findViewById(R.id.pop_more_action_flag_tv);
        tvRead = findViewById(R.id.pop_more_action_read_tv);

    }

    public void setData(MailBean mailBean) {
        String read = mailBean.isSeenMail() ? "标为未读" : "标为已读";
        tvRead.setText(read);

        String flag = mailBean.isRedFlagMail() ? "取消红旗" : "标为红旗";
        tvFlag.setText(flag);
    }

    public void setOnClick(ConfirmCallBack<Integer> listener, PopWindow popWindow) {
        callBack = listener;
        mPopWindow = popWindow;
    }

    @Override
    public void onClick(View v) {
        if (callBack != null) {
            callBack.onConfirm((Integer) v.getTag());
        }
        if (mPopWindow != null) {
            mPopWindow.dismiss();
        }
    }
}
