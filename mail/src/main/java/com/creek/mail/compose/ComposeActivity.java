package com.creek.mail.compose;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import com.creek.common.interfaces.CommonCallBack;
import com.creek.common.BaseActivity;
import com.creek.mail.compose.action.SaveEmail;
import com.creek.mail.compose.action.SendEmail;
import com.creek.mail.compose.msg.EventID;
import com.creek.mail.compose.msg.EventWatcher;
import com.creek.mail.R;
import com.creek.mail.compose.manager.ComposeManager;
import com.creek.common.MailAttachment;
import com.creek.common.dialog.LoadingDialog;
import com.creek.router.annotation.CreekBean;
import com.mail.tools.MailToast;

import java.util.List;

@CreekBean(path = "mail_page_compose_activity")
public class ComposeActivity extends BaseActivity implements EventWatcher {

    public static final int REQUEST_CODE_CHOOSE = 23;


    public static final int INSERT_IMG = 6;
    public static final int CHOOSE_PICTURE_ATTAC = 4;

    public static final int RESULTCODE_SELECTCONTACTS = 1;
    public static final int RESULTCODE_FILE_ACTIVITY = 2;
    public static final int ATTAC = 5;
    public static final int CHOCE_INNER_FILE = 100;

    public static String takePhotoPath;

    private LoadingDialog mSendingDialog;

    private boolean isMailSending = false;

    private ComposeManager manager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_emai);

        manager = new ComposeManager(mContext);
        manager.addRegister(this);

        mSendingDialog = new LoadingDialog.Builder().setText("正在发送").cancelAble(false).build(mContext);
        mSendingDialog.setBackPressDismiss(true);
        manager.parseIntent();
    }


    private void clickToSendEmail() {
        if (isMailSending) {
            return;
        }
        isMailSending = true;
        mSendingDialog.show();//显示发送中loading

        SendEmail.sendEmail(manager.getDataManager(), new CommonCallBack<Void, String>() {
            @Override
            public void success(Void unused) {
                MailToast.show("发送成功!");
                mSendingDialog.dismiss();
                finish();
            }

            @Override
            public void fail(String message) {
                MailToast.show(message);
                mSendingDialog.dismiss();
            }
        });


//        if (manager.getDataManager().mFrom == TYPE_FORWARD) {
//            //检测附加下载情况
//            if (!checkAttachment()) {
//                MailToast.show("附件正在下载中");
//                mSendingDialog.dismiss();
//                return;
//            }
//        }
//        if (manager.getDataManager().mFrom == TYPE_WRITE_MAIL) {
//
//        } else {
//            //解决回复收件人无法显示图片
//            manager.getViewManager().getComposeEditor().loadUrl(ConstJs.executeGetHtmlMethod);
//        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        manager.sendEvent(EventID.Compose_Common_Event_BackPressed);
    }


    public void setAttachmentListView(List<MailAttachment> AttachList) {
        manager.getViewManager().getAttachAdapter().setData(AttachList);
        manager.getViewManager().getAttachView().scrollToPosition(manager.getAttachList().size() - 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public boolean onEventHappen(int eventId, Message message) {
        if (eventId == EventID.Compose_Mail_Action_Save) {
            SaveEmail.saveToDraft(manager.getDataManager());
            return true;
        } else if (eventId == EventID.Compose_Mail_Action_Send) {
            clickToSendEmail();
            return true;
        } else if (eventId == EventID.Compose_Activity_Finish) {
            finish();
        }
        return false;
    }

}