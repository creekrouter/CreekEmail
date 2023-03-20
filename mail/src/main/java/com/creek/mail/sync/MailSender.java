package com.creek.mail.sync;

import com.creek.common.entities.SendData;
import com.creek.common.interfaces.CommonCallBack;
import com.creek.sync.smtp.SmtpManager;
import com.libmailcore.MailException;
import com.libmailcore.OperationCallback;
import com.libmailcore.SMTPOperation;


public class MailSender {
    /**
     * 邮件发送
     *
     * @param callback
     */
    public static void send(SendData data, CommonCallBack<Void, String> callback) {

        if (!data.build()) {
            callback.fail(data.getError());
            return;
        }
        //发送
        SMTPOperation smtpOperation = SmtpManager.singleton().getSession().sendMessageOperation(data.getBuilder().data());
        smtpOperation.start(new OperationCallback() {
            @Override
            public void succeeded() {

                /*
                copy message to sent folder
                 */
//                String folder_name = DBManager.getFolderNameByImapFlag(UserInfo.userEmail,
//                        IMAPFolderFlags.IMAPFolderFlagSentMail);
//                IMAPAppendMessageOperation op = ImapManager.singleton().getSession().appendMessageOperation(folder_name,
//                        builder.data(), MessageFlag.MessageFlagSeen);
//                op.start(new OperationCallback() {
//                    @Override
//                    public void succeeded() {
//
//                    }
//
//                    @Override
//                    public void failed(MailException e) {
//
//                    }
//                });

                callback.success(null);
            }

            @Override
            public void failed(MailException e) {
                callback.fail(e.toString());
            }
        });
    }

}
