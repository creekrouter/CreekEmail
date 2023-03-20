package com.creek.common;


import android.text.TextUtils;

import com.creek.common.constant.Const;

import java.io.Serializable;
import java.util.ArrayList;

public class ContactEntity implements Serializable {
    private int Type;
    private String Name;
    private String Email;
    //分组标签

    public ContactEntity(int type, String name, String email) {
        Type = type;
        Name = name;
        Email = email;
    }


    public int getType() {
        return Type;
    }

    @Override
    public String toString() {
        return "ContactEntity{" +
                "Type=" + Type +
                ", Name='" + Name + '\'' +
                ", Email='" + Email + '\'' +
                '}';
    }

    public void setType(int type) {
        Type = type;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }


    /**
     * Description:根据当前邮件实体类信息，生成邮件联系人实体类
     *
     * @author jack
     * Created at 2018/7/11 17:46
     */
    public static ArrayList<ContactEntity> getContactList(MailBean mail) {
        ArrayList<ContactEntity> entities = new ArrayList<>();

        //分组标签
        entities.add(new ContactEntity(Const.MAIL_SEND_TAG, "", ""));
        entities.add(new ContactEntity(Const.MAIL_SEND, mail.getDisplayName().replace("\"", "").trim(), mail.getSend_email()));

        //接收者数组
        String[] receiverNames = mail.getReceiver_email_display_name().split(";");

        //接收者邮件地址数组
        String[] receiverEmails = mail.getReceiver_email().split(";");

        //分组标签
        entities.add(new ContactEntity(Const.MAIL_TO_TAG, "", ""));
        for (int i = 0; i < receiverNames.length; i++) {
            //解决联系人字符串尾缀为；问题
            if (TextUtils.isEmpty(receiverNames[i]) || TextUtils.isEmpty(receiverEmails[i])) {
                continue;
            }
            entities.add(new ContactEntity(Const.MAIL_TO, receiverNames[i].replace("\"", "").trim(), receiverEmails[i]));
        }


        //抄送人非空处理
        if (!TextUtils.isEmpty(mail.getCc_email())) {
            //抄送者数组
            String[] ccNames = mail.getCc_email_display_name().split(";");
            //抄送者邮件地址数组
            String[] ccEmails = mail.getCc_email().split(";");
            //分组标签
            entities.add(new ContactEntity(Const.MAIL_CC_TAG, "", ""));
            for (int i = 0; i < ccNames.length; i++) {
                //解决联系人字符串尾缀为；问题
                if (TextUtils.isEmpty(ccNames[i]) || TextUtils.isEmpty(ccEmails[i])) {
                    continue;
                }
                entities.add(new ContactEntity(Const.MAIL_CC, ccNames[i], ccEmails[i]));
            }

        }
        return entities;
    }

    public static MailContact getSender(MailBean mail) {
        MailContact contact = new MailContact();
        contact.setEmail_addr(mail.getSend_email());
        contact.setDisplay_name(mail.getDisplayName());
        return contact;
    }

    public static ArrayList<MailContact> getToList(MailBean mail) {
        ArrayList<MailContact> entities = new ArrayList<>();
        //接收者数组
        String[] receiverNames = mail.getReceiver_email_display_name().split(";");
        //接收者邮件地址数组
        String[] receiverEmails = mail.getReceiver_email().split(";");
        for (int i = 0; i < receiverNames.length; i++) {
            //解决联系人字符串尾缀为；问题
            if (TextUtils.isEmpty(receiverNames[i]) || TextUtils.isEmpty(receiverEmails[i])) {
                continue;
            }
            MailContact contact = new MailContact();
            contact.setEmail_addr(receiverEmails[i]);
            contact.setDisplay_name(receiverNames[i].replace("\"", "").trim());
            entities.add(contact);
        }
        return entities;
    }

    public static ArrayList<MailContact> getCcList(MailBean mail) {
        ArrayList<MailContact> entities = new ArrayList<>();
        //抄送人非空处理
        if (!TextUtils.isEmpty(mail.getCc_email())) {
            //抄送者数组
            String[] ccNames = mail.getCc_email_display_name().split(";");
            //抄送者邮件地址数组
            String[] ccEmails = mail.getCc_email().split(";");
            for (int i = 0; i < ccNames.length; i++) {
                //解决联系人字符串尾缀为；问题
                if (TextUtils.isEmpty(ccNames[i]) || TextUtils.isEmpty(ccEmails[i])) {
                    continue;
                }
                MailContact contact = new MailContact();
                contact.setEmail_addr(ccEmails[i]);
                contact.setDisplay_name(ccNames[i]);
                entities.add(contact);

            }
        }
        return entities;
    }


}
