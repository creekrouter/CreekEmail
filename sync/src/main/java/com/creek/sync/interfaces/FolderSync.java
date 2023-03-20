package com.creek.sync.interfaces;

import com.creek.common.MailFolder;
import com.creek.common.interfaces.CommonCallBack;
import com.libmailcore.IMAPFolderStatus;

import java.util.List;

public interface FolderSync {
    void syncFolderStatus(String folderName, CommonCallBack<IMAPFolderStatus,String> callBack);

    void syncFolderList(String userEmail, CommonCallBack<List<MailFolder>,String> callBack);

}
