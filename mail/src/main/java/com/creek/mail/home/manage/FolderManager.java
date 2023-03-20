package com.creek.mail.home.manage;

import android.os.Message;

import com.creek.common.CreekPath;
import com.creek.common.interfaces.CommonCallBack;
import com.creek.router.CreekRouter;
import com.creek.sync.MailSync;
import com.mail.tools.ThreadPool;
import com.libmailcore.IMAPFolderStatus;
import com.creek.common.MailFolder;
import com.creek.common.constant.Const;
import com.mail.tools.MailToast;
import com.creek.common.UserInfo;
import com.creek.database.DBManager;
import com.creek.mail.home.msg.EventID;
import com.creek.mail.home.msg.EventListen;
import com.creek.mail.home.msg.HomeHandler;

import java.util.ArrayList;
import java.util.List;

public class FolderManager implements EventListen {
    private List<MailFolder> mFolders = new ArrayList<>();
    private HomeHandler mHandler;
    private int mCurrentPosition = 0;

    public FolderManager(HomeHandler handler) {
        this.mHandler = handler;
        this.mHandler.addRegister(this);
    }

    public List<MailFolder> getFolders() {
        return mFolders;
    }

    @Override
    public boolean onMessageCome(int eventId, Message message) {
        if (eventId == EventID.Sync_Folder_From_Local) {
            syncFolderByLocal();
            return true;
        } else if (eventId == EventID.Sync_Folder_From_Server) {
            syncFolderByServer();
            return true;
        } else if (eventId == EventID.Sync_Folder_UnRead_Count_Server) {
            refreshUnReadCount(mCurrentPosition);
            return true;
        } else if (eventId == EventID.On_New_Folder_Selected) {
            mCurrentPosition = message.arg1;
            mHandler.sendEmptyMessage(EventID.On_Folder_Changed);
            CreekRouter.functionRun(CreekPath.Mail_BroadCast_Inbox_Folder_Changed, getFolder().getFolder_name_en());
            return true;
        } else if (eventId == EventID.Sync_Folder_Menu_UnRead_Count) {
            for (int i = 0; i < mFolders.size(); i++) {
                refreshUnReadCount(i);
            }
            return true;
        } else if (eventId == EventID.Folder_Menu_List_Click) {
            onClick(message.arg1);
            return true;
        } else if (eventId == EventID.Sync_Folder_UnRead_Count_Local) {
            getCurrentFolderUnreadDb();
            return true;
        }
        return false;
    }

    /**
     * Inbox listView unread count, from Local Database.
     */
    public void getCurrentFolderUnreadDb() {
        ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                long unseen_count = DBManager.
                        selectUnSeenCount(getFolder().getFolder_name_en());
                getFolder().setUnreadNum(unseen_count);
                mHandler.sendEmptyMessage(EventID.Sync_Folder_UnRead_Count_Local_Over);
            }
        });
    }

    private void onClick(int position) {
        if (mFolders.get(position).getFolder_flag() == -1) {
            return;
        }
        if (position < 0 || position >= mFolders.size())
            return;
        Message msg = mHandler.obtainMessage();
        msg.what = EventID.On_New_Folder_Selected;
        msg.arg1 = position;
        mHandler.sendMessage(msg);
    }

    private void refreshUnReadCount(int index) {
        if (mFolders.size() == 0 || mCurrentPosition < 0 || mCurrentPosition >= mFolders.size()) {
            return;
        }
        String folderNameEn = mFolders.get(index).getFolder_name_en();
        MailFolder folder = mFolders.get(index);
        Message msg = mHandler.obtainMessage();
        msg.arg1 = index;

        //if current folder is Flag-Folder
        if (Const.FLAGED_EN.equals(folderNameEn)) {
            ThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    Long flagged_unread_count = DBManager.
                            selectUnSeenCount(Const.FLAGED_EN);
                    folder.setUnreadNum(flagged_unread_count);
                    msg.what = EventID.Folder_Flag_UnRead_Refresh;
                    mHandler.sendMessage(msg);
                }
            });
        } else {
            MailSync.syncFolderStatus(folderNameEn, new CommonCallBack<IMAPFolderStatus,String>() {
                @Override
                public void success(IMAPFolderStatus imapFolderStatus) {
                    folder.setUnreadNum(imapFolderStatus.unseenCount());
                    msg.what = EventID.Folder_Normal_UnRead_Refresh;
                    mHandler.sendMessage(msg);
                }

                @Override
                public void fail(String message) {

                }
            });
        }
    }

    public MailFolder getFolder() {
        return mFolders.get(mCurrentPosition);
    }

    public MailFolder getFolder(int index) {
        return mFolders.get(index);
    }

    public String getFolderNameEn() {
        return mFolders.get(mCurrentPosition).getFolder_name_en();
    }

    public String getFolderNameCh() {
        return mFolders.get(mCurrentPosition).getFolder_name_ch();
    }

    private void syncFolderByLocal() {
        ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                List<MailFolder> localFolders = DBManager.selectMailFolder();
                if (localFolders != null) {
                    mFolders.clear();
                    mFolders.addAll(localFolders);
                }
                mHandler.sendEmptyMessage(EventID.Sync_Folder_Local_Over);
            }
        });
    }

    private void syncFolderByServer() {

        MailSync.syncFolderList(UserInfo.userEmail, new CommonCallBack<List<MailFolder>,String>() {
            @Override
            public void success(List<MailFolder> mailFolders) {

                //存入数据库
                DBManager.insertMailFolder(mailFolders);

                if (mailFolders != null && mailFolders.size() > 0) {
                    addCustomFolder(mailFolders);
                    mFolders = mailFolders;
                }
                mHandler.sendEmptyMessage(EventID.Sync_Folder_Server_Over);
            }

            @Override
            public void fail(String errorStr) {
                MailToast.show(errorStr);
            }
        });
    }

    private void addCustomFolder(List<MailFolder> list) {
        int j = -1;
        for (int i = 0; i < list.size(); i++) {
            MailFolder item = list.get(i);
            if (item.getFolder_flag() == com.libmailcore.IMAPFolderFlags.IMAPFolderFlagNone) {
                j = i;
                break;
            }
        }
        if (j >= 0) {
            MailFolder item = new MailFolder();
            item.setFolder_flag(-1);
            item.setFolder_name_ch("自定义文件夹");
            item.setUnreadNum(0);
            list.add(j, item);
        }
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }
}
