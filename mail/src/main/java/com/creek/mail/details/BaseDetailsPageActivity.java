package com.creek.mail.details;

import static com.creek.mail.details.msg.EvenId.ATTACHMENT_INFO_LOAD_FINISH;
import static com.creek.mail.details.msg.EvenId.MAIL_BEAN_INIT_FINISH;
import static com.creek.mail.details.msg.EvenId.WEB_VIEW_LOAD_FINISHED;

import android.content.Intent;

import java.util.List;

import com.creek.common.EmailData;
import com.creek.mail.details.msg.WatchDog;
import com.creek.common.BaseActivity;
import com.creek.mail.details.msg.MsgHandler;
import com.creek.mail.details.msg.EvenId;
import com.creek.mail.details.msg.Event;
import com.mail.tools.MailToast;

public abstract class BaseDetailsPageActivity extends BaseActivity implements WatchDog {

    protected EmailData mData = new EmailData();
    protected MsgHandler mHandler = new MsgHandler();


    protected boolean checkIntentLegal() {
        if (parseIntent(getIntent())) {
            for (WatchDog listener : createRegister()) {
                mHandler.addWatchDog(listener);
            }
            return true;
        } else {
            MailToast.show("数据传递错误！");
            return false;
        }
    }

    private void checkMailBeanLegal() {
        if (mData.getMail() == null) {
            MailToast.show("邮件数据获取错误！");
            finish();
        } else {
            mailBeanInitFinish();
        }
    }


    @Override
    public final void onMsgCome(Event event) {
        switch (event.eventID) {
            case ATTACHMENT_INFO_LOAD_FINISH:
                attachmentInitFinish();
                break;
            case MAIL_BEAN_INIT_FINISH:
                checkMailBeanLegal();
                break;
            case WEB_VIEW_LOAD_FINISHED:
                onWebViewPageFinished();
                break;
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mHandler.sendEvent(EvenId.ACTIVITY_WINDOW_FOCUS);
        }
    }

    public abstract List<WatchDog> createRegister();

    public abstract boolean parseIntent(Intent intent);

    public abstract void initViews();

    public abstract void attachmentInitFinish();

    public abstract void mailBeanInitFinish();

    public abstract void onWebViewPageFinished();


}
