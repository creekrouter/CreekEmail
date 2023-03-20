package com.creek.mail.home.status;

import android.os.Message;

import com.creek.mail.home.msg.EventID;
import com.creek.mail.home.msg.EventListen;
import com.creek.mail.home.msg.HomeHandler;

public class InboxStatusCtl implements EventListen {
    private InboxStatus mStatus = InboxStatus.Normal;

    private HomeHandler mHandler;

    public InboxStatusCtl(HomeHandler handler) {
        this.mHandler = handler;
        mHandler.addRegister(this);
    }

    public InboxStatus getStatus() {
        return mStatus;
    }


    @Override
    public boolean onMessageCome(int eventId, Message message) {
        if (eventId == EventID.Inbox_Status_Normal) {
            mStatus = InboxStatus.Normal;
        } else if (eventId == EventID.Inbox_Status_Select) {
            mStatus = InboxStatus.Select;
        } else if (eventId == EventID.Inbox_Status_Filter) {
            mStatus = InboxStatus.Filter;
        }
        return false;
    }
}
