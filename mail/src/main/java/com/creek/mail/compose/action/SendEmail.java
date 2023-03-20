package com.creek.mail.compose.action;

import static com.creek.mail.compose.Compose.TYPE_DRAFTS;
import static com.creek.mail.compose.Compose.TYPE_FORWARD;
import static com.creek.mail.compose.Compose.TYPE_REPLY;

import com.creek.common.MailAttachment;
import com.creek.common.MailContact;
import com.creek.common.constant.Const;
import com.creek.common.constant.ConstPath;
import com.creek.common.entities.SendData;
import com.creek.common.interfaces.CommonCallBack;
import com.creek.mail.compose.manager.DataManager;
import com.creek.common.Attach;
import com.creek.mail.sync.MailSender;
import com.creek.mail.compose.autocompleteview.ContactsCompletionView;
import com.creek.sync.MailSync;
import com.libmailcore.Attachment;

import java.util.ArrayList;
import java.util.List;

public class SendEmail {

    public static void sendEmail(DataManager manager, CommonCallBack<Void, String> callBack) {

        //检测附加下载情况
        if (!checkAttachment(manager)) {
//            MailToast.show("附件正在下载中");
            callBack.fail("附件正在下载中");
            return;
        }

        final List<String> toAddressList = new ArrayList<>();  //收件人
        boolean errorTo = setMailContactList(toAddressList, manager.toList);

        final List<String> ccAddressList = new ArrayList<>();  //抄送
        boolean errorCc = setMailContactList(ccAddressList, manager.ccList);

        final List<String> bccAddressList = new ArrayList<>();  //密送
        boolean errorBcc = setMailContactList(bccAddressList, manager.bccList);

        if (!errorTo || !errorCc || !errorBcc || toAddressList.size() == 0) {
            //邮件格式不正确
            callBack.fail("收件人地址错误或收件人为空！");
            return;
        }

        final String subject = manager.mSubjectText;        //主题
        final String body = manager.mSendHtml;        //正文


        List<Attach> attaches = getMailAttachments(manager.mAttachmentList);

        List<Attachment> attachments = new ArrayList<>();

        //转发、回复时需要把邮件内容带过来
        if (manager.mFrom == TYPE_REPLY || manager.mFrom == TYPE_FORWARD || manager.mFrom == TYPE_DRAFTS) {
            for (Attachment attachment : manager.mMail.inlineAttachments) {
                attachments.add(attachment);
            }

        }

        SendData data = new SendData();
        data.toAddressList.addAll(toAddressList);
        data.ccAddressList.addAll(ccAddressList);
        data.bccAddressList.addAll(bccAddressList);
        data.setSubject(subject);
        data.setBodyStr(body);
        data.attachList.addAll(attaches);
        data.inlineAttachments.addAll(attachments);

        MailSender.send(data, new CommonCallBack<Void, String>() {
            @Override
            public void success(Void unused) {
                callBack.success(null);
                if (manager.mFrom == TYPE_DRAFTS && manager.mMail != null) {  //如果来自草稿箱，发送成功后删除该邮件
                    MailSync.messageDeleted(manager.mMail, new CommonCallBack<Void, String>() {
                        @Override
                        public void success(Void unused) {

                        }

                        @Override
                        public void fail(String message) {

                        }
                    });
                } else if (manager.mFrom == TYPE_REPLY && manager.mMail != null) {
//                    mailActionSender.sendOperateAction(ComposeActivity.this, MailOperate.Operate.mark_replay, mMail);
                }

            }

            @Override
            public void fail(final String errorStr) {
                callBack.fail(errorStr);
            }
        });

    }


    private static boolean setMailContactList(List<String> list, List<MailContact> contactsList) {
        for (MailContact contact : contactsList) {
            if (ContactsCompletionView.isEmail(contact.getEmail_addr())) {
                list.add(contact.getEmail_addr());
            } else {
                return false;
            }
        }
        return true;

    }

    private static List<Attach> getMailAttachments(List<MailAttachment> attachList) {
        List<Attach> attachments = new ArrayList<>();
        for (MailAttachment attach : attachList) {
            Attach attachment = new Attach();
            attachment.name = attach.getFile_name();
            if (attach.getFile_path() != null) {
                attachment.path = attach.getFile_path();
                attachments.add(attachment);
            }
        }
        return attachments;
    }

    private static boolean checkAttachment(DataManager manager) {
        if (manager.mMail==null){
            return true;
        }
        int count = manager.mMail.getAttachmentsCount();
        int downloadFinishedCount = 0;
        for (MailAttachment attachment : manager.mAttachmentList) {
            if (attachment.getDownload_state() == Const.ATAACHMENTS_DOWNLOAD_STATE_FIINSHED) {
                downloadFinishedCount++;
            }
        }
        return downloadFinishedCount == count;
    }
}
