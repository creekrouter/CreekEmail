package com.creek.sync.interfaces;

import com.creek.common.interfaces.CommonCallBack;
import com.libmailcore.IMAPMessage;
import com.libmailcore.IndexSet;
import com.libmailcore.Range;

import java.util.List;

public interface MailListSync {
    void syncMailList(String folderName, IndexSet indexSet, CommonCallBack<List<IMAPMessage>,String> callBack);
    void fetchMailList(String folderName, IndexSet indexSet, CommonCallBack<List<IMAPMessage>,String> callBack);
    void getMailList(String folderName, IndexSet indexSet, CommonCallBack<List<IMAPMessage>,String> callBack);
    void obtMailList(String folderName, IndexSet indexSet, CommonCallBack<List<IMAPMessage>,String> callBack);

}
