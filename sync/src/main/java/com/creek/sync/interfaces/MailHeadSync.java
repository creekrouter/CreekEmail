package com.creek.sync.interfaces;

import com.creek.common.interfaces.CommonCallBack;
import com.libmailcore.IMAPMessage;
import com.libmailcore.IndexSet;

import java.util.List;

public interface MailHeadSync {
    void syncMailHead(String folderName, IndexSet indexSet, CommonCallBack<List<IMAPMessage>,String> callBack);
}
