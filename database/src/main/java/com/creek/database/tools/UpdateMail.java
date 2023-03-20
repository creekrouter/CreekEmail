package com.creek.database.tools;

import android.content.ContentValues;
import android.text.TextUtils;

import com.creek.common.MailContact;
import com.creek.common.UserInfo;
import com.libmailcore.Address;
import com.libmailcore.IMAPMessage;

import java.util.List;
import java.util.Set;

public class UpdateMail {
    public static ContentValues get(IMAPMessage mailInfo, Set<MailContact> contacts){
        ContentValues values = new ContentValues();
        values.put("subject", mailInfo.header().subject());
        values.put("email_load_flag", 1);
        if (mailInfo.requiredPartsForRendering().size() > 0) {
            values.put("plain_charset", mailInfo.requiredPartsForRendering().get(0).charset());
        }
        String send_email = "";
        if (mailInfo.header().from() != null) {
            send_email = mailInfo.header().from().mailbox();
        }
        values.put("send_email", send_email);
        String display_name = "";
        if (mailInfo.header().from() != null) {
            display_name = mailInfo.header().from().displayName();
        }
        if (TextUtils.isEmpty(display_name) && !TextUtils.isEmpty(send_email)) {
            display_name = send_email.substring(0, send_email.lastIndexOf("@"));
        } else {
            display_name = display_name.replace("\"", "");
        }
        values.put("display_name", display_name);
        contacts.add(new MailContact(UserInfo.userEmail, display_name, send_email));

        List<Address> bccList = mailInfo.header().bcc();
        String bccStr = "";
        if (bccList != null && bccList.size() > 0) {
            for (int j = 0; j < bccList.size(); j++) {
                bccStr += bccList.get(j).mailbox() + ";";
            }
            values.put("bcc_email", bccStr.substring(0, bccStr.lastIndexOf(";")));
        } else {
            values.put("bcc_email", bccStr);
        }
        List<Address> ccList = mailInfo.header().cc();
        String ccStr = "";
        String ccStr2 = "";
        if (ccList != null && ccList.size() > 0) {
            for (int j = 0; j < ccList.size(); j++) {
                String displayName = "";
                ccStr += ccList.get(j).mailbox() + ";";
                displayName = ccList.get(j).displayName();
                if (TextUtils.isEmpty(displayName) || "null".equals(displayName)) {
                    if (!TextUtils.isEmpty(ccList.get(j).mailbox())) {
                        displayName = ccList.get(j).mailbox();
                        displayName = displayName.substring(0, displayName.lastIndexOf("@"));
                    }
                } else {
                    displayName = displayName.replace("\"", "");
                }
                ccStr2 += displayName + ";";
                contacts.add(new MailContact(UserInfo.userEmail, displayName, ccList.get(j).mailbox()));
            }
            values.put("cc_email", ccStr.substring(0, ccStr.lastIndexOf(";")));
            values.put("cc_email_display_name", ccStr2.substring(0, ccStr2.lastIndexOf(";")));
        } else {
            values.put("cc_email", ccStr);
            values.put("cc_email_display_name", ccStr2);
        }
        values.put("send_email_time", mailInfo.header().receivedDate().getTime());
        values.put("create_time", mailInfo.header().date().getTime());
        List<Address> toList = mailInfo.header().to();
        String toStr = "";
        String toStr2 = "";
        if (toList != null && toList.size() > 0) {
            for (int j = 0; j < toList.size(); j++) {
                String displayName = "";
                toStr += toList.get(j).mailbox() + ";";
                displayName = toList.get(j).displayName();
                if (TextUtils.isEmpty(displayName) || "null".equals(displayName)) {
                    if (!TextUtils.isEmpty(toList.get(j).mailbox())) {
                        displayName = toList.get(j).mailbox();
                        displayName = displayName.substring(0, displayName.lastIndexOf("@"));
                    }
                } else {
                    displayName = displayName.replace("\"", "");
                }
                toStr2 += displayName + ";";
                contacts.add(new MailContact(UserInfo.userEmail, displayName, toList.get(j).mailbox()));
            }
            values.put("receiver_email", toStr.substring(0, toStr.lastIndexOf(";")));
            values.put("receiver_email_display_name", toStr2.substring(0, toStr2.lastIndexOf(";")));
        } else {
            values.put("receiver_email", toStr);
            values.put("receiver_email_display_name", toStr2);
        }
        values.put("email_sque", mailInfo.sequenceNumber());
        values.put("attachments_count", mailInfo.attachments().size() + mailInfo.htmlInlineAttachments().size());
        values.put("email_uid", mailInfo.uid());
        values.put("email_flag", mailInfo.flags());
        return values;
    }
}
