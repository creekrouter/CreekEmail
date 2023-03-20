package com.creek.common.constant;


import com.creek.common.UserInfo;

public class Const {

    public static final String SP_USER_EMAIL = "sp_user_email";
    public static final String SP_USER_PWD = "sp_user_pwd";


    public static final int page_size = 50;  //邮件分页数量
    public static final int page_load_size = 10; //断层加载一次数量

    public static final int MAIL_CC = 0x101;
    public static final int MAIL_BCC = 0x102;
    public static final int MAIL_TO = 0x103;
    public static final int MAIL_CC_TAG = 0x104;
    public static final int MAIL_BCC_TAG = 0x105;
    public static final int MAIL_TO_TAG = 0x106;
    public static final int MAIL_SEND_TAG = 0x107;
    public static final int MAIL_SEND = 0x108;
    public static final String INBOX = "收件箱";
    public static final String DRAFTS = "草稿箱";
    public static final String SENT = "已发送";
    public static final String TRASH = "已删除";
    public static final String JUNK = "垃圾邮件";
    public static final String FLAGED = "红旗邮件";
    public static final String FLAGED_EN = "Flaged";

    //默认状态
    public static final int ATAACHMENTS_DOWNLOAD_STATE_DEFAULT = 0;
    //开始下载
    public static final int ATAACHMENTS_DOWNLOAD_STATE_START = 1;
    //下载完成
    public static final int ATAACHMENTS_DOWNLOAD_STATE_FIINSHED = 2;


    public static final String HTML_BR = "<div><font color='#757575'>------------------------</font></div>";
    public static final String MAIL_SIGNATURE = "<br/><br/><br/>" + HTML_BR + "<font color='#757575'  style = 'margin:5px;padding:5px' >From CreekMail.</font>" + "<br/>";
    public static final String MAIL_SIGNATURE_NEW = "<font color='#888888'>输入个性签名...</font>" + "<br/>";
    public static final String MAIL_SIGNATURE_SUFFIX = "<div><font color='#888888' style = 'margin:5px;padding:5px'>Have A Nice Day!</font></div>";


    public static String get_KEY_SIGNATURE() {
        return UserInfo.userEmail;
    }
}
