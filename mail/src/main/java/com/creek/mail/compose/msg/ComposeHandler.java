package com.creek.mail.compose.msg;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

public class ComposeHandler extends Handler {

    private Set<EventWatcher> mWatchers;

    public ComposeHandler() {
        super(Looper.getMainLooper());
        mWatchers = new HashSet<>();
    }

    public void addRegister(EventWatcher watcher) {
        if (mWatchers.contains(watcher)) {
            return;
        }
        mWatchers.add(watcher);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        for (EventWatcher watcher : mWatchers) {
            watcher.onEventHappen(msg.what, msg);
        }
    }

    public void sendEvent(int eventID) {
        this.sendEmptyMessage(eventID);
    }

    public void sendEvent(int eventID, int arg1) {
        Message msg = obtainMessage();
        msg.what = eventID;
        msg.arg1 = arg1;
        sendMessage(msg);
    }

    public void sendEvent(int eventID, String arg) {
        Message msg = obtainMessage();
        msg.what = eventID;
        msg.obj = arg;
        sendMessage(msg);
    }

    public void sendEvent(int eventID, int arg1, int arg2) {
        Message msg = obtainMessage();
        msg.what = eventID;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        sendMessage(msg);
    }
}
