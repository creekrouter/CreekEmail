package com.creek.mail.sync;

import static com.libmailcore.IMAPFolderFlags.IMAPFolderFlagDrafts;

import android.text.TextUtils;

import com.creek.common.interfaces.CommonCallBack;
import com.creek.database.DBManager;
import com.mail.tools.FileTool;
import com.creek.sync.imap.ImapManager;
import com.libmailcore.Address;
import com.libmailcore.Attachment;
import com.libmailcore.IMAPAppendMessageOperation;
import com.libmailcore.MailException;
import com.libmailcore.MessageBuilder;
import com.libmailcore.MessageFlag;
import com.libmailcore.OperationCallback;

import java.util.ArrayList;
import java.util.List;

public class MailDraft {

    /**
     * 保存到草稿箱
     *
     * @param fromAddress 发件人地址
     * @param toAddress   收件人地址
     * @param bccAddress  密送地址
     * @param ccAddress   抄送地址
     * @param subject     主题
     * @param body        正文
     * @param fileName    附件名称
     * @param filePath    附件绝对路径
     * @param callback
     */
    public static void save(String fromAddress, List<String> toAddress, List<String> bccAddress, List<String> ccAddress,
                            final String subject, String body, List<String> fileName, List<String> filePath, CommonCallBack<Void, String> callback) {
        MessageBuilder builder = new MessageBuilder();

        if (TextUtils.isEmpty(fromAddress)) {
            callback.fail("发件人邮箱不能为空");
        } else {
            builder.header().setFrom(Address.addressWithMailbox(fromAddress));  //发件人
        }

        if (toAddress == null || toAddress.size() == 0) {
            //callback.fail("收件人邮箱不能为空");
        } else {
            List<Address> toAddressList = new ArrayList<>();
            for (int i = 0; i < toAddress.size(); i++) {
                toAddressList.add(Address.addressWithMailbox(toAddress.get(i)));
            }
            builder.header().setTo(toAddressList);  //收件人
        }

        if (bccAddress != null && bccAddress.size() > 0) {
            List<Address> bccAddressList = new ArrayList<>();
            for (int i = 0; i < bccAddress.size(); i++) {
                bccAddressList.add(Address.addressWithMailbox(bccAddress.get(i)));
            }
            builder.header().setBcc(bccAddressList);      //密送
        }

        if (ccAddress != null && ccAddress.size() > 0) {
            List<Address> ccAddressList = new ArrayList<>();
            for (int i = 0; i < ccAddress.size(); i++) {
                ccAddressList.add(Address.addressWithMailbox(ccAddress.get(i)));
            }
            builder.header().setCc(ccAddressList);      //抄送
        }

        if (!TextUtils.isEmpty(subject)) {
            builder.header().setSubject(subject);     //主题
        }

        if (!TextUtils.isEmpty(body)) {
            //builder.setTextBody(body);        //正文
            builder.setHTMLBody(body);
        }

        List<Attachment> attachmentList = new ArrayList<>();
        if (fileName != null && fileName.size() > 0 && filePath != null && filePath.size() > 0) {
            for (int i = 0; i < filePath.size(); i++) {
                String s = filePath.get(i);
                if (s != null) {
                    attachmentList.add(Attachment.attachmentWithData(fileName.get(i), FileTool.getBytes(s)));
                }
            }
            builder.setAttachments(attachmentList); //附件
        }

        String folder_name = DBManager.getFolderNameByImapFlag(fromAddress, IMAPFolderFlagDrafts);
        //保存
        IMAPAppendMessageOperation operation = ImapManager.singleton().getSession().
                appendMessageOperation(folder_name, builder.data(), MessageFlag.MessageFlagNone);
        operation.start(new OperationCallback() {
            @Override
            public void succeeded() {
                callback.success(null);
            }

            @Override
            public void failed(MailException e) {
                callback.fail(e.toString());
            }
        });
    }

}
