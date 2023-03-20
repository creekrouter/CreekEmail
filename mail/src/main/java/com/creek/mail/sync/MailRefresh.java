package com.creek.mail.sync;


import com.creek.common.MailBean;

import java.util.List;

public interface MailRefresh {
    void onMailFlagUpdate(List<MailBean> list);
}
