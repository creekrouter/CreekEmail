package com.creek.mail.compose.manager;

import android.app.Activity;
import android.os.Message;

import com.creek.common.MailAttachment;
import com.creek.mail.compose.msg.ComposeHandler;
import com.creek.mail.compose.msg.EventWatcher;
import com.creek.mail.compose.view.ViewManager;
import com.creek.mail.compose.view.WriteMailHeader;

import java.util.List;

public class ComposeManager {
    private Activity mActivity;
    private ComposeHandler mHandler;
    private CommonManager common;
    private ViewManager viewManager;
    private DataManager dataManager;

    public ComposeManager(Activity composeActivity) {
        mActivity = composeActivity;
        mHandler = new ComposeHandler();
        common = new CommonManager(mActivity, mHandler);
        dataManager = new DataManager(mActivity, mHandler);
        viewManager = new ViewManager(mActivity, mHandler, dataManager);
        viewManager.initView();
    }

    public void addRegister(EventWatcher watcher) {
        mHandler.addRegister(watcher);
    }

    public void sendEvent(int eventID) {
        mHandler.sendEvent(eventID);
    }

    public void sendEvent(int eventID, int arg1) {
        mHandler.sendEvent(eventID, arg1);
    }

    public void sendEvent(int eventID, String arg) {
        mHandler.sendEvent(eventID, arg);
    }

    public void sendEvent(int eventID, int arg1, int arg2) {
        mHandler.sendEvent(eventID, arg1, arg2);
    }

    public void sendEvent(Message message) {
        mHandler.sendMessage(message);
    }

    public ViewManager getViewManager() {
        return viewManager;
    }

    public WriteMailHeader getHeader() {
        return viewManager.getHeader();
    }

    public List<MailAttachment> getAttachList() {
        return dataManager.mAttachmentList;
    }

    public void parseIntent() {
        dataManager.parseIntent();
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}
