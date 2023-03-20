package com.creek.mail.search;



import com.creek.common.MailBean;

import java.util.List;

public interface EmailSearch {
    void preLocalSearch();
    void onLocalSearchResult(List<MailBean> results);
    void localSearchNone();
    void onLocalSearchFinish();
}
