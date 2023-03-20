package com.creek.mail.details.part;

import static com.creek.mail.details.msg.EvenId.LOADING_DISMISS;
import static com.creek.mail.details.msg.EvenId.LOADING_SHOW;
import static com.creek.mail.details.msg.EvenId.POP_WINDOW_MAIL_HTTP;
import static com.creek.mail.details.msg.EvenId.POP_WINDOW_MAIL_TO;
import static com.creek.mail.details.msg.EvenId.POP_WINDOW_REPLY;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;


import com.creek.common.ContactEntity;
import com.creek.common.EmailData;
import com.creek.common.MailBean;
import com.creek.common.constant.Const;
import com.creek.common.entities.SendData;
import com.creek.common.interfaces.CommonCallBack;
import com.creek.common.interfaces.EventCallBack;
import com.creek.common.router.Launcher;
import com.creek.mail.compose.Compose;
import com.creek.mail.compose.ComposeActivity;
import com.creek.mail.details.msg.EvenId;
import com.creek.mail.details.msg.WatchDog;
import com.creek.mail.details.msg.MsgHandler;
import com.creek.mail.details.msg.Event;
import com.creek.mail.sync.MailSender;
import com.creek.common.dialog.LoadingDialog;
import com.creek.mail.pop.Popup;
import com.mail.tools.MailToast;

import java.util.ArrayList;
import java.util.List;


public class DetailMailPopupWindow implements WatchDog {

    private Activity mActivity;
    private LoadingDialog mailLoading;
    protected MsgHandler mHandler;
    protected EmailData data;
    private String mQuickReplyContent = "";
    private ArrayList<ContactEntity> contactList;


    public DetailMailPopupWindow(Activity activity, MsgHandler handler, EmailData emailData) {
        this.mActivity = activity;
        mHandler = handler;
        data = emailData;

    }


    private void routerWeb(final Context context, @NonNull final String url, String menuId, final String title, boolean titleFlag) {


    }

    @Override
    public void onMsgCome(Event event) {
        switch (event.eventID) {
            case POP_WINDOW_REPLY:
                Popup.quickReply(mQuickReplyContent, data.getMail(), new EventCallBack<Integer, String>() {
                    @Override
                    public void onEvent(Integer integer, String s) {
                        mQuickReplyContent = s;
                        if (integer == 1) {
                            goToWriteActivity();
                        } else if (integer == 2) {
                            sendEmail((String) event.info);
                        }

                    }
                });
                break;
            case POP_WINDOW_MAIL_TO:
                writeMailTo((String) event.info);
                break;
            case POP_WINDOW_MAIL_HTTP:
                routerWeb(mActivity, (String) event.info, "", "", true);
                break;
            case LOADING_SHOW:
                showMailLoading();
                break;
            case LOADING_DISMISS:
                dismissMailLoading();
                break;
        }
    }


    private void goToWriteActivity() {
        Bundle bundle = new Bundle();
        bundle.putInt("type", Compose.TYPE_REPLY);
        bundle.putSerializable("mail", data.getMail());
        if (!TextUtils.isEmpty(mQuickReplyContent)) {
            bundle.putString("wordFromRepalay", mQuickReplyContent);
        }
        Launcher.startActivity(mActivity, ComposeActivity.class, bundle);
    }

    private void sendEmail(String mailBodyHtml) {
        mailLoading = new LoadingDialog.Builder().setText("正在发送").cancelAble(false).build(mActivity);
        mailLoading.setBackPressDismiss(true);
        mailLoading.show();

        List<ContactEntity> toList = new ArrayList<>();
        if (contactList == null) {
            contactList = ContactEntity.getContactList(data.getMail());
        }
        for (ContactEntity entity : contactList) {
            if (entity.getType() == Const.MAIL_CC || entity.getType() == Const.MAIL_SEND) {
                toList.add(entity);
            }
        }
        String[] receiverMails = data.getMail().getReceiver_email().split(";");
        String[] ccMails = data.getMail().getCc_email().split(";");
        String start = "<a  href=\"mailto:";
        String middle = "\">";
        String end = "</a>&nbsp;&nbsp;";
        String result = "";
        for (int i = 0; i < receiverMails.length; i++) {
            result += start + receiverMails[i] + middle + receiverMails[i] + end;
        }
        String ccResult = "";
        for (int i = 0; i < ccMails.length; i++) {
            if (ccMails[i] != null && ccMails[i].trim().length() > 0) {
                ccResult += start + ccMails[i] + middle + ccMails[i] + end;
            }
        }
        if (ccResult.length() > 0) {
            ccResult = "<div><b>抄送:</b>&nbsp; " + ccResult + "</div>";
        }

        String fromAndToInfo = "<div><br /> <div style=\"padding-bottom: 20px;\"> <div style=\"background-color:#eee\">"
                + "<div><b>发件人:</b>&nbsp; <a  href=\"mailto:" + data.getMail().getSend_email() + "\">" + data.getMail().getSend_email() + "</a></div>"
                + "<div><b>发送时间:</b>&nbsp;" + data.getMail().getSend_time() + "</div>"
                + "<div><b>收件人:</b>&nbsp; "
                + result
                + "</div>"
                + ccResult
                + "<div><b>主题:</b>&nbsp; <i>" + data.getMail().getSubject() + "</i></div>"
                + "</div></div>";
        String subjectStr = "回复:" + data.getMail().getSubject();
        String contentStr = "<div>" + mQuickReplyContent.replace("\n", "<br/>") + Const.MAIL_SIGNATURE +
                Const.MAIL_SIGNATURE_SUFFIX + "<br/>" + fromAndToInfo + "<br/>" + mailBodyHtml;


        SendData data = new SendData();
        for (ContactEntity entity : toList) {
            data.toAddressList.add(entity.getEmail());
        }
        data.setSubject(subjectStr);
        data.setBodyStr(contentStr);


        MailSender.send(data, new CommonCallBack<Void, String>() {
            @Override
            public void success(Void unused) {
                mailLoading.dismiss();
                MailToast.show("回复成功");
                mHandler.sendEvent(EvenId.MAIL_ANSWERED);
                mActivity.finish();
            }

            @Override
            public void fail(final String errorStr) {
                mailLoading.dismiss();
                MailToast.show("回复失败");
            }
        });

    }

    private void showMailLoading() {
        mailLoading = new LoadingDialog.Builder().setText("正在加载").cancelAble(false).build(mActivity);
        mailLoading.setBackPressDismiss(true);
        mailLoading.show();
    }

    private void dismissMailLoading() {
        if (mailLoading != null) {
            mailLoading.dismiss();
        }
    }

    private void writeMailTo(final String mailStr) {
        if (mActivity != null && !mActivity.isFinishing()) {
            if (mailStr == null || mailStr.length() == 0) {
                return;
            }
            MailBean mailBean = new MailBean();
            mailBean.setCc_email(mailStr);
            mailBean.setCc_email_display_name(mailStr);

            Popup.mailTo(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", Compose.TYPE_MAILTO);
                    bundle.putSerializable("mail", mailBean);
                    Launcher.startActivity(mActivity, ComposeActivity.class, bundle);
                }
            });
        }

    }
}
