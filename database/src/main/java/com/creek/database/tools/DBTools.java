package com.creek.database.tools;

import android.text.TextUtils;

import com.creek.common.MailBean;
import com.libmailcore.Address;
import com.libmailcore.IMAPMessage;

import java.util.ArrayList;
import java.util.List;

public class DBTools {



    public static int getSubjectHash(String subject) {
        if (subject == null) {
            return 0;
        }
        return subject.hashCode();
    }

    /**
     * 将List<IMAPMessage>类型转换为List<MailBean>
     */
    public static List<MailBean> dataTransform(String folderName, String mailAddress, List<IMAPMessage> msgList) {
        List<MailBean> tempList = new ArrayList<>();
        for (int i = 0; i < msgList.size(); i++) {
            MailBean mail = new MailBean();
            mail.setFolderName(folderName);
            mail.setUserEmail(mailAddress);
            String send_email = msgList.get(i).header().from().mailbox();
            mail.setSend_email(send_email);
            String display_name = msgList.get(i).header().from().displayName();
            mail.setDisplayName(display_name == null ? send_email : display_name);
            List<Address> toList = msgList.get(i).header().to();
            String toStr = "";
            String toStr2 = "";
            if (toList != null && toList.size() > 0) {
                for (int j = 0; j < toList.size(); j++) {
                    String displayName = "";
                    toStr += toList.get(j).mailbox() + ";";
                    displayName = toList.get(j).displayName();
                    if (TextUtils.isEmpty(displayName) || "null".equals(displayName)) {
                        displayName = toList.get(j).mailbox();
                    }
                    toStr2 += displayName + ";";
                }
            }

            mail.setReceiver_email(toStr);
            mail.setReceiver_email_display_name(toStr2);

            List<Address> bccList = msgList.get(i).header().bcc();
            String bccStr = "";
            if (bccList != null && bccList.size() > 0) {
                for (int j = 0; j < bccList.size(); j++) {
                    bccStr += bccList.get(j).mailbox() + ";";
                }
                mail.setBcc_email(bccStr);
            } else {
                mail.setBcc_email(bccStr);
            }

            List<Address> ccList = msgList.get(i).header().cc();
            String ccStr = "";
            String ccStr2 = "";
            if (ccList != null && ccList.size() > 0) {
                for (int j = 0; j < ccList.size(); j++) {
                    String displayName = "";
                    ccStr += ccList.get(j).mailbox() + ";";
                    displayName = ccList.get(j).displayName();
                    if (TextUtils.isEmpty(displayName) || "null".equals(displayName)) {
                        displayName = ccList.get(j).mailbox();
                    }
                    ccStr2 += displayName + ";";
                }

                mail.setCc_email(ccStr);
                mail.setCc_email_display_name(ccStr2);
            } else {
                mail.setCc_email(ccStr);
                mail.setCc_email_display_name(ccStr2);
            }

            mail.setSequence(msgList.get(i).sequenceNumber());
            mail.setUid(msgList.get(i).uid());
            mail.setAttachmentsCount(msgList.get(i).attachments().size() + msgList.get(i).htmlInlineAttachments().size());
            mail.setSendTime(msgList.get(i).header().date().getTime());
            mail.setCreateTime(msgList.get(i).header().date().getTime());
            mail.setSubject(msgList.get(i).header().subject());
            mail.setEmailFlag(msgList.get(i).flags());
            mail.setMainPart(msgList.get(i).mainPart());
            if (msgList.get(i).requiredPartsForRendering().size() > 0) {
                mail.setPlainCharset(msgList.get(i).requiredPartsForRendering().get(0).charset());
            }

            tempList.add(mail);
        }

        return tempList;
    }

}
