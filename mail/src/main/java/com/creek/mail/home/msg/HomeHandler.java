package com.creek.mail.home.msg;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

public class HomeHandler extends Handler {
    private Set<EventListen> listeners;

    public HomeHandler() {
        super(Looper.getMainLooper());
        listeners = new HashSet<>();
    }

    public void addRegister(EventListen listener) {
        if (listener == null || listeners.contains(listener)) {
            return;
        }
        listeners.add(listener);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        for (EventListen eventListen : listeners) {
            eventListen.onMessageCome(msg.what, msg);
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

    public void sendEvent(int eventID, int arg1, int arg2) {
        Message msg = obtainMessage();
        msg.what = eventID;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        sendMessage(msg);
    }
}
