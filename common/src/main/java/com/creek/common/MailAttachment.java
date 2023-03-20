package com.creek.common;

import com.creek.common.constant.Const;

import java.io.Serializable;


public class MailAttachment implements Serializable {
    private int download_state = Const.ATAACHMENTS_DOWNLOAD_STATE_DEFAULT;
    private String user_email = "";

    public int getDownload_state() {
        return download_state;
    }

    private void setDownload_state(int download_state) {
        this.download_state = download_state;
    }

    public void setDownloadStateFinished() {
        setDownload_state(Const.ATAACHMENTS_DOWNLOAD_STATE_FIINSHED);
    }

    public void setDownloadStateDefault() {
        setDownload_state(Const.ATAACHMENTS_DOWNLOAD_STATE_DEFAULT);
    }

    public void setDownloadStateStart() {
        setDownload_state(Const.ATAACHMENTS_DOWNLOAD_STATE_START);
    }

    private String email_folder = "";
    private long email_uid;
    private String create_time = "";
    private String file_name = "";
    private String file_path = "";
    private String part_id = "";

    public boolean isEncryption() {
        return encryption;
    }

    public void setEncryption(boolean encryption) {
        this.encryption = encryption;
    }

    private int encoding;
    private long file_size;

    private boolean encryption=false;

    public String getEmail_folder() {
        return email_folder;
    }

    public void setEmail_folder(String email_folder) {
        this.email_folder = email_folder;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public long getEmail_uid() {
        return email_uid;
    }

    public void setEmail_uid(long email_uid) {
        this.email_uid = email_uid;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getPart_id() {
        return part_id;
    }

    public void setPart_id(String part_id) {
        this.part_id = part_id;
    }

    public int getEncoding() {
        return encoding;
    }

    public void setEncoding(int encoding) {
        this.encoding = encoding;
    }

    public long getFile_size() {
        return file_size;
    }

    public void setFile_size(long file_size) {
        this.file_size = file_size;
    }

    public boolean isDownLoadFinished() {
        return getDownload_state() == Const.ATAACHMENTS_DOWNLOAD_STATE_FIINSHED;

    }
}
