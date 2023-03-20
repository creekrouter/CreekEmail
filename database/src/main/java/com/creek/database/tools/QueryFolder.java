package com.creek.database.tools;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.creek.common.MailAttachment;
import com.creek.common.UserInfo;
import com.creek.database.helper.MailCursor;

public class QueryFolder {

    @SuppressLint("Range")
    public static MailAttachment get(MailCursor c) {
        MailAttachment attachment = new MailAttachment();
        attachment.setUser_email(UserInfo.userEmail);
        attachment.setEmail_folder(c.getString("email_folder"));
        attachment.setEmail_uid(c.getLong("email_uid"));
        attachment.setCreate_time(c.getString("create_time"));
        attachment.setFile_name(c.getString("file_name"));
        attachment.setFile_path(c.getString("file_path"));
        attachment.setFile_size(c.getLong("file_size"));
        attachment.setPart_id(c.getString("part_id"));
        attachment.setEncoding(c.getInt("encoding"));
        return attachment;
    }
}
