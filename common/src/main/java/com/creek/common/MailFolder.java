package com.creek.common;


public class MailFolder {

    private boolean selected = false;
    private String user_mail = "";
    private String folder_name_en = "";
    private int folder_flag;
    private long unreadNum; // the number of emails unread
    private long unreadNumDB = 0l; // the number of emails unread local


    public boolean isSelected() {
        return selected;
    }

    private String folder_name_ch = "";

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public long getUnreadNumDB() {
        return unreadNumDB;
    }

    public void setUnreadNumDB(long unreadNumDB) {
        this.unreadNumDB = unreadNumDB;
    }

    public long getUnreadNum() {
        return unreadNum;
    }

    public void setUnreadNum(long unreadNum) {
        this.unreadNum = unreadNum;
    }

    public String getUser_mail() {
        return user_mail;
    }

    public void setUser_mail(String user_mail) {
        this.user_mail = user_mail;
    }

    public String getFolder_name_en() {
        return folder_name_en;
    }

    public void setFolder_name_en(String folder_name_en) {
        this.folder_name_en = folder_name_en;
    }

    public int getFolder_flag() {
        return folder_flag;
    }

    public void setFolder_flag(int folder_flag) {
        this.folder_flag = folder_flag;
    }

    public String getFolder_name_ch() {
        return folder_name_ch;
    }

    public void setFolder_name_ch(String folder_name_ch) {
        this.folder_name_ch = folder_name_ch;
    }
}
