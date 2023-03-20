package com.creek.mail.home.inbox;



import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.sort;

import com.creek.common.MailBean;

public class SortTools {
    private int lastPos = -1;
    private long minLocalUid;

    public long getMinLocalUid(){
        return minLocalUid;
    }

    //通常加载更多后，
    public void setMinLocalUid(long minEmailUid){
        minLocalUid = minEmailUid;
    }

    public void deleteList(List<MailBean> inboxList, List<MailBean> delList){
        List<MailBean> tmpList = new ArrayList<>();
        for (MailBean del:delList){
            for (MailBean item:inboxList){
                if (del.uid()==item.uid()){
                    tmpList.add(item);
                }
            }
        }
        inboxList.removeAll(tmpList);
    }


    public void insertList(List<MailBean> inboxList, List<MailBean> insertList){

        if (inboxList.size()==0){
            minLocalUid = insertList.get(0).uid();
        }
        inboxList.addAll(insertList);
        sort(inboxList, new Comparator<MailBean>() {
            @Override
            public int compare(MailBean o1, MailBean o2) {
                if (o1 == null && o2== null){
                    return 0;
                }else if (o1 != null && o2 == null){
                    return -1;
                }else if (o1 == null && o2 != null){
                    return 1;
                }else {
                    if (o2.getSendTime()>o1.getSendTime()){
                        return 1;
                    }else if (o2.getSendTime()<o1.getSendTime()){
                        return -1;
                    }else {
                        return 0;
                    }
                }
            }
        });

    }
}
