package com.creek.mail.home.manage;

import android.app.Activity;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.IdRes;

import com.creek.common.UserInfo;
import com.creek.common.router.Launcher;
import com.creek.mail.R;
import com.creek.mail.contact.ContactsPageActivity;
import com.creek.mail.pages.SignCardActivity;
import com.creek.mail.home.msg.EventID;
import com.creek.mail.home.msg.EventListen;
import com.creek.mail.home.msg.HomeHandler;
import com.creek.mail.pop.Popup;

public class MenuViewManager implements EventListen, View.OnClickListener {

    private Activity mActivity;
    private HomeHandler mHandler;


    public MenuViewManager(Activity activity, HomeHandler handler) {
        this.mActivity = activity;
        this.mHandler = handler;
        this.mHandler.addRegister(this);

    }

    @Override
    public boolean onMessageCome(int eventId, Message message) {
        if (eventId == EventID.View_Init) {
            initView();
        }
        return false;
    }

    private <T extends View> T findViewById(@IdRes int id) {
        return mActivity.findViewById(id);
    }

    private void initView() {
        findViewById(R.id.rr_email_title).setOnClickListener(this);
        TextView tvUser = findViewById(R.id.tv_drawer_email_account);
        tvUser.setText(UserInfo.userEmail);

        //清空邮件
        findViewById(R.id.ll_clear_mail_area).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Popup.clearMail(null, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //清空邮件中除附件以外的所有数据
                        mHandler.sendEmptyMessage(EventID.Clear_Folder_Mail);
                    }
                });
            }
        });

        //落款签名
        findViewById(R.id.ll_signature_area).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.startActivity(mActivity,SignCardActivity.class);
            }
        });

        // mail contact
        findViewById(R.id.mail_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.startActivity(mActivity,ContactsPageActivity.class);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rr_email_title) {
            mHandler.sendEmptyMessage(EventID.View_Close_Drawer);
        }
    }
}
