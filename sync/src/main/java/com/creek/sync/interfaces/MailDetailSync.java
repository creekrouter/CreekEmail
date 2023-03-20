package com.creek.sync.interfaces;

import com.creek.common.interfaces.CommonCallBack;
import com.libmailcore.IMAPMessage;

import java.util.List;

public interface MailDetailSync {
    void syncMailDetails(String folderName, long mailUid, CommonCallBack<List<IMAPMessage>,String> callBack);
}
