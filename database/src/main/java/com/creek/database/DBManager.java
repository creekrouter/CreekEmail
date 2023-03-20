package com.creek.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;


import com.creek.common.CreekPath;
import com.creek.database.constants.SQL;
import com.creek.database.constants.SQLiteConstant;
import com.creek.database.helper.MailCursor;
import com.creek.database.helper.SQLiteHelper;
import com.creek.database.helper.SqlCallBack;
import com.creek.database.tools.InsertMail;
import com.creek.database.tools.QueryFolder;
import com.creek.database.tools.QueryMail;
import com.creek.database.tools.UpdateMail;
import com.creek.router.annotation.CreekMethod;
import com.libmailcore.AbstractPart;
import com.libmailcore.IMAPMessage;
import com.libmailcore.IMAPPart;
import com.libmailcore.MessageFlag;
import com.creek.common.UserInfo;
import com.creek.common.MailContact;
import com.creek.common.MailFolder;
import com.creek.common.MailBean;
import com.creek.common.MailAttachment;
import com.creek.common.constant.Const;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.creek.database.helper.SQLiteHelper.exe;
import static com.libmailcore.IMAPFolderFlags.IMAPFolderFlagDrafts;
import static com.libmailcore.IMAPFolderFlags.IMAPFolderFlagFlagged;
import static com.libmailcore.IMAPFolderFlags.IMAPFolderFlagTrash;


public class DBManager {


