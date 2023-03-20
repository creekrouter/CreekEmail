package com.creek.mail.home.msg;

import android.os.Message;

public interface EventListen {
    boolean onMessageCome(int eventId, Message message);
}
