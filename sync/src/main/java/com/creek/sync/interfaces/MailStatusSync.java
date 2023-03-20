package com.creek.sync.interfaces;

import com.creek.common.MailBean;
import com.creek.common.interfaces.CommonCallBack;

import java.util.List;


public interface MailStatusSync {

    void messageDeleted(List<MailBean> mailList, CommonCallBack<Void,String> callBack);

    void messageMove(String desFolder, List<MailBean> mailList, CommonCallBack<Void,String> callBack);

    void messageUpdateFlag(int requestKind, int flag, List<MailBean> mailList, CommonCallBack<Void,String> callBack);

}
