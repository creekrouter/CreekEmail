package com.creek.mail.sync;

import com.creek.common.CreekPath;
import com.creek.common.MailBean;
import com.creek.common.constant.Const;
import com.creek.common.interfaces.CommonCallBack;
import com.creek.database.DBManager;
import com.creek.router.annotation.CreekMethod;
import com.creek.sync.MailSync;
import com.libmailcore.IMAPMessage;
import com.libmailcore.IndexSet;
import com.libmailcore.Range;


import java.util.List;

public class HistoryLostMail {

    private static String sCurrentFolderName;

    @CreekMethod(path = CreekPath.Mail_BroadCast_Inbox_Folder_Changed)
    public static void onInboxFolderChange(String folderName) {
        sCurrentFolderName = folderName;
    }


    @CreekMethod(path = CreekPath.Fetch_History_Lost_Mail)
    public static void fetchHistoryLostMessages(CommonCallBack<List<MailBean>,String> callBack) {
        String folderName = sCurrentFolderName;
        fetchLostMail(folderName, new CommonCallBack<Void,String>() {
            @Override
            public void success(Void unused) {
                updateView(folderName, callBack);
            }

            @Override
            public void fail(String message) {

            }
        });
    }

    private static void fetchLostMail(String folderName, CommonCallBack<Void,String> callBack) {
        long minUid = DBManager.getLastSyncUid(folderName) + 1;
        if (minUid < 0) {
            return;
        }
        IndexSet indexSet = new IndexSet().indexSetWithRange(new Range(minUid, Long.MAX_VALUE - minUid - 1));

        MailSync.obtMailList(folderName, indexSet, new CommonCallBack<List<IMAPMessage>,String>() {
            @Override
            public void success(List<IMAPMessage> messages) {
                if (messages == null || messages.size() == 0) {
                    return;
                }
                // 同步UID 写入数据库
                DBManager.insertMailUid(folderName, messages);
                callBack.success(null);
            }

            @Override
            public void fail(String errorStr) {

            }
        });

    }


    private static void updateView(final String folderName, CommonCallBack<List<MailBean>,String> callBack) {
        if (callBack == null || !folderName.equals(sCurrentFolderName)) {
            return;
        }
        List<MailBean> list = DBManager.selectUnloadMailByUID(folderName, Long.MAX_VALUE - 1, Const.page_load_size);
        if (list == null || list.size() == 0) {
            return;
        }
        //开始同步list中这50条数据
        IndexSet indexSet = new IndexSet();
        for (MailBean _mailBean : list) {
            indexSet.addIndex(_mailBean.uid());
        }

        //从服务端拉取新增的邮件
        MailSync.fetchMailList(folderName, indexSet, new CommonCallBack<List<IMAPMessage>,String>() {
            @Override
            public void success(List<IMAPMessage> messages) {
                if (messages == null || messages.size() == 0) {
                    return;
                }
                //更新邮件入库
                DBManager.updateEmailByUid(folderName, messages);
                List<MailBean> mailList = DBManager.selectMailByUID(folderName, messages);

                if (folderName.equals(sCurrentFolderName)) {
                    callBack.success(mailList);
                    updateView(folderName, callBack);
                }
            }

            @Override
            public void fail(String errorStr) {
                callBack.fail("邮件拉取失败");
            }
        });
    }
}
