package com.creek.common;

import com.libmailcore.AbstractPart;
import com.libmailcore.Attachment;
import com.libmailcore.MessageFlag;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MailBean implements Serializable {

    private String userEmail = "";
    private String subject = "";
    private String plainTxt = null;
    private String plainCharset = "";
    private String send_email = "";
    private String displayName = "";
    private String bcc_email = "";
    private String cc_email_display_name = "";
    private String cc_email = "";
    private long createTime;
    private String receiver_email_display_name = "";
    private String receiver_email = "";
    private int emailFlag;
    private int folderFlag;
    private String folderNameCh = "";
    private long sequence;
    private long uid;
    private String folderName = "";
    private int attachmentsCount;
    private AbstractPart mainPart;
    private long sendTime = 0;

    public final List<Attachment> inlineAttachments = new ArrayList<>();


    private boolean selected = false;//是否在选中状态

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long emailSendTime) {
        sendTime = emailSendTime;
    }

    @Override
    public String toString() {
        String str = uid + "";
        return str;
    }

    public String toString2() {
        String str = "主题：" + subject +
                "\n发件人邮件地址：" + send_email +
                "\nFlag：" + emailFlag +
                "\n发件人别名：" + displayName +
                "\n密送：" + bcc_email +
                "\n抄送:" + cc_email_display_name +
                "\n抄送别名:" + cc_email +
                "\n时间：" + getSend_time() +
                "\n接收人：" + receiver_email +
                "\n接收人别名：" + receiver_email_display_name +
                "\n邮件Uid：" + uid +
                "\n附件数量：" + attachmentsCount +
                "\n邮件序列号：" + sequence;
        return str;
    }

    public boolean isForwardMail() {
        long flag = getEmailFlag();
        if ((flag & MessageFlag.MessageFlagForwarded) == MessageFlag.MessageFlagForwarded) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isAnsweredMail() {
        long flag = getEmailFlag();
        if ((flag & MessageFlag.MessageFlagAnswered) == MessageFlag.MessageFlagAnswered) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isSeenMail() {
        long flag = getEmailFlag();
        if ((flag & MessageFlag.MessageFlagSeen) == MessageFlag.MessageFlagSeen) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isRedFlagMail() {
        long flag = getEmailFlag();
        if ((flag & MessageFlag.MessageFlagFlagged) == MessageFlag.MessageFlagFlagged) {
            return true;
        } else {
            return false;
        }
    }

    public int getAttachmentsCount() {
        return attachmentsCount;
    }

    public void setAttachmentsCount(int attachmentsCount) {
        this.attachmentsCount = attachmentsCount;
    }

    public String getPlainCharset() {
        return plainCharset;
    }

    public void setPlainCharset(String plainCharset) {
        this.plainCharset = plainCharset;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSend_email() {
        return send_email;
    }

    public void setSend_email(String send_email) {
        this.send_email = send_email;
    }

    public String getBcc_email() {
        return bcc_email;
    }

    public void setBcc_email(String bcc_email) {
        this.bcc_email = bcc_email;
    }

    public String getCc_email() {
        return cc_email;
    }

    public void setCc_email(String cc_email) {
        this.cc_email = cc_email;
    }

    public String getSend_time() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(sendTime);
    }

    public String getReceiver_email() {
        return receiver_email;
    }

    public void setReceiver_email(String receiver_email) {
        this.receiver_email = receiver_email;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public long uid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String folderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPlainTxt() {
        return plainTxt;
    }

    public void setPlainTxt(String plainTxt) {
        this.plainTxt = plainTxt;
    }

    public int getEmailFlag() {
        return emailFlag;
    }

    public void setEmailFlag(int emailFlag) {
        this.emailFlag = emailFlag;
    }

    public String getCc_email_display_name() {
        return cc_email_display_name;
    }

    public void setCc_email_display_name(String cc_email_display_name) {
        this.cc_email_display_name = cc_email_display_name;
    }

    public String getReceiver_email_display_name() {
        return receiver_email_display_name;
    }

    public void setReceiver_email_display_name(String receiver_email_display_name) {
        this.receiver_email_display_name = receiver_email_display_name;
    }

    public int getFolderFlag() {
        return folderFlag;
    }

    public void setFolderFlag(int folderFlag) {
        this.folderFlag = folderFlag;
    }

    public String getFolderNameCh() {
        return folderNameCh;
    }

    public void setFolderNameCh(String folderNameCh) {
        this.folderNameCh = folderNameCh;
    }

    public AbstractPart getMainPart() {
        return mainPart;
    }

    public void setMainPart(AbstractPart mainPart) {
        this.mainPart = mainPart;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }


}
