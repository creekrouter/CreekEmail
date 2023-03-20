package com.creek.mail.home.inbox;


import android.os.Bundle;
import android.os.Message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.creek.common.CreekPath;
import com.creek.common.interfaces.CommonCallBack;
import com.creek.common.loadview.LoadingLine;
import com.creek.mail.sync.MailUpdater;
import com.creek.mail.pop.Popup;
import com.creek.router.CreekRouter;
import com.creek.sync.MailSync;
import com.mail.tools.ThreadPool;
import com.libmailcore.MessageFlag;
import com.mail.tools.MailToast;
import com.creek.mail.R;
import com.creek.mail.home.manage.HomeManager;
import com.creek.database.DBManager;
import com.creek.common.MailFolder;
import com.creek.common.MailBean;
import com.creek.mail.home.msg.EventID;
import com.creek.mail.home.msg.EventListen;
import com.creek.common.constant.Const;
import com.creek.mail.home.swipe.OnSwipeMenuItemClick;
import com.creek.mail.home.swipe.SwipeMenu;


import java.util.ArrayList;
import java.util.List;


public class InboxFragment extends InboxBaseFragment implements EventListen,
        LoadMore, View.OnClickListener, OnSwipeMenuItemClick {

    public InboxFragment(HomeManager manager) {
        super(manager);
        manager.addRegister(this);
    }


    private String folder_name = "INBOX";
    private String folder_name_cn = "收件箱";

    private LoadingLine progressBar;

    List<MailBean> selectedMail = null;

    private TextView tvSelectCancle;
    private View tvSelectSeen;
    private View tvSelectFlagged, tvSelectDelete, tvSelectMove;
    private View tvSelectSeenUnflagged;
    private View tvUnRead;

    private SortTools sortTools = new SortTools();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.inbox_sync_no_mail_layout, null);
        manager.sendEvent(EventID.View_Init);

        initView();
        CreekRouter.functionRun(CreekPath.Mail_BroadCast_Inbox_Folder_Changed, folder_name);
        refreshMail();
        return mRootView;
    }
    


    private void loadListByMailType(MailFolder folder) {
        if (!folder.getFolder_name_en().equals(folder_name)) {
            manager.getInboxListManager().getInboxList().clear();
            notifyDataSetChanged();
        }
        folder_name = folder.getFolder_name_en();
        folder_name_cn = folder.getFolder_name_ch();
        //清空老数据 重新加载--加载前清空列表，未通知adapter
        manager.getInboxListManager().getInboxList().clear();

        if (ifRedFlagFolder()) {
            ThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    final List<MailBean> results = DBManager.selectFlaggedMail();
                    manager.mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            loadRedFlagMails(results);
                        }
                    });
                }
            });
        } else {
            //更新列表数据
            refreshMail();
        }

    }

    private List<MailBean> tmpDBList = new ArrayList<>();

    @Override
    public void loadMoreDataFromDB(List<MailBean> mailInfoList) {
        if (mailInfoList == null || mailInfoList.size() == 0) {
            return;
        }
        manager.sendEvent(EventID.Inbox_List_Stop_LoadMore);
        if (mailInfoList.get(0).folderName().equals(folder_name)) {
            sortTools.setMinLocalUid(mailInfoList.get(mailInfoList.size() - 1).uid());
            sortTools.insertList(manager.getInboxListManager().getInboxList(), mailInfoList);
            notifyDataSetChanged();
            tmpDBList.clear();
            tmpDBList.addAll(mailInfoList);
        }
    }

    @Override
    public void loadMoreDataDel(List<MailBean> delList) {
        if (delList == null || delList.size() == 0) {
            manager.sendEvent(EventID.Inbox_List_Stop_LoadMore);
            return;
        }
        if (delList.get(0).folderName().equals(folder_name)) {
            sortTools.deleteList(manager.getInboxListManager().getInboxList(), delList);
            notifyDataSetChanged();
        }
    }

    @Override
    public void loadMoreDataAdd(List<MailBean> addList) {
        if (addList == null || addList.size() == 0) {
            return;
        }
        manager.sendEvent(EventID.Inbox_List_Stop_LoadMore);
        if (addList.get(0).folderName().equals(folder_name)) {
            sortTools.insertList(manager.getInboxListManager().getInboxList(), addList);
            notifyDataSetChanged();
        }
    }

    @Override
    public void loadMoreDataUpdate(List<MailBean> updateList) {
        if (updateList == null || updateList.size() == 0) {
            return;
        }
        if (updateList.get(0).folderName().equals(folder_name)) {
            sortTools.deleteList(manager.getInboxListManager().getInboxList(), tmpDBList);
            sortTools.insertList(manager.getInboxListManager().getInboxList(), updateList);
            notifyDataSetChanged();
        }
    }

    @Override
    public void loadMoreDataNone() {
        MailToast.show("没有更多数据");
        manager.sendEvent(EventID.Inbox_List_Stop_LoadMore);
    }


    private void initView() {
        progressBar = findViewById(R.id.loading_line);
        tvSelectCancle = findViewById(R.id.tv_select_cancle_mail_title);
        tvSelectSeen = findViewById(R.id.tv_seen_mail_select);
        tvSelectSeenUnflagged = findViewById(R.id.tv_unflagged_mail_select);
        tvUnRead = findViewById(R.id.tv_unread_mail_select);
        tvSelectFlagged = findViewById(R.id.tv_flagged_mail_select);
        tvSelectDelete = findViewById(R.id.tv_flagged_mail_delete);
        tvSelectMove = findViewById(R.id.tv_flagged_mail_move);

        tvSelectSeenUnflagged.setOnClickListener(this);
        tvSelectFlagged.setOnClickListener(this);
        tvUnRead.setOnClickListener(this);
        tvSelectSeen.setOnClickListener(this);
        tvSelectMove.setOnClickListener(this);
        tvSelectCancle.setOnClickListener(this);

        tvSelectDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMail = getSelectedMail();

                boolean hasTrashMail = false;
                for (MailBean tmpMail : selectedMail) {
                    if ("Trash".equals(tmpMail.folderName())) {
                        hasTrashMail = true;
                        break;
                    }
                }
                if (hasTrashMail) {
                    Popup.deleteMail(null, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteSelectedMails();
                        }
                    });
                } else {
                    deleteSelectedMails();
                }

            }
        });
    }

    private void deleteSelectedMails() {
        if (selectedMail.size() == 0) {
            return;
        }
        List<Long> uidList = new ArrayList<>();
        for (MailBean info : selectedMail) {
            uidList.add(info.uid());
        }

        MailSync.messageDeleted(selectedMail, new CommonCallBack<Void,String>() {
            @Override
            public void success(Void unused) {
                manager.sendEvent(EventID.Inbox_Status_Normal);
                manager.getInboxListManager().getInboxList().removeAll(selectedMail);
                refreshInboxListView("删除成功");
            }

            @Override
            public void fail(String message) {
                MailToast.show("删除失败");
            }
        });
    }


    private void refreshInboxListView(String Toast) {
        notifyDataSetChanged();
        MailToast.show(Toast);
        manager.sendEvent(EventID.Inbox_Status_Normal);
    }


    private List<MailBean> getFinalSelectedMails() {
        selectedMail = getSelectedMail();
        return selectedMail;
    }

    private List<MailBean> getSelectedMail() {
        List<MailBean> infos = new ArrayList<>();
        for (MailBean info : manager.getInboxListManager().getInboxList()) {
            if (info.isSelected()) {
                infos.add(info);
            }
        }
        return infos;
    }


    private void setSelectFooterMenueState() {
        if (Const.DRAFTS.equals(folder_name_cn)) {
            tvSelectSeen.setEnabled(false);
            tvSelectMove.setEnabled(false);
            tvSelectFlagged.setEnabled(false);
        } else if (Const.FLAGED.equals(folder_name_cn)) {
            tvSelectSeen.setEnabled(true);
            tvSelectMove.setEnabled(true);
            tvSelectFlagged.setEnabled(true);
        } else if (Const.SENT.equals(folder_name_cn)) {
            tvSelectSeen.setEnabled(true);
            tvSelectMove.setEnabled(false);
            tvSelectFlagged.setEnabled(true);
        } else {
            tvSelectSeen.setEnabled(true);
            tvSelectMove.setEnabled(true);
            tvSelectFlagged.setEnabled(true);
        }
    }

    private void refreshMail() {
        progressBar.show();

        InboxRefreshHelp.refreshMail(folder_name, new CommonCallBack<List<MailBean>,String>() {
            @Override
            public void success(List<MailBean> mailInfoList) {
                manager.sendEvent(EventID.Inbox_List_Stop_Refresh);
                progressBar.hide();
                MailToast.show("邮件更新完毕");

                if (mailInfoList == null) {
                    return;
                }

                if (mailInfoList.size() == 0) {
                    manager.getInboxListManager().getInboxList().clear();
                    notifyDataSetChanged();
                    return;
                }
                //refresh unread count from DB
                manager.sendEvent(EventID.Sync_Folder_UnRead_Count_Local);

                if (mailInfoList.get(0).folderName().equals(folder_name)) {
                    manager.getInboxListManager().getInboxList().clear();
                    sortTools.insertList(manager.getInboxListManager().getInboxList(), mailInfoList);
                }

                notifyDataSetChanged();

                CreekRouter.functionRun(CreekPath.Fetch_History_Lost_Mail, new CommonCallBack<List<MailBean>,String>() {
                    @Override
                    public void success(List<MailBean> messages) {
                        if (messages == null || messages.size() == 0) {
                            return;
                        }
                        if (!folder_name.equals(messages.get(0).folderName())) {
                            return;
                        }
                        if (manager.getInboxListManager().getInboxList().size() > 49 && messages.get(0).uid() < sortTools.getMinLocalUid()) {
                            return;
                        }
                        sortTools.insertList(manager.getInboxListManager().getInboxList(), messages);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void fail(String message) {

                    }
                });

            }

            @Override
            public void fail(String errorStr) {
                manager.sendEvent(EventID.Inbox_List_Stop_Refresh);
                progressBar.hide();
                MailToast.show(errorStr);
            }
        });
    }

    public void notifyDataSetChanged() {
        //页面显示时，需要刷新当前页面
        manager.sendEvent(EventID.Inbox_List_Notify_Data_Changed);
        manager.sendEvent(EventID.Inbox_Empty_View_Refresh, manager.getInboxListManager().getInboxList().size());

        //refresh unread count from DB
        manager.sendEvent(EventID.Sync_Folder_UnRead_Count_Local);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_select_cancle_mail_title) {
            manager.sendEvent(EventID.Inbox_Status_Normal);
        } else if (id == R.id.tv_seen_mail_select) {
            setSelectedMailSeen();
        } else if (id == R.id.tv_unread_mail_select) {
            setSelectedUnread();
        } else if (id == R.id.tv_flagged_mail_move) {
            MailUpdater.move(getFinalSelectedMails());
        } else if (id == R.id.tv_unflagged_mail_select) {
//            mRedFlagHelper.cancelRedFlagMails(getFinalSelectedMails());
        } else if (id == R.id.tv_flagged_mail_select) {
//            mRedFlagHelper.redFlagMails(getFinalSelectedMails());
        }
    }


    private void setSelectedMailSeen() {

        MailSync.messageFlagAdd(MessageFlag.MessageFlagSeen, getSelectedMail(), new CommonCallBack<Void, String>() {
            @Override
            public void success(Void unused) {
                for (MailBean info : getSelectedMail()) {
                    info.setEmailFlag(info.getEmailFlag() | MessageFlag.MessageFlagSeen);
                }
                refreshInboxListView("标记成功");
            }

            @Override
            public void fail(String s) {
                MailToast.show("标记失败");
            }
        });
    }

    private void setSelectedUnread() {

        MailSync.messageFlagRemove(MessageFlag.MessageFlagSeen, getSelectedMail(), new CommonCallBack<Void, String>() {
            @Override
            public void success(Void unused) {
                for (MailBean info : getSelectedMail()) {
                    info.setEmailFlag(info.getEmailFlag() & ~MessageFlag.MessageFlagSeen);
                }
                refreshInboxListView("标记成功");
            }

            @Override
            public void fail(String s) {
                MailToast.show("标记失败");
            }
        });
    }


    private boolean ifRedFlagFolder() {
        return Const.FLAGED_EN.equals(folder_name);
    }



    public void loadRedFlagMails(List<MailBean> redFlagMails) {
        manager.getInboxListManager().getInboxList().clear();
        if (redFlagMails == null || redFlagMails.size() == 0) {
            MailToast.show("暂时没有数据");
        } else {
            sortTools.insertList(manager.getInboxListManager().getInboxList(), redFlagMails);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onMenuItemClick(int position, SwipeMenu menu, int index) {
//        final MailBean mailInfo = manager.getInboxListManager().getInboxList().get(position);
//        //邮件item 向左边侧滑 操作
//        switch (index) {
//            case 0://移动
//                mMoveMail.usePopWindowMoveMail(mailInfo);
//                break;
//            case 1://标记已读
//                mailActionSender.sendOperateAction(getContext(), MailOperate.Operate.mark_read, mailInfo, true);
//                break;
//            case 2://标记为未读
//                mailActionSender.sendOperateAction(getContext(), MailOperate.Operate.cancel_read, mailInfo, true);
//                break;
//            case 3://取消标记红旗
//                mailActionSender.sendOperateAction(getContext(), MailOperate.Operate.cancel_redFlag, mailInfo, true);
//                break;
//            case 4://红旗标记
//                mailActionSender.sendOperateAction(getContext(), MailOperate.Operate.mark_redFlag, mailInfo, true);
//                break;
//            case 5://删除
//                if ("Trash".equals(manager.getInboxListManager().getInboxList().get(position).getEmail_folder())) {
//                    PopUtils.showCancelConfirmPop(getContext(), "确定删除该邮件?", "取消", "确定", new CreekPopWindow.OnConfirmPopClickListener() {
//                        @Override
//                        public void onRightClick() {
//                            mailActionSender.sendOperateAction(getContext(), MailOperate.Operate.delete_mail, mailInfo, true);
//                        }
//                    });
//                } else {
//                    mailActionSender.sendOperateAction(getContext(), MailOperate.Operate.delete_mail, mailInfo, true);
//                }
//                break;
//
//        }
    }


//    public void onReceiveOperateEvent(MailOperate.OperateBean bean) {
//        if (bean.mailBean == null) {
//            return;
//        }
//        MailBean mail = bean.mailBean;
//        switch (bean.operateType) {
//            case mark_redFlag:
//                mail.setEmail_flag(mail.getEmail_flag() | MessageFlag.MessageFlagFlagged);
//                break;
//            case cancel_redFlag:
//                mail.setEmail_flag(mail.getEmail_flag() & ~MessageFlag.MessageFlagFlagged);
//                break;
//            case delete_mail:
//                manager.getInboxListManager().getInboxList().remove(mail);
//                break;
//            case mark_read:
//                mail.setEmail_flag(mail.getEmail_flag() | MessageFlag.MessageFlagSeen);
//                manager.sendEvent(EventID.Sync_Folder_UnRead_Count_Server);
//                break;
//            case cancel_read:
//                mail.setEmail_flag(mail.getEmail_flag() & ~MessageFlag.MessageFlagSeen);
//                break;
//            case mark_replay:
//                mail.setEmail_flag(mail.getEmail_flag() | MessageFlag.MessageFlagAnswered);
//                break;
//            case move_mail:
//            case update_mail_local:
//                updateMailLocal(bean.mailBean);
//                break;
//        }
//        notifyDataSetChanged();
//    }


    public List<MailBean> shareMailData() {
        return manager.getInboxListManager().getInboxList();
    }


    private void onFolderChange() {
        //refresh unread count from DB
        manager.sendEvent(EventID.Sync_Folder_UnRead_Count_Local);

        //根据文件夹类型 加载集合数据
        loadListByMailType(manager.getFolderManager().getFolder());
    }

    private void onInboxStatusNormal() {
        manager.sendEvent(EventID.View_Drawer_Lock_Mode_UNLOCKED);
        notifyDataSetChanged();
    }

    private void onInboxStatusSelect() {
        //关闭抽屉布局
        manager.sendEvent(EventID.View_Drawer_Lock_Mode_CLOSED);

        notifyDataSetChanged();
        setSelectFooterMenueState();
    }

    public void startLoadMore() {
        manager.sendEvent(EventID.Inbox_List_Stop_Refresh);
        progressBar.hide();
        //开启侧滑同步未读数量
        if (manager.getInboxListManager().getInboxList().size() == 0) {
            manager.sendEvent(EventID.Inbox_List_Stop_LoadMore);
            //如果本地的数据和数据库的数据都被清空，直接刷新检测服务端是否有新的数据
            refreshMail();
            return;
        }

        MoreLoader.fetch(folder_name, manager.getInboxListManager().getInboxList(), InboxFragment.this);
    }

    @Override
    public boolean onMessageCome(int eventId, Message message) {
        if (eventId == EventID.Refresh_List) {
            notifyDataSetChanged();
        } else if (eventId == EventID.On_Folder_Changed) {
            onFolderChange();
        } else if (eventId == EventID.Clear_Folder_Mail) {
            manager.getInboxListManager().getInboxList().clear();
            notifyDataSetChanged();
        } else if (eventId == EventID.Inbox_Status_Normal) {
            onInboxStatusNormal();
        } else if (eventId == EventID.Inbox_Status_Select) {
            onInboxStatusSelect();
        } else if (eventId == EventID.Inbox_List_Refresh) {
            refreshMail();
            return true;
        } else if (eventId == EventID.Inbox_List_LoadMore) {
            startLoadMore();
            return true;
        }
        return false;
    }
}
