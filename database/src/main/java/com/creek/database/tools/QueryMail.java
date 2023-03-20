package com.creek.database.tools;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.creek.common.MailBean;
import com.creek.common.UserInfo;
import com.creek.database.helper.MailCursor;

public class QueryMail {
    @SuppressLint("Range")
    public static MailBean get(MailCursor c) {
        MailBean mail = new MailBean();
        mail.setUserEmail(UserInfo.userEmail);
        mail.setSubject(c.getString("subject"));
        mail.setPlainCharset(c.getString("plain_charset"));
        mail.setPlainTxt(c.getString("plain_txt"));
        mail.setSend_email(c.getString("send_email"));
        mail.setDisplayName(c.getString("display_name"));
        mail.setBcc_email(c.getString("bcc_email"));
        mail.setCc_email(c.getString("cc_email"));

        mail.setCc_email_display_name(c.getString("cc_email_display_name"));
        mail.setSendTime(c.getLong("send_email_time"));
        mail.setCreateTime(c.getLong("create_time"));
        mail.setReceiver_email(c.getString("receiver_email"));

        mail.setReceiver_email_display_name(c.getString("receiver_email_display_name"));
        mail.setSequence(c.getLong("email_sque"));
        mail.setUid(c.getLong("email_uid"));
        mail.setAttachmentsCount(c.getInt("attachments_count"));

        mail.setFolderName(c.getString("email_folder"));
        mail.setEmailFlag(c.getInt("email_flag"));

        mail.setFolderNameCh(c.getString("folder_name_ch"));
        mail.setFolderFlag(c.getInt("folder_flag"));

//                    AbstractPart mainPart = (AbstractPart) ObjectLocalUtil.byteToObject(c.getBlob("main_part"));
//                    mail.setMainPart(mainPart);

        return mail;

    }
}
