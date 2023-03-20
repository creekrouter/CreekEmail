package com.creek.common;

import java.util.ArrayList;
import java.util.List;

public class EmailData {

    private List<MailBean> mailList;
    private int position;
    private String activityFrom;
    private MailBean mMail;

    private long uid;
    private String folderName;

    private List<MailAttachment> attachmentList = new ArrayList<>();

    public List<MailBean> getMailList() {
        return mailList;
    }

    public void setMailList(List<MailBean> mailList) {
        this.mailList = mailList;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getFrom() {
        return activityFrom;
    }

    public void setFrom(String from) {
        this.activityFrom = from;
    }

    public MailBean getMail() {
        return mMail;
    }

    public void setMail(MailBean mMail) {
        this.mMail = mMail;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public List<MailAttachment> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<MailAttachment> attachmentList) {
        this.attachmentList = attachmentList;
    }

    @Override
    public String toString() {
        return "EmailData{" +
                "mailList=" + mailList +
                ", position=" + position +
                ", activityFrom='" + activityFrom + '\'' +
                ", mMail=" + mMail +
                ", uid=" + uid +
                ", folderName='" + folderName + '\'' +
                ", attachmentList=" + attachmentList +
                '}';
    }


}
