package com.creek.mail.compose.manager;

import android.app.Activity;
import android.os.Message;
import android.view.View;

import com.creek.mail.compose.msg.ComposeHandler;
import com.creek.mail.compose.msg.EventID;
import com.creek.mail.compose.msg.EventWatcher;
import com.creek.mail.pop.Popup;
import com.mail.tools.ToolInput;

public class CommonManager implements EventWatcher {

    private Activity mActivity;
    private ComposeHandler mHandler;

    public CommonManager(Activity activity, ComposeHandler handler) {
        mActivity = activity;
        mHandler = handler;
        mHandler.addRegister(this);
    }

    @Override
    public boolean onEventHappen(int eventId, Message message) {

        if (eventId == EventID.Compose_Common_Event_BackPressed) {
            onBackKeyPressed();
            return true;
        }

        return false;
    }

    private void onBackKeyPressed() {
        ToolInput.hide();
        Popup.saveDraft(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.sendEvent(EventID.Compose_Mail_Action_Save);
            }
        });
    }
}
