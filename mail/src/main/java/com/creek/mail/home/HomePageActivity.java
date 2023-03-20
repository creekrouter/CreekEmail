package com.creek.mail.home;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;


import androidx.fragment.app.FragmentActivity;

import com.creek.common.constant.ConstPath;
import com.creek.common.BaseActivity;
import com.creek.mail.sync.MailRefresh;
import com.creek.router.CreekRouter;
import com.creek.router.annotation.CreekBean;
import com.creek.router.annotation.CreekMethod;
import com.creek.common.CreekPath;
import com.creek.mail.R;
import com.creek.sync.imap.ImapManager;
import com.creek.database.DBManager;
import com.creek.common.MailFolder;
import com.creek.common.MailBean;
import com.creek.mail.home.inbox.InboxFragment;
import com.creek.mail.home.manage.HomeManager;
import com.creek.mail.home.msg.EventID;
import com.creek.mail.home.msg.EventListen;
import com.creek.mail.home.status.InboxStatus;

import java.util.List;


@CreekBean(path = "mail_activity_home_page_activity")
public class HomePageActivity extends BaseActivity implements EventListen, MailRefresh {

    private InboxFragment mInboxFragment;

    private HomeManager manager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        CreekRouter.functionRun(CreekPath.Mail_BroadCast_User_Init, ImapManager.singleton().getSession().username());

        ImapManager.theSingleton = null;
        ImapManager.singleton().getSession().cancelAllOperations();

        manager = new HomeManager(mContext, this);

        //初始化 收件箱fragment
        mInboxFragment = new InboxFragment(manager);

        //初始化侧拉菜单未读数量
        //先从本地拿，如果本地没有再从服务端取，若本地有直接取本地，然后在子线程中更新本地数据
        manager.sendEvent(EventID.Sync_Folder_From_Local);

        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mInboxFragment).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        setStatusBarColor(Color.parseColor("#f9f9f9"), 0);
    }

    private void initFolders(List<MailFolder> folders) {
        if (folders != null && folders.size() > 0) {
//          "本地数据库有文件夹列表数据"
            manager.getFolderManager().getFolder().setSelected(true);
            manager.sendEvent(EventID.Folder_Menu_List_Refresh);
            //refresh unread count from DB
            manager.sendEvent(EventID.Sync_Folder_UnRead_Count_Local);
        }
        manager.sendEvent(EventID.Sync_Folder_From_Server);

    }

    private void setSelect(int position) {
        for (int i = 0; i < manager.getFolderManager().getFolders().size(); i++) {
            if (position == i) {
                manager.getFolderManager().getFolders().get(i).setSelected(true);
            } else {
                manager.getFolderManager().getFolders().get(i).setSelected(false);
            }
        }
        manager.sendEvent(EventID.Folder_Menu_List_Refresh);
        manager.sendEvent(EventID.View_Close_Drawer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ImapManager.singleton().getSession().cancelAllOperations();
    }


    @Override
    public boolean onMessageCome(int eventId, Message message) {
        if (eventId == EventID.Sync_Folder_Local_Over) {
            initFolders(manager.getFolderManager().getFolders());
        } else if (eventId == EventID.Sync_Folder_Server_Over) {
            manager.getFolderManager().getFolder().setSelected(true);
            manager.sendEvent(EventID.Folder_Menu_List_Refresh);
        } else if (eventId == EventID.On_Folder_Changed) {
            setSelect(manager.getFolderManager().getCurrentPosition());
        } else if (eventId == EventID.Folder_Flag_UnRead_Refresh) {
            int index = message.arg1;
            long count = manager.getFolderManager().getFolder(message.arg1).getUnreadNum();
            manager.getFolderManager().getFolders().get(index).setUnreadNum(count);
            manager.sendEvent(EventID.Folder_Menu_List_Refresh);
        } else if (eventId == EventID.Folder_Normal_UnRead_Refresh) {
            int index = message.arg1;
            long count = manager.getFolderManager().getFolder(index).getUnreadNum();

            manager.getFolderManager().getFolders().get(index).setUnreadNum(count);
            manager.sendEvent(EventID.Folder_Menu_List_Refresh);

        } else if (eventId == EventID.Clear_Folder_Mail) {
            DBManager.deleteAllMail();
            ConstPath.getAttachDir().deleteOnExit();
            for (MailFolder folder : manager.getFolderManager().getFolders()) {
                folder.setUnreadNum(0);
            }
            manager.sendEvent(EventID.Folder_Menu_List_Refresh);
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        if (manager.getInboxStatusCtl().getStatus() == InboxStatus.Select) {
            manager.sendEvent(EventID.Inbox_Status_Normal);
        } else {
            super.onBackPressed();
        }
    }

    @CreekMethod(path = CreekPath.Mail_Message_List_Share_Home)
    public List<MailBean> shareMailDataList() {
        return mInboxFragment.shareMailData();
    }


    @Override
    public void onMailFlagUpdate(List<MailBean> list) {
        manager.sendEvent(EventID.Inbox_List_Refresh);
    }

    @CreekMethod(path = CreekPath.Mail_Information_Update_Flag)
    public static void onDateUpdate(List<MailBean> list) {
//        for (Activity activity : AppContext.getActivities(HomePageActivity.class.getName())) {
//            ((HomePageActivity) activity).onMailFlagUpdate(list);
//        }

        // TODO router
    }

}
