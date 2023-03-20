package com.creek.mail.details.part;

import static com.creek.mail.details.DetailsPageActivity.INBOX_ACTIVITY;
import static com.creek.mail.details.DetailsPageActivity.INTERACT_ACTIVITY;
import static com.creek.mail.details.DetailsPageActivity.KEY_FROM;
import static com.creek.mail.details.DetailsPageActivity.KEY_POSITION;
import static com.creek.mail.details.DetailsPageActivity.SEARCH_ACTIVITY;
import static com.creek.mail.details.msg.EvenId.ACTIVITY_WINDOW_FOCUS;
import static com.creek.mail.details.msg.EvenId.CAT_NEXT_MAIL;
import static com.creek.mail.details.msg.EvenId.CAT_PRE_MAIL;
import static com.creek.mail.details.msg.EvenId.FIND_VIEW_BY_ID;
import static com.creek.mail.details.msg.EvenId.INIT_UI_CONTENT;
import static com.creek.mail.details.msg.EvenId.LIST_DATA_RESET;
import static com.creek.mail.details.msg.EvenId.LIST_NOTIFY_DATA_CHANGE;
import static com.creek.mail.details.msg.EvenId.MAIL_PERSON_INFO;
import static com.creek.mail.details.msg.EvenId.MAIL_REPLAY_ALL;
import static com.creek.mail.details.msg.EvenId.MAIL_REPLAY_SENDER;
import static com.creek.mail.details.msg.EvenId.POP_WINDOW_FORWARD;
import static com.creek.mail.details.msg.EvenId.POP_WINDOW_MORE_ACTIONS;
import static com.creek.mail.details.msg.EvenId.UI_VIEW_REFRESH;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;


import com.creek.common.EmailData;
import com.creek.common.MailBean;
import com.creek.common.interfaces.ConfirmCallBack;
import com.creek.common.router.Launcher;
import com.creek.mail.R;
import com.creek.mail.details.DetailsPageActivity;
import com.creek.mail.details.msg.WatchDog;
import com.creek.mail.compose.Compose;
import com.creek.mail.compose.ComposeActivity;
import com.creek.mail.details.msg.MsgHandler;
import com.creek.mail.details.msg.Event;
import com.creek.common.MailAttachment;
import com.creek.mail.pop.Popup;
import com.mail.tools.MailToast;

import java.util.List;

public class ViewsManager extends BaseViewsManager implements WatchDog {

    public ViewsManager(Activity activity, MsgHandler handler, EmailData emailData) {
        super(activity, handler, emailData);
    }

    @Override
    public void onMsgCome(Event event) {
        switch (event.eventID) {
            case FIND_VIEW_BY_ID:
                initViews();
                break;
            case UI_VIEW_REFRESH:
                refreshUI(event);
                break;
            case INIT_UI_CONTENT:
                initContentAndUi();
                break;
            case LIST_NOTIFY_DATA_CHANGE:
                notifyDataSetChanged();
                break;
            case ACTIVITY_WINDOW_FOCUS:
                onWindowFocus();
                break;
            case POP_WINDOW_FORWARD:
                Bundle b = new Bundle();
                b.putBoolean("replayAll",false);
                b.putInt("type", Compose.TYPE_FORWARD);
                toComposeMail(b);
                break;
            case MAIL_REPLAY_SENDER:
                Bundle b1 = new Bundle();
                b1.putBoolean("replayAll",false);
                b1.putInt("type", Compose.TYPE_REPLY);
                toComposeMail(b1);
                break;
            case MAIL_REPLAY_ALL:
                Bundle b2 = new Bundle();
                b2.putBoolean("replayAll",true);
                b2.putInt("type", Compose.TYPE_REPLY);
                toComposeMail(b2);
                break;
            case POP_WINDOW_MORE_ACTIONS:
                Popup.moreAction(data.getMail(), new ConfirmCallBack<Integer>() {
                    @Override
                    public void onConfirm(Integer integer) {
                        mHandler.sendEvent(integer);
                    }
                });
                break;
            case LIST_DATA_RESET:
                resetListData();
                break;
            case CAT_PRE_MAIL:
                goToPreMailActivity();
                break;
            case CAT_NEXT_MAIL:
                goToNextMailActivity();
                break;
            case MAIL_PERSON_INFO:
                goToPersonInfo();
                break;
        }
    }


