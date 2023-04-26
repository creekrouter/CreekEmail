package com.creek.mail.home.manage;

import static com.creek.mail.details.DetailsPageActivity.INBOX_ACTIVITY;
import static com.creek.mail.details.DetailsPageActivity.KEY_FROM;
import static com.creek.mail.details.DetailsPageActivity.KEY_POSITION;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;

import com.creek.common.router.Launcher;
import com.creek.mail.compose.Compose;
import com.creek.mail.details.DetailsPageActivity;
import com.creek.common.MailBean;
import com.creek.common.constant.Const;
import com.creek.mail.R;
import com.creek.mail.compose.ComposeActivity;
import com.creek.mail.home.inbox.InboxAdapter;
import com.creek.mail.home.msg.EventID;
import com.creek.mail.home.msg.EventListen;
import com.creek.mail.home.msg.HomeHandler;
import com.creek.mail.home.status.InboxStatus;
import com.creek.mail.home.listview.InboxListView;
import com.creek.mail.home.swipe.NewSwipeMenuCreator;
import com.creek.mail.home.swipe.OnSwipeMenuItemClick;
import com.creek.mail.home.swipe.SwipeMenu;

import java.util.ArrayList;
import java.util.List;

public class InboxListManager implements EventListen, OnSwipeMenuItemClick {
    private Activity mActivity;
    private HomeHandler mHandler;
    private FolderManager folderManager;
    private InboxStatus status = InboxStatus.Normal;

    private InboxListView listView;
    private InboxAdapter adapter;
    private List<MailBean> inboxList = new ArrayList<>();
    private int countSelect = 0;


    public InboxListManager(Activity activity, HomeHandler handler, FolderManager folderManager) {
        this.mActivity = activity;
        this.mHandler = handler;
        this.folderManager = folderManager;
        this.mHandler.addRegister(this);
    }

    @Override
    public boolean onMessageCome(int eventId, Message message) {
        if (eventId == EventID.View_Init) {
            initView();
        } else if (eventId == EventID.Inbox_Status_Normal) {
            onInboxStatusNormal();
        } else if (eventId == EventID.Inbox_Status_Select) {
            onInboxStatusSelect();
        } else if (eventId == EventID.On_Folder_Changed) {
            onFolderChanged();
        } else if (eventId == EventID.Inbox_List_Select_All) {
            toggleSelect(true);
            return true;
        } else if (eventId == EventID.Inbox_List_Select_None) {
            toggleSelect(false);
            return true;
        } else if (eventId == EventID.Inbox_List_Stop_LoadMore) {
            listView.stopLoadMore();
            return true;
        } else if (eventId == EventID.Inbox_List_Stop_Refresh) {
            listView.stopRefresh();
            return true;
        } else if (eventId == EventID.Inbox_List_Notify_Data_Changed) {
            notifyDataChanged();
            return true;
        }
        return false;
    }

    private void initView() {
        listView = mActivity.findViewById(R.id.listview);
        adapter = new InboxAdapter(mActivity, inboxList);
        listView.setAdapter(adapter);

        listView.setPullRefreshEnable(true);
        listView.setOnSwipeListener(new InboxListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {

            }

            @Override
            public void onSwipeEnd(int position) {
                listView.setHeaderInvisible();
            }
        });
        listView.setOnMenuItemClickListener(this);
        listView.setPullLoadEnable(true);
        listView.setMenuCreator(new NewSwipeMenuCreator(mActivity));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (status == InboxStatus.Select) {
                    MailBean bean = inboxList.get(position - 1);
                    bean.setSelected(!bean.isSelected());
                    countSelect = bean.isSelected() ? countSelect + 1 : countSelect - 1;
                    mHandler.sendEvent(EventID.Inbox_List_Select_Change, countSelect, inboxList.size());
                    // todo notifydatasetchange
                    notifyDataChanged();

                } else {
                    jumpActivity(position - 1, inboxList);
                }

            }

        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mHandler.sendEmptyMessage(EventID.Inbox_Status_Select);
                return false;
            }
        });


        listView.setXListViewListener(new InboxListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                mHandler.sendEvent(EventID.Inbox_List_Refresh);
            }

            @Override
            public void onLoadMore() {
                mHandler.sendEvent(EventID.Inbox_List_LoadMore);
            }
        });

    }

    @Override
    public void onMenuItemClick(int position, SwipeMenu menu, int index) {

    }

    private void jumpActivity(int position, List<MailBean> list) {
        Bundle bundle = new Bundle();
        //草稿箱 直接到写邮件  收件箱汉字名称依赖flag值，比较准确
        if (Const.DRAFTS.equals(folderManager.getFolder().getFolder_name_ch())) {//根据邮件箱标题类型，判断跳转哪里
            bundle.putInt("type", Compose.TYPE_DRAFTS);
            bundle.putSerializable("mail", list.get(position));
            Launcher.startActivity(mActivity, ComposeActivity.class, bundle);
        } else {
            bundle.putInt(KEY_POSITION, position);
            bundle.putString(KEY_FROM, INBOX_ACTIVITY);
            Launcher.startActivity(mActivity, DetailsPageActivity.class, bundle);
        }

    }

    private void notifyDataChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

    }

    private void onFolderChanged() {
        setListViewPull(false);
        //设置邮件夹类型Flag
        listView.getSwipeMenuAdapter().setFolderFlag(folderManager.getFolder().getFolder_flag());

    }

    private void onInboxStatusNormal() {
        status = InboxStatus.Normal;
        adapter.status = status;
        listView.getSwipeMenuAdapter().status = status;
        setListViewPull(false);

    }

    private void toggleSelect(boolean status) {
        for (MailBean mail : inboxList) {
            mail.setSelected(status);
        }
        countSelect = status ? inboxList.size() : 0;
        mHandler.sendEvent(EventID.Inbox_List_Select_Change, countSelect, inboxList.size());
        // todo notifydatasetchange
        notifyDataChanged();
    }

    private void onInboxStatusSelect() {
        status = InboxStatus.Select;
        adapter.status = status;
        listView.getSwipeMenuAdapter().status = status;
        setListViewPull(true);
        countSelect = 0;
        for (MailBean mail : inboxList) {
            mail.setSelected(false);
        }
    }

    private void setListViewPull(boolean isCLose) {
        if (Const.FLAGED_EN.equals(folderManager.getFolderNameEn()) || isCLose) {
            //关闭上拉刷新下拉加载
            listView.setPullLoadEnable(false);
            listView.setPullRefreshEnable(false);
        } else {
            listView.setPullLoadEnable(true);
            listView.setPullRefreshEnable(true);
        }
    }

    public List<MailBean> getInboxList() {
        return inboxList;
    }

}
