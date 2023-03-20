package com.creek.sync.interfaces;

import com.creek.common.MailAttachment;
import com.creek.common.MailBean;
import com.creek.common.interfaces.CommonCallBack;
import com.creek.sync.callback.MultiCallBack;

import java.util.HashMap;
import java.util.List;

public interface MailAttachSync {
    void fetchAttach(MailAttachment attach, CommonCallBack<MailAttachment, String> callBack);

    void fetchHtmlPhoto(String photoCID, MailBean mailBean, CommonCallBack<HashMap<String,String>, String> callBack);

    void fetchHtmlPhotoArr(String[] cidArr, MailBean mailBean, CommonCallBack<HashMap<String,String>,String> callBack);

}