    @SuppressLint("Range")
    public static long getLastSyncUid(String mailFolder) {
        if (TextUtils.isEmpty(mailFolder)) {
            return -10;
        }
        final long[] minUid = {Long.MAX_VALUE};

        boolean res = exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                String sql = SQL.getMinUid(UserInfo.userEmail, mailFolder);
                Cursor c = db.rawQuery(sql, new String[]{});
                if (null != c) {
                    while (c.moveToNext()) {
                        long itemUid = c.getLong(c.getColumnIndex("email_uid"));
                        if (itemUid < minUid[0]) {
                            minUid[0] = itemUid;
                        }
                    }
                }
                return c;
            }
        });
        if (res && minUid[0] < Long.MAX_VALUE) {
            return minUid[0];
        }
        return -10;
    }

    public static boolean insertMailUid(String folderName, List<IMAPMessage> mailList) {

        if (mailList == null || mailList.size() == 0) {
            return true;
        }
        boolean res = exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                for (int i = 0; i < mailList.size(); i++) {
                    ContentValues values = new ContentValues();

                    values.put("email_folder", folderName);
                    values.put("user_email", UserInfo.userEmail);
                    values.put("email_uid", mailList.get(i).uid());
                    values.put("email_load_flag", 0);

                    synchronized (DBManager.class) {
                        long res = db.insert(SQLiteConstant.MAIL_DETAIL, null, values);
                        //插入失败，说明数据库中存在啊
                        if (res < 0) {
                            String[] whereArgs = {UserInfo.userEmail, folderName, mailList.get(i).uid() + ""};
                            values.remove("email_load_flag");
                            db.update(SQLiteConstant.MAIL_DETAIL, values, "user_email=? and email_folder=? and email_uid=?", whereArgs);
                        }
                    }
                }
                return null;
            }
        });
        return res;
    }

    /**
     * 将邮件信息存储到邮件信息表和邮件id表
     *
     * @param folderName 文件夹名称   如：收件箱=INBOX
     * @param mailList   待插入邮件数据
     * @return
     */
    public static boolean insertMail(String folderName, List<IMAPMessage> mailList) {

        if (mailList == null || mailList.size() == 0) {
            return true;
        }
        Set<MailContact> contacts = new HashSet<>();

        boolean res = exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                for (int i = 0; i < mailList.size(); i++) {
                    IMAPMessage mailInfo = mailList.get(i);
                    ContentValues values = InsertMail.get(folderName, mailInfo, contacts);
                    synchronized ("insertToTableMailInfo") {

                        List<AbstractPart> attList = new ArrayList<>();
                        attList.addAll(mailInfo.attachments());
                        attList.addAll(mailInfo.htmlInlineAttachments());
                        //如果包含附件，将附件信息插入到数据库
                        if (attList.size() > 0) {
                            insertMailAttachment(folderName, mailInfo.uid(), attList);
                        }

                        long ll = db.replace(SQLiteConstant.MAIL_DETAIL, null, values);
                    }
                }

                //将联系人信息插入到常用联系人表
                insertMailContact(contacts);
                return null;
            }
        });

        return res;
    }


    public static void updateEmailByUid(String folder_name, List<IMAPMessage> mailList) {
        if (mailList == null || mailList.size() == 0) {
            return;
        }
        String table = SQLiteConstant.MAIL_DETAIL;
        Set<MailContact> contacts = new HashSet<>();
        exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                for (int i = 0; i < mailList.size(); i++) {
                    IMAPMessage mailInfo = mailList.get(i);
                    String[] whereArgs = {UserInfo.userEmail, folder_name, mailInfo.uid() + ""};
                    ContentValues values = UpdateMail.get(mailInfo, contacts);
                    synchronized ("updateEmailInfoByUid") {
                        List<AbstractPart> attList = new ArrayList<>();
                        attList.addAll(mailInfo.attachments());
                        attList.addAll(mailInfo.htmlInlineAttachments());
                        //如果包含附件，将附件信息插入到数据库
                        if (attList.size() > 0) {
                            insertMailAttachment(folder_name, mailInfo.uid(), attList);
                        }
                        db.update(table, values, "user_email=? and email_folder=? and email_uid=?", whereArgs);
                    }
                }
                return null;
            }
        });
    }

    /**
     * 根据uid更新邮件的概要信息
     *
     * @param table       表名
     * @param folder_name 文件夹名称   如：收件箱=INBOX
     * @param uid         邮件uid
     * @param plain_txt   要更新
     * @return
     */
    public static boolean updatePlainByUid(String table, String folder_name, long uid, String plain_txt) {

        if (!TextUtils.isEmpty(plain_txt)) {
            return exe(new SqlCallBack() {
                @Override
                public Cursor crud(SQLiteHelper db) {
                    synchronized (DBManager.class) {
                        String[] whereArgs = {UserInfo.userEmail, folder_name, uid + ""};
                        ContentValues cv = new ContentValues();
                        cv.put("plain_txt", plain_txt);

                        db.update(table, cv, "user_email=? and email_folder=? and email_uid=?", whereArgs);
                    }
                    return null;
                }
            });
        }
        return true;
    }

    /**
     * 根据uid更新邮件的Flag
     *
     * @param folder_name 文件夹名称   如：收件箱=INBOX
     * @param uid         邮件uid
     * @param flag
     * @return
     */
    @CreekMethod(path = CreekPath.SQL_Update_Message_ImapFlag)
    public static boolean updateFlagByUid(String folder_name, long uid, int flag) {
        if (flag < 0) {
            return true;
        }

        return exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                synchronized ("updateFlagByUid") {
                    String sql = "UPDATE " + SQLiteConstant.MAIL_DETAIL + " SET email_flag = '" + flag + "' WHERE user_email='" +
                            UserInfo.userEmail + "' and email_folder='" + folder_name + "' and email_uid=" + uid;
                    db.execSQL(sql);
                }
                return null;
            }
        });
    }

    /**
     * 根据uid更新邮件的FolderName
     *
     * @param folder_name  文件夹名称   如：收件箱=INBOX
     * @param uid          邮件uid
     * @param folder_name2 更新后的文件夹名称
     * @return
     */
    public static boolean updateMessageFolderName(String folder_name, long uid, String folder_name2) {

        return exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                synchronized ("updateFolderNameByUid") {
                    String sql = "UPDATE " + SQLiteConstant.MAIL_DETAIL + " SET email_folder = '" + folder_name2 + "' WHERE user_email='" +
                            UserInfo.userEmail + "' and email_folder='" + folder_name + "' and email_uid=" + uid;
                    db.execSQL(sql);
                }
                return null;
            }
        });
    }

    @CreekMethod(path = CreekPath.SQL_Update_Message_List_Folder_Name)
    public static void updateMessageFolderNameList(String desFolder, List<MailBean> mailList) {

        if (mailList == null || mailList.size() == 0) {
            return;
        }
        for (MailBean mailBean : mailList) {
            updateMessageFolderName(mailBean.folderName(), mailBean.uid(), desFolder);
        }
    }


    /**
     * 从邮件详情表中查询指定文件夹的邮件信息
     *
     * @param mail_folder 邮件文件夹  如:收件箱=INBOX
     * @param page_size   分页数量
     * @return
     */
    @SuppressLint("Range")
    public static List<MailBean> selectMailList(String mail_folder, int page_size) {
        if (TextUtils.isEmpty(mail_folder)) {
            return null;
        }

        List<MailBean> mailInfoList = new ArrayList<>();
        exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                String sql = "select * from " + SQLiteConstant.MAIL_DETAIL + " where user_email = ? and " +
                        "email_folder = ? order by email_uid desc limit 0," + page_size;
                Cursor c = db.rawQuery(sql, new String[]{UserInfo.userEmail, mail_folder});
                MailCursor cursor = new MailCursor(c);
                while (cursor.moveToNext()) {
                    mailInfoList.add(QueryMail.get(cursor));
                }
                return c;
            }
        });
        return mailInfoList;
    }

    /**
     * 从邮件详情表中查询指定文件夹的未读数量
     *
     * @param mail_folder 邮件文件夹  如:收件箱=INBOX
     * @return
     */
    public static long selectUnSeenCount(String mail_folder) {
        final long[] count = {0};
        if (TextUtils.isEmpty(mail_folder)) {
            return count[0];
        }
        exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                String sql = "";
                Cursor c = null;
                if (Const.FLAGED_EN.equals(mail_folder)) {  //红旗文件夹

                    String deleteFolder = getFolderNameByImapFlag(UserInfo.userEmail, IMAPFolderFlagTrash);

                    sql = "select count(*) from " + SQLiteConstant.MAIL_DETAIL + " where user_email = ? and " +
                            "email_folder != '" + deleteFolder + "' and email_flag&" +
                            MessageFlag.MessageFlagFlagged + ">0 and email_flag&" + MessageFlag.MessageFlagSeen + "=0";
                    c = db.rawQuery(sql, new String[]{UserInfo.userEmail});
                } else {
                    sql = "select count(*) from " + SQLiteConstant.MAIL_DETAIL + " where user_email = ? and " +
                            "email_folder = ? and email_flag&" + MessageFlag.MessageFlagSeen + "=0";
                    c = db.rawQuery(sql, new String[]{UserInfo.userEmail, mail_folder});
                }
                if (null != c) {
                    c.moveToFirst();
                    count[0] = c.getLong(0);

                }
                return c;
            }
        });
        return count[0];
    }

    /**
     * 从邮件详情表中查询指定关键字的邮件
     *
     * @param type 搜索范围 0=全部，1=发件人，2=主题，3=收件人
     * @param key  搜索关键字
     * @return
     */
    @SuppressLint("Range")
    public static List<MailBean> searchMailByKey(String type, String
            key, long startTime, long endTime) {

        List<MailBean> mailInfoList = new ArrayList<>();

        exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                String sql = "";
                String timeCondition = " a.send_email_time <= " + startTime + " and a.send_email_time > " + endTime + " and ";
                if ("1".equals(type)) {
                    sql = "select * from " + SQLiteConstant.MAIL_DETAIL + " a, " + SQLiteConstant.MAIL_FOLDER +
                            " b where a.user_email = b.user_email and a.email_folder = b.folder_name_en and a.user_email = ? and " + timeCondition +
                            "(a.send_email like '%" + key + "%' or a.display_name like '%" + key + "%') order by a.send_email_time desc";
                } else if ("2".equals(type)) {
                    sql = "select * from " + SQLiteConstant.MAIL_DETAIL + " a, " + SQLiteConstant.MAIL_FOLDER +
                            " b where a.user_email = b.user_email and a.email_folder = b.folder_name_en and a.user_email = ? and " + timeCondition +
                            "a.subject like '%" + key + "%' order by a.send_email_time desc";
                } else if ("3".equals(type)) {
                    sql = "select * from " + SQLiteConstant.MAIL_DETAIL + " a, " + SQLiteConstant.MAIL_FOLDER +
                            " b where a.user_email = b.user_email and a.email_folder = b.folder_name_en and a.user_email = ? and " + timeCondition +
                            "(a.receiver_email like '%" + key + "%' or a.receiver_email_display_name like '%" + key + "%') order by a.send_email_time desc";
                } else {
                    sql = "select * from " + SQLiteConstant.MAIL_DETAIL + " a, " + SQLiteConstant.MAIL_FOLDER +
                            " b where a.user_email = b.user_email and a.email_folder = b.folder_name_en and" + timeCondition +
                            " a.user_email = ? and (a.send_email like '%" + key + "%' or a.subject like '%" +
                            key + "%' or " + "a.receiver_email like '%" + key +
                            "%' or a.receiver_email_display_name like '%" + key + "%' or a.display_name like '%" + key + "%') order by a.send_email_time desc";
                }
                Cursor c = db.rawQuery(sql, new String[]{UserInfo.userEmail});
                MailCursor cursor = new MailCursor(c);
                while (cursor.moveToNext()) {
                    mailInfoList.add(QueryMail.get(cursor));
                }
                return c;
            }
        });
        return mailInfoList;
    }

    /**
     * 从邮件详情表中查询小于指定uid且email_load_flag=0前五十条数句
     * email_load_flag=0表示当前邮件没有下载到数据库，属于断层邮件
     *
     * @param mail_folder 邮件文件夹  如:收件箱=INBOX
     * @param uid         邮件uid
     * @param page_size   分页数量
     * @return
     */
    @SuppressLint("Range")
    public static List<MailBean> selectUnloadMailByUID(String mail_folder, long uid, int page_size) {
        if (TextUtils.isEmpty(mail_folder)) {
            return null;
        }

        List<MailBean> mailInfoList = new ArrayList<>();
        exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                String sql = "select * from " + SQLiteConstant.MAIL_DETAIL + " where user_email = ? and " +
                        "email_folder = ? and email_uid < ? and email_load_flag=0 order by email_uid desc limit 0," + page_size;
                Cursor c = db.rawQuery(sql, new String[]{UserInfo.userEmail, mail_folder, uid + ""});
                if (null != c) {
                    while (c.moveToNext()) {
                        MailBean mailInfo = new MailBean();
                        mailInfo.setUid(c.getLong(c.getColumnIndex("email_uid")));
                        mailInfoList.add(mailInfo);
                    }
                }
                return null;
            }
        });
        return mailInfoList;
    }

    /**
     * 从邮件详情表中查询小于指定uid的前五十条数句
     *
     * @param mail_folder 邮件文件夹  如:收件箱=INBOX
     * @param uid         邮件uid
     * @param page_size   分页数量
     * @return
     */
    @SuppressLint("Range")
    public static List<MailBean> selectMailByUID(String mail_folder, long uid, int page_size) {
        if (TextUtils.isEmpty(mail_folder)) {
            return null;
        }

        List<MailBean> mailInfoList = new ArrayList<>();

        exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                String sql = "select * from " + SQLiteConstant.MAIL_DETAIL + " where user_email = ? and " +
                        "email_folder = ? and email_uid < ? and email_load_flag = 1 order by email_uid desc limit 0," + page_size;
                Cursor c = db.rawQuery(sql, new String[]{UserInfo.userEmail, mail_folder, uid + ""});
                MailCursor cursor = new MailCursor(c);
                while (cursor.moveToNext()) {
                    mailInfoList.add(QueryMail.get(cursor));
                }
                return c;
            }
        });
        return mailInfoList;
    }

    /**
     * 从邮件详情表中查询指定范围的邮件UID
     *
     * @param mail_folder 邮件文件夹  如:收件箱=INBOX
     * @param min_uid     查找范围的最大值
     * @param max_uid     查找范围的最小值
     * @return
     */
    @SuppressLint("Range")
    public static List<MailBean> selectUidFromMail(String mail_folder,
                                                   long min_uid, long max_uid) {
        if (TextUtils.isEmpty(mail_folder)) {
            return null;
        }

        List<MailBean> mailInfoList = new ArrayList<>();
        exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                String sql = "select email_uid from " + SQLiteConstant.MAIL_DETAIL + " where user_email = ? and " +
                        "email_folder = ? and email_uid <= " + max_uid + " and email_uid >= " + min_uid + " order by email_uid desc";
                Cursor c = db.rawQuery(sql, new String[]{UserInfo.userEmail, mail_folder});
                if (null != c) {
                    while (c.moveToNext()) {

                        MailBean mailInfo = new MailBean();
                        mailInfo.setUid(c.getLong(c.getColumnIndex("email_uid")));

                        mailInfoList.add(mailInfo);
                    }
                }
                return c;
            }
        });
        return mailInfoList;
    }


    /**
     * 从邮件详情表中获取指定uid的详情
     *
     * @param mail_folder 邮件文件夹  如:收件箱=INBOX
     * @param mailList    指定uid的集合
     * @return
     */
    @SuppressLint("Range")
    public static List<MailBean> selectMailByUID(String mail_folder,
                                                 List<IMAPMessage> mailList) {
        if (TextUtils.isEmpty(mail_folder)) {
            return null;
        }

        String ids = "";
        for (int i = 0; i < mailList.size(); i++) {
            ids += mailList.get(i).uid() + ",";
        }
        ids = ids.substring(0, ids.lastIndexOf(","));

        List<MailBean> mailInfoList = new ArrayList<>();

        String finalIds = ids;
        exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                String sql = "select * from " + SQLiteConstant.MAIL_DETAIL + " where user_email = ? and " +
                        "email_folder = ? and email_uid in (" + finalIds + ") order by email_uid desc";
                Cursor c = db.rawQuery(sql, new String[]{UserInfo.userEmail, mail_folder});
                MailCursor cursor = new MailCursor(c);
                while (cursor.moveToNext()) {
                    mailInfoList.add(QueryMail.get(cursor));
                }
                return c;
            }
        });
        return mailInfoList;
    }

    /**
     * 获取本地所有红旗有件（除了已删除文件夹中的）
     *
     * @return
     */
    @SuppressLint("Range")
    public static List<MailBean> selectFlaggedMail() {
        List<MailBean> mailInfoList = new ArrayList<>();
        exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                String deleteFolder = getFolderNameByImapFlag(UserInfo.userEmail, IMAPFolderFlagTrash);
                String draftFolder = getFolderNameByImapFlag(UserInfo.userEmail, IMAPFolderFlagDrafts);

                String sql = "select * from " + SQLiteConstant.MAIL_DETAIL + " where user_email = ? and " +
                        "email_folder != '" + draftFolder + "' and email_folder != '" + deleteFolder +
                        "' and email_flag&" + MessageFlag.MessageFlagFlagged + ">0 order by email_uid desc";
                Cursor c = db.rawQuery(sql, new String[]{UserInfo.userEmail});

                MailCursor cursor = new MailCursor(c);
                while (cursor.moveToNext()) {
                    mailInfoList.add(QueryMail.get(cursor));
                }
                return c;
            }
        });
        return mailInfoList;
    }

    /**
     * 删除指定uid的邮件
     *
     * @param mail_folder 邮件文件夹  如:收件箱=INBOX
     * @param mail_list   要删除的邮件集合
     */
    @CreekMethod(path = CreekPath.SQL_Delete_Message_List)
    public static void deleteMailByUid(String mail_folder, List<MailBean> mail_list) {

        exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                String ids = "";
                for (int i = 0; i < mail_list.size(); i++) {
                    ids += mail_list.get(i).uid() + ",";
                }
                ids = ids.substring(0, ids.lastIndexOf(","));
                String sql = "DELETE from " + SQLiteConstant.MAIL_DETAIL + " where email_folder='" +
                        mail_folder + "' and user_email='" + UserInfo.userEmail + "' and email_uid in (" + ids + ")";
                db.execSQL(sql);

                return null;
            }
        });
    }

    /**
     * 删除大于指定uid的邮件
     *
     * @param mail_folder 邮件文件夹  如:收件箱=INBOX
     * @param uid         指定uid
     */
    public static void deleteMailByUid(String mail_folder, long uid) {

        exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                String sql = "DELETE from " + SQLiteConstant.MAIL_DETAIL + " where email_folder='" +
                        mail_folder + "' and user_email='" + UserInfo.userEmail + "' and email_uid > " + uid;
                db.execSQL(sql);
                return null;
            }
        });
    }

    /**
     * 清空所有邮件数据
     */
    public static void deleteAllMail() {

        exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                String sql = "DELETE from " + SQLiteConstant.MAIL_DETAIL;
                db.execSQL(sql);
                String sql2 = "DELETE from " + SQLiteConstant.MAIL_CONTACTS;
                db.execSQL(sql2);
                String sql3 = "DELETE from " + SQLiteConstant.MAIL_FOLDER;
                db.execSQL(sql3);
//            String sql4 = "DELETE from "+ MailSqlConstant.MAIL_ATTACHMENT;
//            db.execSQL(sql4);
                return null;
            }
        });
    }


    /**
     * 将邮件文件夹信息存储到邮件文件夹信息表
     *
     * @param folders 待插入邮件文件夹数据
     * @return
     */
    public static boolean insertMailFolder(List<MailFolder> folders) {

        if (folders == null || folders.size() == 0) {
            return true;
        }
        return exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                for (int i = 0; i < folders.size(); i++) {
                    MailFolder folder = folders.get(i);
                    ContentValues values = new ContentValues();
                    values.put("id", i);
                    values.put("folder_flag", folder.getFolder_flag());
                    values.put("user_email", UserInfo.userEmail);
                    values.put("folder_name_en", folder.getFolder_name_en());
                    values.put("folder_name_ch", folder.getFolder_name_ch());

                    synchronized ("insertToTableMailFolder") {
                        long ll = db.replace(SQLiteConstant.MAIL_FOLDER, null, values);
                    }
                }
                return null;
            }
        });

    }

    /**
     * 查询邮件文件夹表中所有信息
     *
     * @return
     */
    @SuppressLint("Range")
    public static List<MailFolder> selectMailFolder() {

        List<MailFolder> folders = new ArrayList<>();
        exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                String sql = "select * from " + SQLiteConstant.MAIL_FOLDER + " where user_email = ? order by id asc";
                Cursor c = db.rawQuery(sql, new String[]{UserInfo.userEmail});
                if (null != c) {
                    while (c.moveToNext()) {

                        MailFolder folder = new MailFolder();
                        folder.setUser_mail(UserInfo.userEmail);
                        folder.setFolder_name_ch(c.getString(c.getColumnIndex("folder_name_ch")));
                        folder.setFolder_name_en(c.getString(c.getColumnIndex("folder_name_en")));
                        folder.setFolder_flag(c.getInt(c.getColumnIndex("folder_flag")));

                        folders.add(folder);
                    }
                }
                return c;
            }
        });
        return folders;
    }

    @SuppressLint("Range")
    public static List<MailFolder> selectMoveFolders() {

        List<MailFolder> folders = new ArrayList<>();
        exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                String sql = "select * from " + SQLiteConstant.MAIL_FOLDER + " where user_email = ? order by id asc";
                Cursor c = db.rawQuery(sql, new String[]{UserInfo.userEmail});
                if (null != c) {
                    while (c.moveToNext()) {

                        int folderFlag = c.getInt(c.getColumnIndex("folder_flag"));
                        if ((folderFlag & IMAPFolderFlagDrafts) > 0 || (folderFlag & IMAPFolderFlagFlagged) > 0) {
                            continue;
                        }

                        MailFolder folder = new MailFolder();
                        folder.setUser_mail(UserInfo.userEmail);
                        folder.setFolder_name_ch(c.getString(c.getColumnIndex("folder_name_ch")));
                        folder.setFolder_name_en(c.getString(c.getColumnIndex("folder_name_en")));
                        folder.setFolder_flag(folderFlag);

                        folders.add(folder);
                    }
                }
                return c;
            }
        });

        return folders;
    }


    /**
     * 通过flag查询邮件文件夹表中文件夹名称
     *
     * @param userEmail 用户邮箱
     * @return
     */

    @CreekMethod(path = CreekPath.SQL_Get_Folder_Name_By_ImapFlag)
    public static String getFolderNameByImapFlag(String userEmail, int imapFlag) {
        final String[] folder_name = {""};
        if (TextUtils.isEmpty(userEmail)) {
            return folder_name[0];
        }

        exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                String sql = "select folder_name_en from " + SQLiteConstant.MAIL_FOLDER +
                        " where user_email = ? and folder_flag=" + imapFlag;
                Cursor c = db.rawQuery(sql, new String[]{userEmail});
                if (null != c) {
                    c.moveToFirst();
                    folder_name[0] = c.getString(0);

                }
                return c;
            }
        });
        return folder_name[0];
    }

    /**
     * 将附件信息存储到附件信息表
     *
     * @param uid   邮件id
     * @param parts 待插入邮件数据
     * @return
     */
    public static boolean insertMailAttachment(String email_folder,
                                               long uid, List<AbstractPart> parts) {

        if (parts == null || parts.size() == 0) {
            return true;
        }
        exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                for (int i = 0; i < parts.size(); i++) {
                    IMAPPart part = (IMAPPart) parts.get(i);
                    ContentValues values = new ContentValues();
                    values.put("user_email", UserInfo.userEmail);
                    values.put("email_folder", email_folder);
                    values.put("email_uid", uid);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
                    values.put("create_time", formatter.format(new Date(System.currentTimeMillis())));
                    if (TextUtils.isEmpty(part.filename())) {
                        values.put("file_name", "未知文件");
                    } else {
                        values.put("file_name", part.filename());
                    }
                    values.put("file_size", part.size());
                    values.put("encoding", part.encoding());
                    values.put("part_id", part.partID());

                    synchronized ("insertToTableMailAttachment") {
                        long ll = db.replace(SQLiteConstant.MAIL_ATTACHMENT, null, values);
                    }
                }
                return null;
            }
        });
        return true;
    }

    /**
     * 获取指定邮件的附件信息
     *
     * @param uid 邮件uid
     * @return
     */
    @SuppressLint("Range")
    public static List<MailAttachment> selectMailAttachmentByUID(long uid) {
        if (uid == 0) {
            return null;
        }

        List<MailAttachment> attachments = new ArrayList<>();
        exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                String sql = "select * from " + SQLiteConstant.MAIL_ATTACHMENT + " where user_email = ? and " +
                        "email_uid = " + uid + " order by part_id asc";
                Cursor c = db.rawQuery(sql, new String[]{UserInfo.userEmail});
                MailCursor cursor = new MailCursor(c);
                while (cursor.moveToNext()) {
                    attachments.add(QueryFolder.get(cursor));
                }
                return c;
            }
        });
        return attachments;
    }


    /**
     * 更新附件的存储路径
     *
     * @return
     */
    public static boolean updateAttachmentFilePath(MailAttachment attachment) {

        String partId = attachment.getPart_id();
        long uid = attachment.getEmail_uid();
        String filepath = attachment.getFile_path();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
        String createTime = formatter.format(new Date(System.currentTimeMillis()));
        if (TextUtils.isEmpty(filepath)) {
            return true;
        }
        return exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                synchronized (DBManager.class) {
                    String sql = "";
                    if (TextUtils.isEmpty(createTime)) {
                        sql = "UPDATE " + SQLiteConstant.MAIL_ATTACHMENT + " SET file_path = '" + filepath + "' WHERE user_email='" +
                                UserInfo.userEmail + "' and part_id='" + partId + "' and email_uid=" + uid;
                    } else {
                        sql = "UPDATE " + SQLiteConstant.MAIL_ATTACHMENT + " SET file_path = '" + filepath + "', create_time='" + createTime + "' WHERE user_email='" +
                                UserInfo.userEmail + "' and part_id='" + partId + "' and email_uid=" + uid;
                    }
                    db.execSQL(sql);
                }
                return null;
            }
        });

    }


    /**
     * 将邮件信息存储到邮件信息表和邮件id表
     *
     * @param mailSet 联系人集合
     * @return
     */
    public static boolean insertMailContact(Set<MailContact> mailSet) {

        if (mailSet == null || mailSet.size() == 0) {
            return true;
        }
        return exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                for (MailContact mail_addr : mailSet) {
                    ContentValues values = new ContentValues();
                    values.put("user_email", UserInfo.userEmail);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    values.put("last_time", formatter.format(new Date(System.currentTimeMillis())));
                    values.put("email_addr", mail_addr.getEmail_addr());
                    values.put("display_name", mail_addr.getDisplay_name());

                    synchronized ("insertToTableMailCommonContact") {

                        long ll = db.replace(SQLiteConstant.MAIL_CONTACTS, null, values);

                    }
                }
                return null;
            }
        });
    }

    /**
     * 通过关键字查询常用联系人信息
     *
     * @param key 搜索关键字
     * @return
     */
    @SuppressLint("Range")
    public static List<MailContact> selectMailContactByKey(String key) {
        if (TextUtils.isEmpty(UserInfo.userEmail)) {
            return null;
        }

        List<MailContact> contacts = new ArrayList<>();

        exe(new SqlCallBack() {
            @Override
            public Cursor crud(SQLiteHelper db) {
                String sql = "";
                if (TextUtils.isEmpty(sql)) {
                    sql = "select * from " + SQLiteConstant.MAIL_CONTACTS + " where user_email = ?";
                } else {
                    sql = "select * from " + SQLiteConstant.MAIL_CONTACTS + " where user_email = ? and " +
                            "email_addr like  '" + key + "'";
                }
                Cursor c = db.rawQuery(sql, new String[]{UserInfo.userEmail});
                if (null != c) {
                    while (c.moveToNext()) {

                        MailContact contact = new MailContact();
                        contact.setUser_email(UserInfo.userEmail);
                        contact.setLast_time(c.getString(c.getColumnIndex("last_time")));
                        contact.setEmail_addr(c.getString(c.getColumnIndex("email_addr")));
                        contact.setDisplay_name(c.getString(c.getColumnIndex("display_name")));

                        contacts.add(contact);
                    }
                }
                return null;
            }
        });
        return contacts;
    }

}
