package com.creek.mail.compose.manager;

import static com.creek.mail.compose.Compose.TYPE_DRAFTS;
import static com.creek.mail.compose.Compose.TYPE_FORWARD;
import static com.creek.mail.compose.Compose.TYPE_MAILTO;
import static com.creek.mail.compose.Compose.TYPE_REPLY;
import static com.creek.mail.compose.Compose.TYPE_WRITE_MAIL;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import com.creek.common.MailAttachment;
import com.creek.common.MailBean;
import com.creek.common.MailContact;
import com.creek.common.constant.ConstPath;
import com.creek.common.interfaces.CommonCallBack;
import com.creek.database.DBManager;
import com.creek.mail.compose.msg.ComposeHandler;
import com.creek.mail.compose.msg.EventID;
import com.creek.mail.compose.msg.EventWatcher;
import com.creek.common.ContactEntity;
import com.creek.mail.sync.Fetcher;
import com.creek.mail.sync.MailBody;
import com.creek.mail.sync.MailSign;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataManager implements EventWatcher {

    private ComposeHandler mHandler;
    private Intent mIntent;
    public int mFrom = -1;
    public MailBean mMail = null;

    public List<MailContact> toList = new ArrayList<>();
    public List<MailContact> ccList = new ArrayList<>();
    public List<MailContact> bccList = new ArrayList<>();

    public String mSubjectText, mTitle;
    public String mContentHtml = MailSign.get();
    public String mSendHtml;

    public List<MailAttachment> mAttachmentList = new ArrayList<>();
//    public final List<Attachment> inlineAttachments = new ArrayList<>();


    public DataManager(Activity activity, ComposeHandler handler) {
        mHandler = handler;
        mHandler.addRegister(this);
        mIntent = activity.getIntent();
    }

    public void parseIntent() {
        mFrom = mIntent.getIntExtra("type", TYPE_WRITE_MAIL);
        mMail = (MailBean) mIntent.getSerializableExtra("mail");
        parseFrom();
        parseMail();

    }

    private void parseFrom() {

        if (mFrom == TYPE_REPLY) {
            //判断是否是回复全部
            boolean replayAll = mIntent.getBooleanExtra("replayAll", true);
            if (replayAll) {
                this.toList.addAll(ContactEntity.getToList(mMail));
                this.ccList.addAll(ContactEntity.getCcList(mMail));
            } else {
                this.toList.add(ContactEntity.getSender(mMail));
            }

            mSubjectText = "回复:" + mMail.getSubject();
            mTitle = "回复邮件";

            //有附件，则自动下载
            boolean hasAttachment = mIntent.getBooleanExtra("attachment", false);
            if (hasAttachment) {
                getAttachment(mMail);
            }
        } else if (mFrom == TYPE_FORWARD) {
            mTitle = "转发邮件";
            mSubjectText = "转发:" + mMail.getSubject();

            boolean hasAttachment = mIntent.getBooleanExtra("attachment", false);
            if (hasAttachment) {
                getAttachment(mMail);
            }

        } else if (mFrom == TYPE_DRAFTS) {
            mTitle = "写邮件";
            mSubjectText = mMail.getSubject();

            mHandler.sendEvent(EventID.Compose_View_ActionBar_Progress_Show);
            this.toList.addAll(ContactEntity.getToList(mMail));
            this.ccList.addAll(ContactEntity.getCcList(mMail));

            MailBody.fetchHtml(mMail, new CommonCallBack<String, String>() {
                @Override
                public void success(String html) {
                    mContentHtml = html;
                    mHandler.sendEvent(EventID.Compose_Mail_Refresh_All_View);
                }

                @Override
                public void fail(String s) {

                }
            });
        }else if (mFrom==TYPE_MAILTO){
            mTitle = "写邮件";
            this.toList.addAll(ContactEntity.getCcList(mMail));
        }

    }

    private void getAttachment(final MailBean mail_info) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<MailAttachment> atachList = DBManager.
                        selectMailAttachmentByUID(mail_info.uid());

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (atachList.size() > 0) {
                            mAttachmentList.addAll(atachList);
                        }
                        //标记下载已完成的文件
                        flagDownLoadFinishedFile(mAttachmentList);
                        if (mAttachmentList.size() > 0) {
                            mHandler.sendEvent(EventID.Compose_Mail_Refresh_Attachment_List);
                        }
                    }
                });
            }


        }).start();

    }

    private void flagDownLoadFinishedFile(List<MailAttachment> mailAttachments) {
        for (MailAttachment attach : mailAttachments) {
            if (!TextUtils.isEmpty(attach.getFile_path()) && new File(attach.getFile_path()).exists()) {
                attach.setDownloadStateFinished();
            } else {
                downLoadAttachments(attach);
            }
            attach.setEncryption(true);
        }
    }

    private void downLoadAttachments(MailAttachment attachment) {
        //下载未完成 显示文件进度条
        attachment.setDownloadStateStart();
        mHandler.sendEvent(EventID.Compose_Mail_Notify_Attachment_List);

        Fetcher.download(attachment, new CommonCallBack<MailAttachment, String>() {
            @Override
            public void success(MailAttachment attach) {
                attachment.setDownloadStateFinished();
                mHandler.sendEvent(EventID.Compose_Mail_Notify_Attachment_List);
            }

            @Override
            public void fail(String message) {

            }
        });
    }


    private void parseMail() {
        if (mFrom == TYPE_WRITE_MAIL || mMail == null) {
            return;
        }
        MailBody.readHtml(ConstPath.getMailBody_TagBody(mMail), new CommonCallBack<String, String>() {
            @Override
            public void success(String html) {
                mContentHtml = html;
                String[] receiverMails = mMail.getReceiver_email().split(";");
                String[] ccMails = mMail.getCc_email().split(";");
                String start = "<a  href=\"mailto:";
                String middle = "\">";
                String end = "</a>&nbsp;&nbsp;";
                String result = "";
                for (int i = 0; i < receiverMails.length; i++) {
                    result += start + receiverMails[i] + middle + receiverMails[i] + end;
                }
                String ccResult = "";
                for (int i = 0; i < ccMails.length; i++) {
                    if (ccMails[i] != null && ccMails[i].trim().length() > 0) {
                        ccResult += start + ccMails[i] + middle + ccMails[i] + end;
                    }
                }
                if (ccResult.length() > 0) {
                    ccResult = "<div><b>抄送:</b>&nbsp; " + ccResult + "</div>";
                }
                String fromAndToInfo = "<div><br /> <div style=\"padding-bottom: 20px;\"> <div style=\"background-color:#eee\">"
                        + "<div><b>发件人:</b>&nbsp; <a  href=\"mailto:" + mMail.getSend_email() + "\">" + mMail.getSend_email() + "</a></div>"
                        + "<div><b>发送时间:</b>&nbsp;" + mMail.getSend_time() + "</div>"
                        + "<div><b>收件人:</b>&nbsp; "
                        + result
                        + "</div>"
                        + ccResult
                        + "<div><b>主题:</b>&nbsp; <i>" + mMail.getSubject() + "</i></div>"
                        + "</div></div>";
                String wordFromRepalay = mIntent.getStringExtra("wordFromRepalay");
                if (!TextUtils.isEmpty(wordFromRepalay)) {
                    mContentHtml = wordFromRepalay + "<br/>" + MailSign.get() + "<br/>"
                            + fromAndToInfo + "<br/>" + mContentHtml;
                } else {
                    mContentHtml = MailSign.get() + "<br/>" + fromAndToInfo + "<br/>" + mContentHtml;
                }
                mHandler.sendEvent(EventID.Compose_Mail_Refresh_All_View);
            }

            @Override
            public void fail(String s) {
                mContentHtml = MailSign.get();
                mHandler.sendEvent(EventID.Compose_Mail_Refresh_All_View);
            }
        });

    }



    @Override
    public boolean onEventHappen(int eventId, Message message) {
        return false;
    }
}
