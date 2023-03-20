package com.creek.mail.details.part;

import static com.creek.mail.details.msg.EvenId.MAIL_ANSWERED;
import static com.creek.mail.details.msg.EvenId.MAIL_CANCEL_RED_FLAG;
import static com.creek.mail.details.msg.EvenId.MAIL_DELETE;
import static com.creek.mail.details.msg.EvenId.MAIL_MOVE;
import static com.creek.mail.details.msg.EvenId.MAIL_READ;
import static com.creek.mail.details.msg.EvenId.MAIL_RED_FLAG;
import static com.creek.mail.details.msg.EvenId.MAIL_TOGGLE_READ;
import static com.creek.mail.details.msg.EvenId.MAIL_TOGGLE_RED_FLAG;
import static com.creek.mail.details.msg.EvenId.MAIL_UNREAD;

import android.app.Activity;
import android.view.View;

import com.creek.common.EmailData;
import com.creek.mail.details.msg.WatchDog;
import com.creek.mail.details.msg.MsgHandler;
import com.creek.mail.details.msg.Event;
import com.creek.mail.sync.ActionType;
import com.creek.mail.sync.MailUpdater;
import com.creek.mail.pop.Popup;
import com.creek.common.MailBean;
import com.libmailcore.MessageFlag;


public class DetailMailOperate implements WatchDog {

    /*
    邮件包含的操作：
    １、删除
    ２、标记已读
    ３、标记未读
    ４、标记红旗
    ５、取消红旗
    ６、移动到其他文件夹
     */
    private Activity mActivity;
    private EmailData data;
    protected MsgHandler mHandler;


    public DetailMailOperate(Activity activity, MsgHandler handler,EmailData emailData) {
        this.mActivity = activity;
        mHandler = handler;
        data = emailData;
    }


    @Override
    public void onMsgCome(Event event) {

        switch (event.eventID) {
            case MAIL_DELETE:
                deleteMail();
                break;
            case MAIL_TOGGLE_READ:
                if ((data.getMail().getEmailFlag() & MessageFlag.MessageFlagSeen) == MessageFlag.MessageFlagSeen) {
                    MailUpdater.flag(ActionType.UnSeen,data.getMail());
                } else {
                    MailUpdater.flag(ActionType.Seen,data.getMail());
                }
                break;
            case MAIL_READ:
                MailUpdater.flag(ActionType.Seen,data.getMail());
                break;
            case MAIL_UNREAD:
                MailUpdater.flag(ActionType.UnSeen,data.getMail());
                break;
            case MAIL_RED_FLAG:
                MailUpdater.flag(ActionType.Flag,data.getMail());
                break;
            case MAIL_CANCEL_RED_FLAG:
                MailUpdater.flag(ActionType.UnFlag,data.getMail());
                break;
            case MAIL_TOGGLE_RED_FLAG:
                if ((data.getMail().getEmailFlag() & MessageFlag.MessageFlagFlagged) == MessageFlag.MessageFlagFlagged) {
                    MailUpdater.flag(ActionType.UnFlag,data.getMail());
                } else {
                    MailUpdater.flag(ActionType.Flag,data.getMail());
                }
                break;
            case MAIL_MOVE:
                MailUpdater.move(data.getMail());
                break;
            case MAIL_ANSWERED:
                MailUpdater.flag(ActionType.Answer,data.getMail());
                break;
        }
    }

    private void deleteMail() {
        final MailBean mail = data.getMail();
        if ("Trash".equals(mail.folderName())) {
            Popup.deleteMail(null, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MailUpdater.delete(data.getMail());
                    mActivity.finish();
                }
            });
        } else {
            MailUpdater.delete(data.getMail());
            mActivity.finish();
        }
    }

}
