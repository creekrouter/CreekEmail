package com.creek.mail.sync;

import com.creek.common.UserInfo;
import com.creek.common.interfaces.CommonCallBack;
import com.creek.sync.imap.ImapManager;
import com.libmailcore.IMAPSearchExpression;
import com.libmailcore.IMAPSearchOperation;
import com.libmailcore.IndexSet;
import com.libmailcore.MailException;
import com.libmailcore.OperationCallback;

import java.util.Date;

public class MailCorrelate {


    /**
     * 往来邮件搜索
     * 搜索范围，1=全部，2=我发送的，3=我收到的
     */
    public static void search(String folder_name, int type, String userEmail, long startTime, long endTime, CommonCallBack<IndexSet,String> callback) {
        if (type != 1 && type != 2 && type != 3) {
            return;
        }
        IMAPSearchExpression mineSend = null, mineReceive = null, searchExp;

        if (2 == type || type == 1) {
            //我发送的
            IMAPSearchExpression expFrom = IMAPSearchExpression.searchFrom(UserInfo.userEmail);
            IMAPSearchExpression expTo = IMAPSearchExpression.searchTo(userEmail);
            IMAPSearchExpression expCC = IMAPSearchExpression.searchCc(userEmail);
            IMAPSearchExpression expReceive = IMAPSearchExpression.searchOr(expTo, expCC);
            mineSend = IMAPSearchExpression.searchAnd(expFrom, expReceive);
        }
        if (3 == type || type == 1) {
            IMAPSearchExpression expFrom = IMAPSearchExpression.searchFrom(userEmail);
            IMAPSearchExpression expTo = IMAPSearchExpression.searchTo(UserInfo.userEmail);
            IMAPSearchExpression expCC = IMAPSearchExpression.searchCc(UserInfo.userEmail);
            IMAPSearchExpression expReceive = IMAPSearchExpression.searchOr(expTo, expCC);
            mineReceive = IMAPSearchExpression.searchAnd(expFrom, expReceive);
        }
        if (type == 2) {
            searchExp = mineSend;
        } else if (type == 3) {
            searchExp = mineReceive;
        } else {
            searchExp = IMAPSearchExpression.searchOr(mineSend, mineReceive);
        }
        Date startDate = new Date(startTime);
        Date endDate = new Date(endTime);
        IMAPSearchExpression sTimeExp = IMAPSearchExpression.searchBeforeDate(startDate);
        IMAPSearchExpression eTimeExp = IMAPSearchExpression.searchSinceDate(endDate);
        IMAPSearchExpression timeExp = IMAPSearchExpression.searchAnd(sTimeExp, eTimeExp);
        IMAPSearchExpression interactMailExp = IMAPSearchExpression.searchAnd(searchExp, timeExp);

        final IMAPSearchOperation operation = ImapManager.singleton().getSession().searchOperation(folder_name, interactMailExp);
        operation.start(new OperationCallback() {
            @Override
            public void succeeded() {
                IndexSet indexSet = operation.uids();
//                IndexSet indexSet = new IndexSet();
//                if(tempSet.count() > 50){
//                    for(int i=tempSet.count()-1; i > (tempSet.count()-50); i--){
//                        indexSet.addIndex(tempSet.);
//                    }
//                }
                callback.success(indexSet);
            }

            @Override
            public void failed(MailException e) {
                callback.fail(e.toString());
            }
        });
    }


}
