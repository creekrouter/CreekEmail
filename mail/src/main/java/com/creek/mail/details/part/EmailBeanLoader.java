package com.creek.mail.details.part;

import static com.creek.mail.details.msg.EvenId.MAIL_BEAN_INIT_START;

import android.app.Activity;

import com.creek.common.CreekPath;
import com.creek.common.EmailData;
import com.creek.common.interfaces.CommonCallBack;
import com.creek.database.tools.DBTools;
import com.creek.mail.details.msg.WatchDog;
import com.creek.mail.details.msg.MsgHandler;
import com.creek.mail.details.msg.Event;
import com.creek.mail.details.msg.EvenId;
import com.creek.router.CreekRouter;
import com.creek.sync.MailSync;
import com.mail.tools.ThreadPool;
import com.libmailcore.IMAPMessage;
import com.libmailcore.IndexSet;
import com.creek.common.UserInfo;
import com.creek.database.DBManager;
import com.creek.common.MailBean;


import java.util.List;


public class EmailBeanLoader implements WatchDog {

    private Activity mActivity;
    protected MsgHandler mHandler;

    EmailData data;

    public EmailBeanLoader(Activity activity, MsgHandler handler,EmailData emailData) {
        mActivity = activity;
        mHandler = handler;
        data = emailData;
    }

    @Override
    public void onMsgCome(Event event) {
        switch (event.eventID) {
            case MAIL_BEAN_INIT_START:
                getMailBeanAsync();
                break;
        }
    }

    private void getMailBeanAsync() {
        if (data.getMail() != null && data.getMail().uid() > 0) {
            mHandler.sendEvent(EvenId.MAIL_BEAN_INIT_FINISH);
            return;
        }
        long uid = data.getUid();
        final String folder = data.getFolderName();
        // 1、先查询本地数据库是否有，若有，则读取
        // 2、本地数据库没有，则网络拉去

        IndexSet indexSet = new IndexSet();
        indexSet.addIndex(uid);
        EmailData finalData = data;
        MailSync.fetchMailList(folder, indexSet, new CommonCallBack<List<IMAPMessage>,String>() {
            @Override
            public void success(final List<IMAPMessage> messages) {
                if (messages == null || messages.size() == 0) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finalData.setMail(null);
                            mHandler.sendEvent(EvenId.MAIL_BEAN_INIT_FINISH);
                        }
                    });
                    return;
                }
                ThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        IMAPMessage imapMessage = messages.get(0);
                        DBManager.insertMailAttachment(folder, imapMessage.uid(), imapMessage.attachments());
                        final List<MailBean> tempList = DBTools.dataTransform(folder, UserInfo.userEmail, messages);
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finalData.setMail(tempList.get(0));
                                mHandler.sendEvent(EvenId.MAIL_BEAN_INIT_FINISH);
                            }
                        });
                    }
                });
            }

            @Override
            public void fail(String errorStr) {
                finalData.setMail(null);
                mHandler.sendEvent(EvenId.MAIL_BEAN_INIT_FINISH);
            }
        });
    }

}
