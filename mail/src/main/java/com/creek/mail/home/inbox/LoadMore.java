package com.creek.mail.home.inbox;



import com.creek.common.MailBean;

import java.util.List;

public interface LoadMore {
    void loadMoreDataFromDB(List<MailBean> mailInfoList);
    void loadMoreDataDel(List<MailBean> delList);
    void loadMoreDataAdd(List<MailBean> addList);
    void loadMoreDataUpdate(List<MailBean> updateList);
    void loadMoreDataNone();
}
