package com.creek.sync.impl;

import static com.libmailcore.IMAPFolderFlags.IMAPFolderFlagDrafts;
import static com.libmailcore.IMAPFolderFlags.IMAPFolderFlagFlagged;
import static com.libmailcore.IMAPFolderFlags.IMAPFolderFlagInbox;
import static com.libmailcore.IMAPFolderFlags.IMAPFolderFlagJunk;
import static com.libmailcore.IMAPFolderFlags.IMAPFolderFlagSentMail;
import static com.libmailcore.IMAPFolderFlags.IMAPFolderFlagTrash;

import com.creek.common.MailBean;
import com.creek.common.constant.Const;
import com.creek.common.MailFolder;
import com.creek.common.interfaces.CommonCallBack;
import com.creek.sync.callback.MultiCallBack;
import com.creek.sync.imap.ImapManager;
import com.libmailcore.IMAPCopyMessagesOperation;
import com.libmailcore.IMAPFetchMessagesOperation;
import com.libmailcore.IMAPFolder;
import com.libmailcore.IMAPMessage;
import com.libmailcore.IMAPOperation;
import com.libmailcore.IMAPSession;
import com.libmailcore.IMAPStoreFlagsRequestKind;
import com.libmailcore.IndexSet;
import com.libmailcore.MailException;
import com.libmailcore.MessageFlag;
import com.libmailcore.OperationCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class BaseSync {
    protected final IMAPSession getImapSession() {
        return ImapManager.singleton().getSession();
    }

    protected final void fetchMessageList(IMAPFetchMessagesOperation operation, CommonCallBack<List<IMAPMessage>,String> callBack, String error) {
        operation.start(new OperationCallback() {
            @Override
            public void succeeded() {
                List<IMAPMessage> mailList = operation.messages();
                if (mailList != null) {
                    if (mailList.size() > 0) {
                        Collections.reverse(mailList);
                    }
                    callBack.success(mailList);
                } else {
                    callBack.fail(error);
                }
            }

            @Override
            public void failed(MailException e) {
                callBack.fail(e.toString());
            }
        });
    }

    protected final void folderExpunge(String folderName, OperationCallback callBack) {
        IMAPOperation imapOperation = ImapManager.singleton().getSession().expungeOperation(folderName);
        imapOperation.start(new OperationCallback() {
            @Override
            public void succeeded() {
                callBack.succeeded();
            }

            @Override
            public void failed(MailException e) {
                callBack.failed(e);
            }
        });

    }

    protected void dealWithFolderList(final String user_mail, List<IMAPFolder> folders, CommonCallBack<List<MailFolder>,String> callBack) {
        List<MailFolder> mailFolders = new ArrayList<MailFolder>();
        for (int i = 0; i < folders.size(); i++) {
            MailFolder mailFolder = new MailFolder();
            mailFolder.setUser_mail(user_mail);

            int flag = folders.get(i).flags();
            String path = folders.get(i).path();

            if ((flag & IMAPFolderFlagInbox) > 0) {

                mailFolder.setFolder_flag(flag);
                mailFolder.setFolder_name_ch(Const.INBOX);
                mailFolder.setFolder_name_en(path);
                mailFolders.add(mailFolder);

                //添加自定义文件夹：红旗邮件文件夹，放在收件箱下面
                MailFolder mailFolder2 = new MailFolder();
                mailFolder2.setFolder_flag(IMAPFolderFlagFlagged);
                mailFolder2.setFolder_name_ch(Const.FLAGED);
                mailFolder2.setFolder_name_en(Const.FLAGED_EN);
                mailFolders.add(mailFolder2);
            } else if ((flag & IMAPFolderFlagDrafts) > 0) {

                mailFolder.setFolder_flag(flag);
                mailFolder.setFolder_name_ch(Const.DRAFTS);
                mailFolder.setFolder_name_en(path);
                mailFolders.add(mailFolder);
            } else if ((flag & IMAPFolderFlagSentMail) > 0) {

                mailFolder.setFolder_flag(flag);
                mailFolder.setFolder_name_ch(Const.SENT);
                mailFolder.setFolder_name_en(path);
                mailFolders.add(mailFolder);
            } else if ((flag & IMAPFolderFlagTrash) > 0) {

                mailFolder.setFolder_flag(flag);
                mailFolder.setFolder_name_ch(Const.TRASH);
                mailFolder.setFolder_name_en(path);
                mailFolders.add(mailFolder);
            } else if ((flag & IMAPFolderFlagJunk) > 0) {

                mailFolder.setFolder_flag(flag);
                mailFolder.setFolder_name_ch(Const.JUNK);
                mailFolder.setFolder_name_en(path);
                mailFolders.add(mailFolder);
            } else {
                if (ImapManager.singleton().getSession().defaultNamespace() == null) {
                    mailFolder.setFolder_name_ch(path);
                    mailFolder.setFolder_name_en(path);
                    mailFolders.add(mailFolder);
                    continue;
                }
                List<String> names = ImapManager.singleton().getSession().defaultNamespace().componentsFromPath(path);
                mailFolder.setFolder_flag(flag);
                if (names != null && names.size() > 0) {
                    String temp = "";
                    for (int j = 0; j < names.size(); j++) {
                        temp = temp + names.get(j) + "/";
                    }
                    mailFolder.setFolder_name_ch(temp.substring(0, temp.length() - 1));
                } else {
                    mailFolder.setFolder_name_ch(path);
                }
                mailFolder.setFolder_name_en(path);
                mailFolders.add(mailFolder);
            }
        }
        callBack.success(mailFolders);
    }

    protected final void basicMailMove(String fromFolder, String toFolder, IndexSet indexSet, CommonCallBack<Void,String> callBack) {

        IMAPCopyMessagesOperation copyMessagesOperation = getImapSession().copyMessagesOperation(fromFolder, indexSet, toFolder);
        copyMessagesOperation.start(new OperationCallback() {
            @Override
            public void succeeded() {
                callBack.success(null);
            }

            @Override
            public void failed(MailException e) {
                callBack.fail(e.toString());
            }
        });
    }

    protected final void basicMailDel(String folderName, IndexSet indexSet, CommonCallBack<Void,String> callBack) {

        IMAPOperation imapOperation = getImapSession().storeFlagsByUIDOperation(folderName, indexSet,
                IMAPStoreFlagsRequestKind.IMAPStoreFlagsRequestKindSet,
                MessageFlag.MessageFlagDeleted);
        imapOperation.start(new OperationCallback() {
            @Override
            public void succeeded() {
                //you need to expunge folder after flagged Deleted,
                folderExpunge(folderName, new OperationCallback() {
                    @Override
                    public void succeeded() {
                        callBack.success(null);
                    }

                    @Override
                    public void failed(MailException e) {
                        callBack.fail(e.getMessage());
                    }
                });
            }

            @Override
            public void failed(MailException e) {
                callBack.fail(e.toString());
            }
        });

    }

    protected final void updateFlag(int flag, List<MailBean> mailList, int RequestKind, CommonCallBack<List<List<MailBean>>, String> callback) {
        if (mailList == null || mailList.size() == 0) {
            return;
        }
        HashMap<String, List<MailBean>> map = new HashMap<>();
        for (MailBean mailBean : mailList) {
            if (map.containsKey(mailBean.folderName())) {
                map.get(mailBean.folderName()).add(mailBean);
            } else {
                List<MailBean> list = new ArrayList<>();
                list.add(mailBean);
                map.put(mailBean.folderName(), list);
            }
        }
        MultiCallBack<List<MailBean>> multiCallBack = new MultiCallBack<>(map.size(), callback);

        for (String folder : map.keySet()) {
            List<MailBean> list = map.get(folder);
            IndexSet indexSet = new IndexSet();
            for (MailBean mailBean : list) {
                indexSet.addIndex(mailBean.uid());
            }
            IMAPOperation opt = getImapSession().storeFlagsByUIDOperation(folder, indexSet, RequestKind, flag);
            opt.start(new OperationCallback() {
                @Override
                public void succeeded() {
                    multiCallBack.success(list);
                }

                @Override
                public void failed(MailException e) {
                    multiCallBack.fail(e.getMessage());
                }
            });
        }


    }
}
