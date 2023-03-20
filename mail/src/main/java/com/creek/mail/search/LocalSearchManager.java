package com.creek.mail.search;

import android.app.Activity;
import com.creek.database.DBManager;
import com.creek.common.MailBean;
import com.mail.tools.ThreadPool;
import com.mail.tools.ToolInput;

import java.util.Date;
import java.util.List;

public class LocalSearchManager {
    private Activity mContext;
    private EmailSearch mSearcher;

    public LocalSearchManager(Activity context, EmailSearch emailSearch) {
        mContext = context;
        mSearcher = emailSearch;
    }

    private boolean notifySearchRes(final List<MailBean> resList) {
        if (resList != null && resList.size() > 0) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSearcher.onLocalSearchResult(resList);
                }
            });
            return true;
        }
        return false;
    }

    private void notifySearchNone() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSearcher.localSearchNone();
            }
        });
    }

    private void notifySearchFinish() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSearcher.onLocalSearchFinish();
            }
        });
    }

    public void startLocalSearch(final String keyword) {
        ToolInput.hide();
        mSearcher.preLocalSearch();
        ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    boolean hasDate = false;
                    long startTime = new Date().getTime();
                    long endTime = 0l;
                    hasDate = notifySearchRes(getSearchRes(keyword, startTime, endTime)) || hasDate;
                    if (!hasDate) {
                        notifySearchNone();
                    }
                    notifySearchFinish();
                }

            }
        });

    }


    private List<MailBean> getSearchRes(String keyword, long startTime, long endTime) {
        List<MailBean> res = DBManager.searchMailByKey("0", keyword, startTime, endTime);
        return res;
    }

}
