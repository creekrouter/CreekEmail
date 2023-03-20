package com.creek.common.entities;

import android.net.MailTo;
import android.text.TextUtils;

import com.creek.common.Attach;
import com.creek.common.UserInfo;
import com.libmailcore.Address;
import com.libmailcore.Attachment;
import com.libmailcore.MessageBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class SendData {
    public final List<String> toAddressList = new ArrayList<>();
    public final List<String> ccAddressList = new ArrayList<>();
    public final List<String> bccAddressList = new ArrayList<>();

    private String subject = "";
    private String bodyStr = "";

    public final List<Attach> attachList = new ArrayList<>();
    public final List<Attachment> inlineAttachments = new ArrayList<>();

    private final MessageBuilder builder = new MessageBuilder();

    private String error = "";

    public String getSubject() {
        return subject;
    }

    public String getBodyStr() {
        return bodyStr;
    }

    public String getError() {
        return error;
    }

    public MessageBuilder getBuilder() {
        return builder;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBodyStr(String bodyStr) {
        this.bodyStr = bodyStr;
    }

    public boolean build() {
        builder.header().setFrom(Address.addressWithMailbox(UserInfo.userEmail));  //发件人

        if (toAddressList.size() == 0) {
            error = "收件人邮箱不能为空";
            return false;
        }
        if (!isCorrectAddress(toAddressList) || !isCorrectAddress(ccAddressList) || !isCorrectAddress(bccAddressList)) {
            error = "邮箱地址错误";
            return false;
        }
        List<Address> to = new ArrayList<>();
        for (int i = 0; i < toAddressList.size(); i++) {
            to.add(Address.addressWithMailbox(toAddressList.get(i)));
        }
        builder.header().setTo(to);  //收件人

        List<Address> cc = new ArrayList<>();
        for (int i = 0; i < ccAddressList.size(); i++) {
            cc.add(Address.addressWithMailbox(ccAddressList.get(i)));
        }
        builder.header().setCc(cc);      //抄送


        List<Address> bcc = new ArrayList<>();
        for (int i = 0; i < bccAddressList.size(); i++) {
            bcc.add(Address.addressWithMailbox(bccAddressList.get(i)));
        }
        builder.header().setBcc(bcc);      //密送

        builder.header().setSubject(subject);     //主题
        builder.setHTMLBody(bodyStr);      //正文

        List<Attachment> attachmentList = new ArrayList<>();
        for (Attach attach : attachList) {
            byte[] buffer = getBytes(attach.path);
            if (buffer == null) {
                error = "附件 " + attach.name + " 无法读取。";
                return false;
            }
            attachmentList.add(Attachment.attachmentWithData(attach.name, buffer));
        }
        builder.setAttachments(attachmentList); //附件

        return true;
    }

    private boolean isCorrectAddress(List<String> addressList) {
        for (String address : addressList) {
            if (!mailFormat(address)) {
                return false;
            }
        }
        return true;
    }

    //邮箱验证
    public static boolean mailFormat(String strEmail) {
        String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        if (TextUtils.isEmpty(strPattern)) {
            return false;
        } else {
            return strEmail.matches(strPattern);
        }
    }

    /**
     * 获得指定文件的byte数组
     */
    private byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (Exception e) {
            e.toString();
        }
        return buffer;
    }
}
