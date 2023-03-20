package com.creek.mail.home.inbox;

import static com.creek.common.constant.Const.page_size;

import com.creek.common.MailBean;
import com.creek.common.UserInfo;
import com.creek.common.interfaces.CommonCallBack;
import com.creek.database.DBManager;
import com.creek.sync.MailSync;
import com.libmailcore.IMAPMessage;
import com.libmailcore.IndexSet;
import com.libmailcore.Range;

import java.util.ArrayList;
import java.util.List;

public class MoreLoader {


    public static void fetch(final String folderName, final List<MailBean> currentPageMailList,
                             final LoadMore callBack) {

        final List<MailBean> currentMailList = new ArrayList<>();
        currentMailList.addAll(currentPageMailList);
        long tmplast_uid = currentMailList.get(0).uid();
        MailBean tmpLastEmail = currentMailList.get(0);
        for (MailBean _mailBean : currentMailList) {
            if (_mailBean.uid() < tmplast_uid) {
                tmplast_uid = _mailBean.uid();
                tmpLastEmail = _mailBean;
            }
        }
        final MailBean lastEmail = tmpLastEmail;
        final long last_uid = tmplast_uid;
        final List<MailBean> local_mail_list = DBManager.selectMailByUID(folderName, last_uid, page_size);
        callBack.loadMoreDataFromDB(local_mail_list);

        //检查服务端是否有可翻页的数据
        MailSync.getMailList(folderName, new Range(1, 1),
                new CommonCallBack<List<IMAPMessage>, String>() {
                    @Override
                    public void success(List<IMAPMessage> messages) {
                        if (messages.size() == 0) {
                            callBack.loadMoreDataDel(local_mail_list);
                        } else {
                            if (messages.get(0).uid() < last_uid) {
                                //服务端还有下一页数据
                                //同步当前页面中最后一页的数据
                                syncRemoteDataByUid(currentMailList, folderName, new CommonCallBack<List<MailBean>, String>() {
                                    @Override
                                    public void success(List<MailBean> mailInfoList) {
                                        //获取本地下一页数据

                                        if (local_mail_list != null && local_mail_list.size() > 0) {
                                            //与服务端进行数据同步
                                            syncRemoteDataByUid(local_mail_list, folderName, new CommonCallBack<List<MailBean>, String>() {
                                                @Override
                                                public void success(List<MailBean> mailInfoList) {
                                                    callBack.loadMoreDataUpdate(mailInfoList);
                                                }

                                                @Override
                                                public void fail(String errorStr) {
                                                }
                                            });
                                        } else {
                                            //本地没有数据则直接从邮件服务器拉取邮件
//                                            long max = currentMailList.get(currentMailList.size() - 1).getEmail_sque() - 1;
//                                            long min = max - MailConstant.page_size + 1;
                                            long min = lastEmail.getSequence() - page_size;
                                            if (min < 1) {
                                                min = 1;
                                            }
                                            MailSync.syncMailList(folderName, new Range(min, page_size - 1),
                                                    new CommonCallBack<List<IMAPMessage>, String>() {
                                                        @Override
                                                        public void success(List<IMAPMessage> messages) {

                                                            //插入本地数据
                                                            boolean isSuccess = DBManager.insertMail(folderName, messages);
                                                            if (!isSuccess) {
                                                                return;
                                                            }
                                                            //从数据库中取出数据
                                                            List<MailBean> mailInfoList = DBManager.selectMailByUID(folderName, messages);
                                                            if (mailInfoList != null) {
                                                                setMessageMainPart(messages, mailInfoList);
                                                                callBack.loadMoreDataAdd(mailInfoList);
                                                            }
                                                        }

                                                        @Override
                                                        public void fail(String errorStr) {
                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void fail(String errorStr) {
                                    }
                                });
                            } else {
                                //服务端没有下一页数据，返回一个空的集合
                                callBack.loadMoreDataNone();
                            }
                        }
                    }

                    @Override
                    public void fail(String errorStr) {
                    }
                });

    }


    private static void syncRemoteDataByUid(final List<MailBean> currentPageMailList, final String folder_name, CommonCallBack<List<MailBean>, String> callback) {

        final List<MailBean> tempList = new ArrayList<>();
        IndexSet indexSet = new IndexSet();
        long tempUid = 0;
        //获取要同步的uid
        if (currentPageMailList.size() >= page_size) {
            for (int i = currentPageMailList.size() - page_size; i < currentPageMailList.size(); i++) {
                tempUid = currentPageMailList.get(i).uid();
                indexSet.addIndex(tempUid);
                tempList.add(currentPageMailList.get(i));
            }
        } else {
            for (int i = 0; i < currentPageMailList.size(); i++) {
                tempUid = currentPageMailList.get(i).uid();
                indexSet.addIndex(tempUid);
                tempList.add(currentPageMailList.get(i));
            }
        }

        if (indexSet.count() == 0) {
            return;
        }

        MailSync.obtMailList(folder_name, indexSet, new CommonCallBack<List<IMAPMessage>, String>() {
            @Override
            public void success(final List<IMAPMessage> messages) {

                if (messages.size() == 0) {  //可能由于服务端已将该页删除，需要继续向下查找
                    currentPageMailList.removeAll(tempList);
                    DBManager.deleteMailByUid(folder_name, tempList);
                    if (currentPageMailList.size() == 0) {
                        currentPageMailList.clear();
                    } else {
                        syncRemoteDataByUid(currentPageMailList, folder_name, callback);
                    }
                } else {

                    //服务端的数据有变化，同步本地数据（更新flag和删除）
                    List<MailBean> after_sync_List = syncLocalData(messages, tempList, UserInfo.userEmail, folder_name);

                    currentPageMailList.removeAll(tempList);
                    currentPageMailList.addAll(after_sync_List);
                }

                callback.success(currentPageMailList);
            }

            @Override
            public void fail(String errorStr) {
                callback.fail(errorStr);
            }
        });
    }


    private static List<MailBean> syncLocalData(List<IMAPMessage> messages, List<MailBean> mail_local_list, String mail_addr, String folder_name) {
        List<IMAPMessage> tempList = new ArrayList<>();
        tempList.addAll(messages);

        //本地需要删除的邮件集合
        List<MailBean> need_delete_list = new ArrayList<>();

        if (tempList.size() > 0 && mail_local_list.size() > 0) {

            for (int i = 0; i < mail_local_list.size(); i++) {
                boolean flag = true;  //本地某条数据是否已被删除的标记
                for (int j = 0; j < tempList.size(); j++) {
                    if (mail_local_list.get(i).uid() == tempList.get(j).uid()) {
                        if (mail_local_list.get(i).getEmailFlag() != tempList.get(j).flags()) {
                            //更新数据库中的flag信息
                            DBManager.updateFlagByUid(folder_name, tempList.get(j).uid(), tempList.get(j).flags());
                            mail_local_list.get(i).setFolderFlag(tempList.get(j).flags());
                        }

                        tempList.remove(j);
                        j--;
                        flag = false;
                        continue;
                    }
                }
                if (tempList.size() == 0) {
                    break;
                }

                if (flag) {
                    need_delete_list.add(mail_local_list.get(i));
                }
            }

            //删除需要删除的数据
            if (need_delete_list.size() > 0) {
                DBManager.deleteMailByUid(folder_name, need_delete_list);
            } else {
            }
        }

        return mail_local_list;
    }


    private static void setMessageMainPart(List<IMAPMessage> messages, List<MailBean> mailList) {

        for (MailBean mail : mailList) {
            for (IMAPMessage msg : messages) {
                if (mail.uid() == msg.uid()) {
                    mail.setMainPart(msg.mainPart());
                    break;
                }
            }
        }
    }
}
