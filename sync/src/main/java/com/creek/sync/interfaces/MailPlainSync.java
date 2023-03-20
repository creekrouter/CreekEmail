package com.creek.sync.interfaces;

import com.creek.common.interfaces.ConfirmCallBack;
import com.libmailcore.AbstractPart;

public interface MailPlainSync {
    void syncMailPlain(AbstractPart mainPart, String folderName, final long uid, ConfirmCallBack<String> callBack);

    void getMailPlain(String folderName, final long uid, ConfirmCallBack<String> callBack);

}