    private void initContentAndUi() {
        MailBean mail = data.getMail();
        setRedFlagIconHide(mail.getEmailFlag());
        setAttachmentIconHide(mail.getAttachmentsCount());
        setMailSubject(mail.getSubject());
        setEmailTime(mail.getSendTime());
        setSenderName(mail.getDisplayName());
        setHeaderText(mail);
        setSentToPeople(mail);
    }

    private void refreshUI(final Event event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            refreshUI(data.getMail());
        } else {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshUI(data.getMail());
                }
            });
        }
    }

    private void refreshUI(MailBean mail) {
        //刷新红旗、已读、未读状态
        if (mail.isAnsweredMail()) {
            answeredIcon.setVisibility(View.VISIBLE);
            seenPoint.setVisibility(View.GONE);
            forwardIcon.setVisibility(View.GONE);
        } else if (mail.isForwardMail()) {
            answeredIcon.setVisibility(View.GONE);
            seenPoint.setVisibility(View.GONE);
            forwardIcon.setVisibility(View.VISIBLE);
        } else if (!mail.isSeenMail()) {
            answeredIcon.setVisibility(View.GONE);
            seenPoint.setVisibility(View.VISIBLE);
            forwardIcon.setVisibility(View.GONE);
        } else {
            answeredIcon.setVisibility(View.GONE);
            seenPoint.setVisibility(View.GONE);
            forwardIcon.setVisibility(View.GONE);
        }
        redFlagIcon.setVisibility(mail.isRedFlagMail() ? View.VISIBLE : View.GONE);
    }

    private void notifyDataSetChanged() {
        if (myAdapter != null) {
            myAdapter.notifyDataSetChanged();
        }
    }

    private void toComposeMail(Bundle bundle) {
        final List<MailAttachment> attachmentList = myAdapter.getList();
        bundle.putSerializable("mail", data.getMail());
        if (data.getMail().getAttachmentsCount() > 0) {

            Popup.toCompose(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (MailAttachment attach : attachmentList) {
                        if (!attach.isDownLoadFinished()) {
                            MailToast.show("请先下载附件");
                            return;
                        }
                    }
                    bundle.putBoolean("attachment", true);
                    Launcher.startActivity(mActivity, ComposeActivity.class, bundle);
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bundle.putBoolean("attachment", false);
                    Launcher.startActivity(mActivity, ComposeActivity.class, bundle);
                }
            });
        } else {
            bundle.putBoolean("attachment", false);
            Launcher.startActivity(mActivity, ComposeActivity.class, bundle);
        }
    }

    private void goToPreMailActivity() {
        if (!SEARCH_ACTIVITY.equals(data.getFrom())
                && !INBOX_ACTIVITY.equals(data.getFrom())
                && !INTERACT_ACTIVITY.equals(data.getFrom())) {
            return;
        }
        int pos = getPreMinusPos(data);
        if (pos < 0) {
            return;
        }
        mActivity.finish();
        goToEmailDetailActivity(pos, data.getFrom());
        mActivity.overridePendingTransition(R.anim.cat_pre_email_anim_in, R.anim.cat_pre_email_anim_out);
    }

    private void goToNextMailActivity() {
        if (!SEARCH_ACTIVITY.equals(data.getFrom())
                && !INBOX_ACTIVITY.equals(data.getFrom())
                && !INTERACT_ACTIVITY.equals(data.getFrom())) {
            return;
        }
        int pos = getNextAddPos(data);
        if (pos < 0) {
            return;
        }
        mActivity.finish();
        goToEmailDetailActivity(pos, data.getFrom());
        mActivity.overridePendingTransition(R.anim.cat_next_email_anim_in, R.anim.cat_next_email_anim_out);

    }

    private void goToEmailDetailActivity(int position, String from) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_POSITION, position);
        bundle.putString(KEY_FROM, from);
        Launcher.startActivity(mActivity, DetailsPageActivity.class, bundle);
    }

    private void goToPersonInfo() {
    }

}
