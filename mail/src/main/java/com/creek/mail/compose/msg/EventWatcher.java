package com.creek.mail.compose.msg;

import android.os.Message;

public interface EventWatcher {
    boolean onEventHappen(int eventId, Message message);
}
