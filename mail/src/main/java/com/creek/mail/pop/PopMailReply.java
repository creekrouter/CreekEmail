package com.creek.mail.pop;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.creek.common.ContactEntity;
import com.creek.common.MailBean;
import com.creek.common.constant.Const;
import com.creek.common.dialog.PopupLayout;
import com.creek.common.interfaces.EventCallBack;
import com.creek.mail.R;
import com.creek.common.view.CircleIconView;
import com.mail.tools.ToolInput;

import java.util.ArrayList;
import java.util.List;

public class PopMailReply extends RelativeLayout implements View.OnClickListener, TextWatcher {
    public PopMailReply(Context context) {
        super(context);
        init(context);
    }

    private CircleIconView header;
    private TextView tvSend, fsTv, ccTv;
    private EditText etInput;
    private ImageView imageView;
    private EventCallBack<Integer, String> mCallBack;
    private PopupLayout mPopupLayout;


    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.mail_quick_reply_layout, this);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        fsTv = findViewById(R.id.send_mail_to);
        ccTv = findViewById(R.id.cc_send_mail_to);
        header = findViewById(R.id.email_replay_header_icon);
        etInput = findViewById(R.id.et_replay_popuwindow);
        imageView = findViewById(R.id.mail_iv_replay_tag_ect);
        tvSend = findViewById(R.id.tv_send_replay_popuwindows);
        tvSend.setOnClickListener(this);
        imageView.setOnClickListener(this);
        etInput.addTextChangedListener(this);

        tvSend.setEnabled(false);

    }

    public void setData(String quickReplyText, MailBean mailBean, PopupLayout popupLayout) {
        etInput.setText(quickReplyText);
        mPopupLayout = popupLayout;
        List<ContactEntity> contactList = ContactEntity.getContactList(mailBean);
        List<ContactEntity> fsContacts = new ArrayList<>();
        List<ContactEntity> ccContacts = new ArrayList<>();
        for (ContactEntity contactEntity : contactList) {
            int type = contactEntity.getType();
            if (type == Const.MAIL_SEND) {
                fsContacts.add(contactEntity);
            } else if (type == Const.MAIL_CC) {
                ccContacts.add(contactEntity);
            }
        }
        fsTv.setText("发至　" + getReplayText(fsContacts));
        String ccStr = getReplayText(ccContacts);
        if (ccStr != null && ccStr.length() > 0) {
            ccTv.setText("抄送　" + ccStr);
        } else {
            ccTv.setVisibility(View.GONE);
        }

        if (mailBean.getDisplayName() == null || mailBean.getDisplayName().length() == 0) {
            header.setName(mailBean.getSend_email());
        } else {
            header.setName(mailBean.getDisplayName());
        }

    }

    public void setCallBack(EventCallBack<Integer, String> callBack) {
        mCallBack = callBack;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(s) || s.equals("\n")) {
            tvSend.setEnabled(false);
            tvSend.setTextColor(Color.parseColor("#B8B8B8"));
        } else {
            tvSend.setEnabled(true);
            tvSend.setTextColor(Color.parseColor("#FFFFFF"));
            if (mCallBack != null) {
                mCallBack.onEvent(0, etInput.getText().toString());
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.mail_iv_replay_tag_ect) {
//            goToWriteActivity();
            if (mCallBack != null) {
                mCallBack.onEvent(1, etInput.getText().toString());
            }
            mPopupLayout.dismiss();
        } else if (id == R.id.tv_send_replay_popuwindows) {
//            sendEmail();
            if (mCallBack != null) {
                mCallBack.onEvent(2, etInput.getText().toString());
            }
        }
    }

    public void requestInputFocus() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                etInput.requestFocus();
                ToolInput.show(getContext(), etInput);
            }
        }, 500);
    }

    private String getReplayText(List<ContactEntity> contacts) {
        String addressStr = "";
        int count = 0;
        if (contacts.size() == 1) {
            return contacts.get(0).getName();
        }
        for (int i = 0; i < contacts.size(); i++) {
            int type = contacts.get(i).getType();
            if (type == Const.MAIL_SEND || type == Const.MAIL_CC) {
                count++;
                if (contacts.size() - 1 == i) {
                    addressStr += contacts.get(i).getName();
                } else {
                    addressStr += contacts.get(i).getName() + ",";
                }
            }
        }
        if (addressStr.length() > 27) {
            addressStr = addressStr.substring(0, 20) + "...等" + count + "人";

        }

        if (addressStr.lastIndexOf(",") == addressStr.length() - 1) {//去掉多余的逗号
            return addressStr.split(",", addressStr.length() - 1)[0];
        }
        return addressStr;
    }
}
