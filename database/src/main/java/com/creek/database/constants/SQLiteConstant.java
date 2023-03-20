package com.creek.database.constants;

public class SQLiteConstant {
    public static final int MAIL_DB_VERSION = 1;


    public static final String MAIL_DETAIL = "mail_detail";  //邮件详情表
    public static final String MAIL_FOLDER = "mail_folder";  //邮件文件夹表
    public static final String MAIL_ATTACHMENT = "mail_attachment";  //邮件文件夹表
    public static final String MAIL_CONTACTS = "mail_contacts";  //常用联系人表

    public static final String CREATE_TABLE_MAIL_FOLDER = "create table if not exists " + MAIL_FOLDER + "(" +
            "id int, " +
            "folder_flag int, " +
            "user_email String, " +
            "folder_name_en String, " +
            "folder_name_ch String, " +
            "primary key (user_email, folder_name_en))";

    public static final String CREATE_TABLE_MAIL_INFO = "create table if not exists " + MAIL_DETAIL + "(" +
            "user_email String, " +
            "subject String, " +
            "plain_txt String, " +
            "plain_charset String, " +
            "send_email String, " +
            "create_time long, " +
            "display_name String, " +
            "bcc_email String, " +
            "cc_email String, " +
            "cc_email_display_name String, " +
            "send_email_time long, " +
            "receiver_email String, " +
            "receiver_email_display_name String, " +
            "attachments_count int, " +
            "email_sque long, " +
            "email_uid long, " +
            "email_folder String, " +
            "email_flag String, " +
            "main_part BLOB, " +
            "email_load_flag int, " +
            "subject_hash long, " +
            "primary key (user_email, email_uid, email_folder))"; // add email folder to primary key

    public static final String CREATE_TABLE_MAIL_ATTACHMENT = "create table if not exists " + MAIL_ATTACHMENT + "(" +
            "user_email String, " +
            "email_folder String, " +
            "email_uid long, " +
            "create_time String, " +
            "file_name String, " +
            "file_size long, " +
            "file_path String, " +
            "encoding int, " +
            "part_id String, " +
            "primary key (user_email, email_uid, part_id))";

    public static final String CREATE_TABLE_MAIL_COMMON_CONTACTS = "create table if not exists " + MAIL_CONTACTS + "(" +
            "user_email String, " +
            "last_time String, " +
            "email_addr String, " +
            "display_name String, " +
            "primary key (user_email, email_addr))";

}
