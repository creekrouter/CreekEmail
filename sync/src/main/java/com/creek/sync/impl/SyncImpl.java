package com.creek.sync.impl;

import static com.libmailcore.IMAPMessagesRequestKind.IMAPMessagesRequestKindInternalDate;

import android.text.TextUtils;

import com.creek.common.CreekPath;
import com.creek.common.MailAttachment;
import com.creek.common.MailBean;
import com.creek.common.MailFolder;
import com.creek.common.constant.ConstPath;
import com.creek.common.interfaces.CommonCallBack;
import com.creek.common.interfaces.ConfirmCallBack;
import com.creek.router.CreekRouter;
import com.creek.sync.R;
import com.creek.sync.callback.MultiCallBack;
import com.creek.sync.imap.ImapManager;
import com.creek.sync.interfaces.FolderSync;
import com.creek.sync.interfaces.MailAttachSync;
import com.creek.sync.interfaces.MailDetailSync;
import com.creek.sync.interfaces.MailHeadSync;
import com.creek.sync.interfaces.MailListSync;
import com.creek.sync.interfaces.MailPlainSync;
import com.creek.sync.interfaces.MailStatusSync;
import com.libmailcore.AbstractPart;
import com.libmailcore.IMAPFetchContentOperation;
import com.libmailcore.IMAPFetchFoldersOperation;
import com.libmailcore.IMAPFetchMessagesOperation;
import com.libmailcore.IMAPFetchParsedContentOperation;
import com.libmailcore.IMAPFolder;
import com.libmailcore.IMAPFolderFlags;
import com.libmailcore.IMAPFolderStatus;
import com.libmailcore.IMAPFolderStatusOperation;
import com.libmailcore.IMAPMessage;
import com.libmailcore.IMAPMessageRenderingOperation;
import com.libmailcore.IMAPMessagesRequestKind;
import com.libmailcore.IMAPPart;
import com.libmailcore.IMAPStoreFlagsRequestKind;
import com.libmailcore.IndexSet;
import com.libmailcore.MailException;
import com.libmailcore.MessageParser;
import com.libmailcore.OperationCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SyncImpl extends BaseSync implements FolderSync, MailListSync, MailHeadSync, MailDetailSync, MailPlainSync, MailStatusSync, MailAttachSync {


    @Override
    public void syncFolderStatus(String folderName, CommonCallBack<IMAPFolderStatus, String> callback) {
        IMAPFolderStatusOperation folderOpt = getImapSession().folderStatusOperation(folderName);
        folderOpt.start(new OperationCallback() {
            @Override
            public void succeeded() {
                IMAPFolderStatus status = folderOpt.status();
                if (status != null) {
                    callback.success(status);
                } else {
                    callback.fail(MailError.IMAPFolderStatus);
                }
            }

            @Override
            public void failed(MailException e) {
                callback.fail(e.toString());
            }
        });
    }

    @Override
    public void syncFolderList(String userEmail, CommonCallBack<List<MailFolder>, String> callBack) {
        IMAPFetchFoldersOperation foldersOpt = getImapSession().fetchAllFoldersOperation();
        foldersOpt.start(new OperationCallback() {
            @Override
            public void succeeded() {
                List<IMAPFolder> folders = foldersOpt.folders();
                if (folders.size() == 0) {
                    return;
                }
                dealWithFolderList(userEmail, folders, callBack);

//                if (ImapManager.singleton().getSession().defaultNamespace() == null) {
//                    dealWithNameSpace(userEmail, folders, callBack);
//                } else {
//                }
            }

            @Override
            public void failed(MailException e) {
                callBack.fail(e.toString());
            }
        });
    }

    @Override
    public void syncMailList(String folderName, IndexSet indexSet, CommonCallBack<List<IMAPMessage>, String> callBack) {
        IMAPFetchMessagesOperation fetchMessagesOp = getImapSession().fetchMessagesByNumberOperation(folderName,
                IMAPMessagesRequestKind.IMAPMessagesRequestKindUid |
                        IMAPMessagesRequestKind.IMAPMessagesRequestKindHeaders |
                        IMAPMessagesRequestKind.IMAPMessagesRequestKindStructure |
                        IMAPMessagesRequestKind.IMAPMessagesRequestKindFlags |
                        IMAPMessagesRequestKindInternalDate, indexSet);
        fetchMessageList(fetchMessagesOp, callBack, MailError.IMAPMessageList);
    }

    @Override
    public void fetchMailList(String folderName, IndexSet indexSet, CommonCallBack<List<IMAPMessage>, String> callBack) {
        IMAPFetchMessagesOperation fetchMessagesOp = getImapSession().fetchMessagesByUIDOperation(folderName,
                IMAPMessagesRequestKind.IMAPMessagesRequestKindUid |
                        IMAPMessagesRequestKind.IMAPMessagesRequestKindHeaders |
                        IMAPMessagesRequestKind.IMAPMessagesRequestKindStructure |
                        IMAPMessagesRequestKind.IMAPMessagesRequestKindFlags |
                        IMAPMessagesRequestKindInternalDate, indexSet);
        fetchMessageList(fetchMessagesOp, callBack, MailError.IMAPMessageList);
    }

    @Override
    public void getMailList(String folderName, IndexSet indexSet, CommonCallBack<List<IMAPMessage>, String> callBack) {
        IMAPFetchMessagesOperation fetchMessagesOp = getImapSession().fetchMessagesByNumberOperation(folderName,
                IMAPMessagesRequestKind.IMAPMessagesRequestKindUid |
                        IMAPMessagesRequestKind.IMAPMessagesRequestKindFlags,
                indexSet);
        fetchMessageList(fetchMessagesOp, callBack, MailError.IMAPMessageList);
    }

    @Override
    public void obtMailList(String folderName, IndexSet indexSet, CommonCallBack<List<IMAPMessage>, String> callBack) {
        final IMAPFetchMessagesOperation fetchMessagesOp = ImapManager.singleton().getSession().fetchMessagesByUIDOperation(folderName,
                IMAPMessagesRequestKind.IMAPMessagesRequestKindUid | IMAPMessagesRequestKind.IMAPMessagesRequestKindFlags, indexSet);
        fetchMessageList(fetchMessagesOp, callBack, MailError.IMAPMessageList);
    }


    @Override
    public void syncMailHead(String folderName, IndexSet indexSet, CommonCallBack<List<IMAPMessage>, String> callBack) {
        IMAPFetchMessagesOperation fetchMessagesOp = getImapSession().fetchMessagesByUIDOperation(folderName,
                IMAPMessagesRequestKind.IMAPMessagesRequestKindUid |
                        IMAPMessagesRequestKind.IMAPMessagesRequestKindHeaders |
                        IMAPMessagesRequestKind.IMAPMessagesRequestKindStructure |
                        IMAPMessagesRequestKind.IMAPMessagesRequestKindFlags |
                        IMAPMessagesRequestKind.IMAPMessagesRequestKindInternalDate, indexSet);
        fetchMessageList(fetchMessagesOp, callBack, MailError.IMAPMessageHeadList);
    }

    @Override
    public void syncMailDetails(String folderName, long mailUid, CommonCallBack<List<IMAPMessage>, String> callBack) {
        IndexSet indexSet = new IndexSet();
        indexSet.addIndex(mailUid);
        IMAPFetchMessagesOperation fetchMessagesOp = getImapSession().syncMessagesByUIDOperation(folderName,
                IMAPMessagesRequestKind.IMAPMessagesRequestKindUid |
                        IMAPMessagesRequestKind.IMAPMessagesRequestKindStructure |
                        IMAPMessagesRequestKind.IMAPMessagesRequestKindFlags |
                        IMAPMessagesRequestKind.IMAPMessagesRequestKindInternalDate,
                indexSet, 0);
        fetchMessageList(fetchMessagesOp, callBack, MailError.IMAPMessageMailDetail);
    }

    @Override
    public void syncMailPlain(AbstractPart mainPart, String folderName, long mailUid, ConfirmCallBack<String> callBack) {
        IMAPMessage message = new IMAPMessage();
        message.setUid(mailUid);
        message.setMainPart(mainPart);
        IMAPMessageRenderingOperation renderingOperation = getImapSession().plainTextBodyRenderingOperation(message, folderName, true);
        renderingOperation.start(new OperationCallback() {
            @Override
            public void succeeded() {
                String plain = renderingOperation.result();
                callBack.onConfirm(plain);
            }

            @Override
            public void failed(MailException e) {

            }
        });
    }

    @Override
    public void getMailPlain(String folderName, long mailUid, ConfirmCallBack<String> callBack) {

        IMAPFetchParsedContentOperation contentOperationOp = ImapManager.singleton().getSession().
                fetchParsedMessageByUIDOperation(folderName, mailUid, true);
        contentOperationOp.start(new OperationCallback() {
            @Override
            public void succeeded() {
                MessageParser messageParser = contentOperationOp.parser();
                String text = messageParser.plainTextBodyRendering(true);
                callBack.onConfirm(text);
            }

            @Override
            public void failed(MailException e) {

            }
        });
    }

    @Override
    public void messageDeleted(List<MailBean> mailList, CommonCallBack<Void, String> callBack) {
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

        MultiCallBack<List<MailBean>> multiCallBack = new MultiCallBack<>(map.size(), new CommonCallBack<List<List<MailBean>>, String>() {
            @Override
            public void success(List<List<MailBean>> lists) {
                if (lists == null || lists.size() == 0) {
                    return;
                }
                int count = 0;
                for (List<MailBean> itemList : lists) {
                    count = count + itemList.size();
                }
                if (count == mailList.size()) {
                    callBack.success(null);
                } else {
                    callBack.fail("操作部分成功！");
                }
            }

            @Override
            public void fail(String s) {
                callBack.fail(s);
            }
        });


        String trashFolder = CreekRouter.methodRun(CreekPath.SQL_Get_Folder_Name_By_ImapFlag, getImapSession().username(), IMAPFolderFlags.IMAPFolderFlagTrash);
        String draftsFolder = CreekRouter.methodRun(CreekPath.SQL_Get_Folder_Name_By_ImapFlag, getImapSession().username(), IMAPFolderFlags.IMAPFolderFlagDrafts);

        for (String folder : map.keySet()) {
            List<MailBean> list = map.get(folder);
            IndexSet indexSet = new IndexSet();
            for (MailBean mailBean : list) {
                indexSet.addIndex(mailBean.uid());
            }
            if (folder.equals(trashFolder) || folder.equals(draftsFolder)) {
                basicMailDel(folder, indexSet, new CommonCallBack<Void, String>() {
                    @Override
                    public void success(Void unused) {
                        //data base
                        CreekRouter.methodRun(CreekPath.SQL_Delete_Message_List, folder, list);
                        multiCallBack.success(list);
                    }

                    @Override
                    public void fail(String s) {
                        multiCallBack.fail(s);
                    }
                });
            } else {
                messageMove(trashFolder, list, new CommonCallBack<Void, String>() {
                    @Override
                    public void success(Void unused) {
                        multiCallBack.success(list);
                    }

                    @Override
                    public void fail(String s) {
                        multiCallBack.fail(s);
                    }
                });
            }
        }

    }

    @Override
    public void messageMove(String desFolder, List<MailBean> mailList, CommonCallBack<Void, String> callBack) {
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
        MultiCallBack<List<MailBean>> multiCallBack = new MultiCallBack<>(map.size(), new CommonCallBack<List<List<MailBean>>, String>() {
            @Override
            public void success(List<List<MailBean>> lists) {
                if (lists == null || lists.size() == 0) {
                    return;
                }
                int count = 0;
                for (List<MailBean> itemList : lists) {
                    count = count + itemList.size();
                }
                if (count == mailList.size()) {
                    callBack.success(null);
                } else {
                    callBack.fail("操作部分成功！");
                }
            }

            @Override
            public void fail(String s) {
                callBack.fail(s);
            }
        });

        for (String folder : map.keySet()) {
            List<MailBean> list = map.get(folder);
            IndexSet indexSet = new IndexSet();
            for (MailBean mailBean : list) {
                indexSet.addIndex(mailBean.uid());
            }


            basicMailMove(folder, desFolder, indexSet, new CommonCallBack<Void, String>() {
                @Override
                public void success(Void unused) {
                    basicMailDel(folder, indexSet, new CommonCallBack<Void, String>() {
                        @Override
                        public void success(Void unused) {
                            CreekRouter.methodRun(CreekPath.SQL_Update_Message_List_Folder_Name, desFolder, list);
                            multiCallBack.success(list);
                        }

                        @Override
                        public void fail(String message) {
                            multiCallBack.fail(message);
                        }
                    });
                }

                @Override
                public void fail(String message) {
                    multiCallBack.fail(message);
                }
            });

        }


    }

    @Override
    public void messageUpdateFlag(int requestKind, int flag, List<MailBean> mailList, CommonCallBack<Void, String> callBack) {

        updateFlag(flag, mailList, requestKind, new CommonCallBack<List<List<MailBean>>, String>() {
            @Override
            public void success(List<List<MailBean>> lists) {
                if (lists == null || lists.size() == 0) {
                    return;
                }
                int count = 0;
                for (List<MailBean> mailList : lists) {
                    for (int i = 0; i < mailList.size(); i++) {
                        count++;
                        int newFlag;
                        if (requestKind == IMAPStoreFlagsRequestKind.IMAPStoreFlagsRequestKindAdd) {
                            newFlag = mailList.get(i).getEmailFlag() | flag;
                        } else if (requestKind == IMAPStoreFlagsRequestKind.IMAPStoreFlagsRequestKindRemove) {
                            newFlag = mailList.get(i).getEmailFlag() & ~flag;
                        } else {
                            newFlag = flag;
                        }
                        //更新本地flag信息
                        CreekRouter.methodRun(CreekPath.SQL_Update_Message_ImapFlag, mailList.get(i).folderName(), mailList.get(i).uid(), newFlag);
                    }
                }
                if (count == mailList.size()) {
                    callBack.success(null);
                } else {
                    callBack.fail("操作部分成功！");
                }
            }

            @Override
            public void fail(String s) {
                callBack.fail(s);
            }
        });
    }

    @Override
    public void fetchAttach(MailAttachment attach, CommonCallBack<MailAttachment, String> callBack) {
        IMAPFetchContentOperation fetchContentOp = ImapManager.singleton().getSession().
                fetchMessageAttachmentByUIDOperation(attach.getEmail_folder(),
                        attach.getEmail_uid(), attach.getPart_id(), attach.getEncoding());

        fetchContentOp.start(new OperationCallback() {
            @Override
            public void succeeded() {
                final byte[] data = fetchContentOp.data();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        //保存到本地
                        String path = CreekRouter.methodRun(CreekPath.Tools_Save_File_To_SdCard, data, ConstPath.getAttach(attach));
                        attach.setFile_path(path);
                        callBack.success(attach);
                    }
                };
                CreekRouter.methodRun(CreekPath.Tools_Thread_Pool_Execute_Runnable, runnable);
            }

            @Override
            public void failed(MailException e) {
                callBack.fail(e.getMessage());
            }
        });
    }

    @Override
    public void fetchHtmlPhoto(String photoCID, MailBean mailBean, CommonCallBack<HashMap<String, String>, String> callBack) {
        String photoCIDVal = photoCID.replace("cid:", "");
        IMAPPart imapPart = (IMAPPart) mailBean.getMainPart().partForContentID(photoCIDVal);
        String fileName = imapPart.filename();
        if (fileName == null || fileName.length() == 0) {
            fileName = photoCIDVal;
        }
        IMAPFetchContentOperation fetchContentOp = getImapSession().
                fetchMessageAttachmentByUIDOperation(mailBean.folderName(), mailBean.uid(), imapPart.partID(), imapPart.encoding());

        String finalFileName = fileName;
        fetchContentOp.start(new OperationCallback() {
            @Override
            public void succeeded() {
                byte[] data = fetchContentOp.data();
                File imgFile = ConstPath.getMailBodyImg(mailBean, finalFileName);

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        String path = CreekRouter.methodRun(CreekPath.Tools_Save_File_To_SdCard, data, imgFile);
                        if (path == null || path.length() == 0) {
                            callBack.fail("html cid photo save error!");
                        } else {
                            HashMap<String, String> map = new HashMap<>();
                            map.put(photoCID, path);
                            callBack.success(map);
                        }
                    }
                };

                CreekRouter.methodRun(CreekPath.Tools_Thread_Pool_Execute_Runnable, runnable);

            }

            @Override
            public void failed(MailException e) {
                callBack.fail("html cid photo save error!");
            }
        });
    }

    @Override
    public void fetchHtmlPhotoArr(String[] cidArr, MailBean mailBean, CommonCallBack<HashMap<String, String>, String> callBack) {
        if (cidArr == null || cidArr.length == 0) {
            return;
        }

        MultiCallBack<HashMap<String, String>> multiCallBack = new MultiCallBack<>(cidArr.length, new CommonCallBack<List<HashMap<String, String>>, String>() {
            @Override
            public void success(List<HashMap<String, String>> hashMapList) {
                if (hashMapList == null || hashMapList.size() == 0) {
                    return;
                }
                HashMap<String, String> map = new HashMap<>();
                for (HashMap<String, String> item : hashMapList) {
                    map.putAll(item);
                }
                callBack.success(map);
            }

            @Override
            public void fail(String s) {
                callBack.fail(s);
            }
        });

        for (String cid : cidArr) {
            fetchHtmlPhoto(cid, mailBean, new CommonCallBack<HashMap<String, String>, String>() {
                @Override
                public void success(HashMap<String, String> hashMap) {
                    multiCallBack.success(hashMap);
                }

                @Override
                public void fail(String s) {
                    multiCallBack.fail(s);
                }
            });
        }
    }
}
