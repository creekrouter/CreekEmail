package com.creek.mail.home.manage;

import android.app.Activity;
import android.os.Message;

import com.creek.mail.home.HomePageActivity;
import com.creek.mail.home.msg.EventListen;
import com.creek.mail.home.msg.HomeHandler;
import com.creek.mail.home.status.InboxStatusCtl;

public class HomeManager {
    private Activity mActivity;
    public HomeHandler mHandler = new HomeHandler();
    private FolderManager folderManager;
    private DrawerManager drawerManager;
    private MenuViewManager menuViewManager;
    private MenuListManager menuListManager;
    private InboxViewManager inboxViewManager;

    private InboxListManager inboxListManager;
    private InboxStatusCtl inboxStatusCtl;

    public HomeManager(Activity activity,EventListen listener) {
        inboxStatusCtl = new InboxStatusCtl(mHandler);

        mActivity = activity;
        mHandler.addRegister(listener);
        folderManager = new FolderManager(mHandler);
        drawerManager = new DrawerManager(mHandler, mActivity);
        menuViewManager = new MenuViewManager(mActivity, mHandler);
        menuListManager = new MenuListManager(mActivity, mHandler, folderManager);
        inboxListManager = new InboxListManager(activity, mHandler, folderManager);
        inboxViewManager = new InboxViewManager(mActivity, mHandler, folderManager);
    }


    public void sendEvent(int eventID) {
        mHandler.sendEmptyMessage(eventID);
    }

    public void sendEvent(int eventID, int arg1) {
        Message msg = mHandler.obtainMessage();
        msg.what = eventID;
        msg.arg1 = arg1;
        sendEvent(msg);
    }

    public void sendEvent(Message message) {
        mHandler.sendMessage(message);
    }

    public Message obtainMessage() {
        return mHandler.obtainMessage();
    }

    public FolderManager getFolderManager() {
        return folderManager;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public void addRegister(EventListen listener) {
        mHandler.addRegister(listener);
    }

    public InboxStatusCtl getInboxStatusCtl() {
        return inboxStatusCtl;
    }

    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
    }

    public InboxViewManager getInboxViewManager() {
        return inboxViewManager;
    }

    public InboxListManager getInboxListManager() {
        return inboxListManager;
    }
}
