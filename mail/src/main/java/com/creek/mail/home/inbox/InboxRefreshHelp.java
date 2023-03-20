package com.creek.mail.home.inbox;


import static com.creek.common.constant.Const.page_size;

import com.creek.common.interfaces.CommonCallBack;
import com.creek.database.DBManager;
import com.creek.sync.MailSync;
import com.libmailcore.IMAPFolderStatus;
import com.libmailcore.IMAPMessage;
import com.libmailcore.IndexSet;
import com.libmailcore.Range;
import com.creek.common.UserInfo;
import com.creek.common.MailBean;


import java.util.ArrayList;
import java.util.List;



public class InboxRefreshHelp {

    /**
     * 获取指定文件夹的首页数据
     *
     * @param folderName 文件夹名称 如收件箱=INBOX
     * @param callBack   结果回调
     */
    public static void refreshMail(final String folderName, CommonCallBack<List<MailBean>,String> callBack) {
        //获取本地数据库最新的首页数据
        final List<MailBean> dbList = DBManager.selectMailList( folderName, page_size);

        if (dbList != null && dbList.size() > 0) {
            callBack.success(dbList);

            MailSync.syncFolderStatus(folderName, new CommonCallBack<IMAPFolderStatus,String>() {
                @Override
                public void success(IMAPFolderStatus folderStatus) {
                    long mailCount = folderStatus.messageCount();
                    long min = mailCount - page_size + 1;
                    if (min < 1) {  //不够一页时最小值取1
                        min = 1;
                    }

                    //获取当前服务端首页的uid
                    MailSync.getMailList(folderName, new Range(min, mailCount),
                            new CommonCallBack<List<IMAPMessage>,String>() {
                                @Override
                                public void success(final List<IMAPMessage> messages) {
                                    if (messages.size() == 0) {
                                        //清空该文件夹下的本地邮件
                                        DBManager.deleteMailByUid(folderName, 0);
                                        callBack.success(new ArrayList<MailBean>());
                                        return;
                                    }

                                    String uids = "";
                                    for (int k = 0; k < messages.size(); k++) {
                                        uids += messages.get(k).uid() + ", ";
                                    }

                                    int remoteSize = messages.size();
                                    int localSize = dbList.size();
                                    if (localSize == remoteSize &&
                                            messages.get(0).uid() == dbList.get(0).uid() &&
                                            messages.get(remoteSize - 1).uid() == dbList.get(localSize - 1).uid()) {

                                        //更新flag
                                        boolean isUpdate = isUpdateFlag(folderName, messages, dbList);

                                        if (isUpdate) {
                                            List<MailBean> tempList = DBManager.selectMailList(folderName, page_size);
                                            setMainPart(messages, tempList);
                                            callBack.success(tempList);
                                        } else {
                                            //服务端没有最新数据
                                            callBack.success(null);
                                        }

                                    } else {
                                        if (messages.get(0).uid() < dbList.get(0).uid()) {
                                            //本地删除大于服务端最新uid的邮件
                                            DBManager.deleteMailByUid( folderName, messages.get(0).uid());
                                        }
                                        //服务端的数据有变化，同步本地数据（新增和删除）
                                        IndexSet indexSet = syncLocalData(messages, UserInfo.userEmail, folderName, messages.get(0).uid());

                                        if (indexSet.count() == 0) {
                                            //若是没有新增的数据，直接从数据库取出指定uid的详情
                                            List<MailBean> tempList = DBManager.selectMailByUID(folderName, messages);
                                            setMainPart(messages, tempList);
                                            callBack.success(tempList);
                                        } else {
                                            //从服务端拉取新增的邮件
                                            MailSync.fetchMailList(folderName, indexSet, new CommonCallBack<List<IMAPMessage>,String>() {
                                                @Override
                                                public void success(List<IMAPMessage> imapMessages) {
                                                    //将新增邮件入库
                                                    DBManager.insertMail(folderName, imapMessages);
                                                    //查询本地首页数据
                                                    List<MailBean> mailInfoList = DBManager.selectMailByUID(folderName, messages);
                                                    if (mailInfoList != null && mailInfoList.size() > 0) {
                                                        setMainPart(messages, mailInfoList);
                                                        callBack.success(mailInfoList);
                                                    } else {
                                                        callBack.fail("本地数据异常");
                                                    }
                                                }

                                                @Override
                                                public void fail(String message) {
                                                    callBack.fail("邮件拉取失败");

                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void fail(String errorStr) {
                                    callBack.fail(errorStr);
                                }
                            });
                }

                @Override
                public void fail(String errorStr) {
                    callBack.fail(errorStr);
                }
            });
        } else {
            //本地没有数据则直接从邮件服务器拉取邮件
            MailSync.syncFolderStatus(folderName, new CommonCallBack<IMAPFolderStatus,String>() {
                @Override
                public void success(IMAPFolderStatus folderStatus) {
                    long mailCount = folderStatus.messageCount();
                    long min = mailCount - page_size + 1;
                    if (min < 1) {  //不够一页时最小值取1
                        min = 1;
                    }

                    MailSync.syncMailList(folderName, new Range(min, page_size - 1),
                            new CommonCallBack<List<IMAPMessage>,String>() {
                                @Override
                                public void success(List<IMAPMessage> messages) {

                                    //插入本地数据
                                    boolean isSuccess = DBManager.insertMail(folderName, messages);
                                    if (!isSuccess) {
                                        callBack.fail("数据库异常");
                                        return;
                                    }
                                    //从数据库中取出数据
                                    List<MailBean> mailInfoList = DBManager.selectMailList( folderName, page_size);
                                    if (mailInfoList != null) {
                                        setMainPart(messages, mailInfoList);
                                        callBack.success(mailInfoList);
                                    } else {
                                        callBack.fail("数据异常");
                                    }
                                }

                                @Override
                                public void fail(String errorStr) {
                                    callBack.fail(errorStr);
                                }
                            });
                }

                @Override
                public void fail(String errorStr) {
                    callBack.fail(errorStr);
                }
            });
        }
    }


    /**
     * 同步本地数据
     *
     * @param messages    服务端数据
     * @param mail_addr   用户邮箱地址
     * @param folder_name 文件夹名称 如收件箱=INBOX
     * @param max_uid     同步uid区间的最大值
     * @return 新增的数据
     */
    private static IndexSet syncLocalData(List<IMAPMessage> messages, String mail_addr, String folder_name, long max_uid) {
        List<IMAPMessage> tempList = new ArrayList<>();
        tempList.addAll(messages);
        IndexSet indexSet = new IndexSet();
        //获取本地指定范围的所有数据(只对比服务端范围内的数据，降低无效对比)
        List<MailBean> mail_local_list = DBManager.selectUidFromMail(folder_name, tempList.get(tempList.size() - 1).uid(), max_uid);

        //本地需要删除的邮件集合
        List<MailBean> need_delete_list = new ArrayList<>();

        if (tempList.size() > 0 && mail_local_list.size() > 0) {

            for (int i = 0; i < mail_local_list.size(); i++) {
                boolean flag = true;  //本地某条数据是否已被删除的标记
                for (int j = 0; j < tempList.size(); j++) {
                    if (mail_local_list.get(i).uid() == tempList.get(j).uid()) {
                        if (mail_local_list.get(i).getEmailFlag() != tempList.get(j).flags()) {
                            //更新flag信息
                            DBManager.updateFlagByUid(folder_name, tempList.get(j).uid(), tempList.get(j).flags());
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
        //本地需要新增的邮件
        if (tempList.size() > 0) {
            String uids = "";
            for (int i = 0; i < tempList.size(); i++) {
                uids += tempList.get(i).uid() + ", ";
                indexSet.addIndex(tempList.get(i).uid());
            }
        } else {
        }

        return indexSet;
    }


    /**
     * 同步本地数据的flag
     *
     * @param messages   服务端数据
     * @param loacl_list 本地数据
     */
    private static boolean isUpdateFlag(String folder_name, List<IMAPMessage> messages, List<MailBean> loacl_list) {
        boolean flag = false;
        for (int i = 0; i < loacl_list.size(); i++) {
            for (int j = 0; j < messages.size(); j++) {
                if (loacl_list.get(i).uid() == messages.get(j).uid()) {

                    if (loacl_list.get(i).getEmailFlag() != messages.get(j).flags()) {
                        //更新flag信息
                        DBManager.updateFlagByUid(folder_name, messages.get(j).uid(), messages.get(j).flags());
                        flag = true;
                    }
                }

            }
        }
        return flag;
    }


    /**
     * 在线情况情况下设置mainpart
     *
     * @param messages  服务端数据
     * @param mailInfos
     */
    private static void setMainPart(List<IMAPMessage> messages, List<MailBean> mailInfos) {

        int len1 = messages.size();
        int len2 = mailInfos.size();
        int i = 0, j = 0;
        while (i < len1 && j < len2) {
            if (messages.get(i).uid() == mailInfos.get(j).uid()) {
                mailInfos.get(j).setMainPart(messages.get(i).mainPart());
                i++;
                j++;
            } else {
                j++;
            }
        }
    }
}
