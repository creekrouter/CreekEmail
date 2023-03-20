package com.creek.mail.details;


import static com.creek.common.router.RouterInit.isApk;
import static com.creek.mail.details.DetailsPageActivity.INBOX_ACTIVITY;
import static com.creek.mail.details.DetailsPageActivity.INTERACT_ACTIVITY;
import static com.creek.mail.details.DetailsPageActivity.KEY_FOLDER;
import static com.creek.mail.details.DetailsPageActivity.KEY_FROM;
import static com.creek.mail.details.DetailsPageActivity.KEY_POSITION;
import static com.creek.mail.details.DetailsPageActivity.KEY_UID;
import static com.creek.mail.details.DetailsPageActivity.OTHER_ACTIVITY;
import static com.creek.mail.details.DetailsPageActivity.SEARCH_ACTIVITY;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;


import com.creek.common.CreekPath;
import com.creek.common.EmailData;
import com.creek.common.MailBean;
import com.creek.mail.home.HomePageActivity;
import com.creek.router.CreekRouter;
import com.mail.tools.context.AppContext;

import java.util.List;


public class IntentParser {

    public static boolean parseIntent(Intent intent, EmailData dataPkg) {
        if (intent == null) {
            return false;
        }
        dataPkg.setFrom(intent.getStringExtra(KEY_FROM));
        if (dataPkg.getFrom() == null) {
            return false;
        }
        if (SEARCH_ACTIVITY.equals(dataPkg.getFrom()) ||
                INBOX_ACTIVITY.equals(dataPkg.getFrom())) {
            return parseFromList(intent, dataPkg);
        } else if (OTHER_ACTIVITY.equals(dataPkg.getFrom())) {
            return parseFromOther(intent, dataPkg);
        } else if (INTERACT_ACTIVITY.equals(dataPkg.getFrom())) {
            return parseFromList(intent, dataPkg);
        }
        return false;
    }

    private static boolean parseFromList(Intent intent, EmailData dataPkg) {
        Object obj = AppContext.getActivity(HomePageActivity.class.getName());
        List<MailBean> mailList  = CreekRouter.methodCall(CreekPath.Mail_Message_List_Share_Home, obj);

        if (mailList == null) {
            return false;
        } else {
            dataPkg.setMailList(mailList);
        }
        int position = intent.getIntExtra(KEY_POSITION, -1);
        if (position < 0 || position > mailList.size() - 1) {
            return false;
        } else {
            dataPkg.setPosition(position);
        }
        MailBean mail = mailList.get(position);
        if (mail == null || mail.uid() < 0) {
            return false;
        } else {
            dataPkg.setMail(mail);
        }
        return true;
    }

    private static boolean parseFromOther(Intent intent, EmailData dataPkg) {
        long uid = intent.getLongExtra(KEY_UID, -1l);
        if (uid < 0) {
            return false;
        } else {
            dataPkg.setUid(uid);
        }
        String folderName = intent.getStringExtra(KEY_FOLDER);
        if (folderName == null || folderName.length() == 0) {
            return false;
        } else {
            dataPkg.setFolderName(folderName);
        }
        return true;
    }

}
