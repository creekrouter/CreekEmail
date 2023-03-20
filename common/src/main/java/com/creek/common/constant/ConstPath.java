package com.creek.common.constant;

import com.creek.common.MailAttachment;
import com.creek.common.MailBean;
import com.creek.common.UserInfo;
import com.creek.common.context.AppContext;

import java.io.File;

public class ConstPath {

    public static final File getFiles() {
        return AppContext.getAppContext().getFilesDir();
    }


    public static final File getUserFiles() {
        return new File(AppContext.getAppContext().getFilesDir(), UserInfo.userEmail);
    }

    /*
    attachment
     */

    public static final File getAttachDir() {
        return new File(getUserFiles(), "accessory");
    }

    public static final File getAttach(MailAttachment attachment) {
        if (attachment == null) {
            return getAttachDir();
        } else {
            return new File(getAttachDir(), attachment.getEmail_uid() + "/" + attachment.getFile_name());
        }
    }

    /*
    Message body
     */

    public static final File getMailBodyDir() {
        return new File(getUserFiles(), "mail/body");
    }

    public static final File getMailBodyImgDir() {
        return new File(getMailBodyDir(), "img");
    }

    public static final File getMailBody(MailBean mailBean) {
        File file = new File(getMailBodyDir(), mailBean.folderName());
        return new File(file, mailBean.uid() + ".html");
    }

    public static final File getMailBody_TagBody(MailBean mailBean) {
        File file = new File(getMailBodyDir(), mailBean.folderName());
        return new File(file, mailBean.uid() + "_body.html");
    }

    public static final File getMailBodyImg(MailBean mailBean, String picName) {
        File file = new File(getMailBodyDir(), mailBean.folderName()+"/"+mailBean.uid());
        return new File(file, picName);
    }

    /*
    cache dir
     */

    public static final File getCache() {
        return new File(AppContext.getAppContext().getCacheDir(), "mail");
    }

    public static final File getCacheImg() {
        return new File(getCache(), "image");
    }
}
