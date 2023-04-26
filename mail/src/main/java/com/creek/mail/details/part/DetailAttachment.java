package com.creek.mail.details.part;

import static com.creek.mail.details.msg.EvenId.ATTACHMENT_INFO_LOAD_START;
import static com.creek.mail.details.msg.EvenId.DOWNLOAD_ALL_ATTACHMENT;
import static com.creek.mail.details.msg.EvenId.DOWNLOAD_AUTO_ATTACHMENT;
import static com.creek.mail.details.msg.EvenId.DOWNLOAD_SINGLE_ATTACHMENT;
import static com.creek.mail.details.msg.EvenId.VIEW_ATTACHMENT;

import android.app.Activity;
import android.text.TextUtils;

import com.creek.common.CreekPath;
import com.creek.common.EmailData;
import com.creek.common.MailAttachment;
import com.creek.common.constant.Const;
import com.creek.common.interfaces.CommonCallBack;
import com.creek.database.DBManager;
import com.creek.mail.details.msg.EvenId;
import com.creek.mail.details.msg.MsgHandler;
import com.creek.mail.details.msg.Event;
import com.creek.mail.details.msg.WatchDog;
import com.creek.mail.pages.ImageViewActivity;
import com.creek.mail.sync.Fetcher;
import com.creek.router.CreekRouter;
import com.mail.tools.MailToast;
import com.mail.tools.ThreadPool;

import java.io.File;
import java.util.List;

public class DetailAttachment implements WatchDog {
    protected Activity mActivity;
    protected EmailData data;

    protected MsgHandler mHandler;


    public DetailAttachment(Activity activity, MsgHandler handler,EmailData emailData) {
        mActivity = activity;
        mHandler = handler;
        data = emailData;
    }

    @Override
    public void onMsgCome(Event event) {
        switch (event.eventID) {
            case ATTACHMENT_INFO_LOAD_START:
                startLoadAttachmentFromDB();
                break;
            case DOWNLOAD_SINGLE_ATTACHMENT:
                download(data.getAttachmentList().get(event.arg1));
                break;
            case DOWNLOAD_AUTO_ATTACHMENT:
                autoDownLoadAttachment();
                break;
            case DOWNLOAD_ALL_ATTACHMENT:
                downLoadAllAttachment();
                break;
            case VIEW_ATTACHMENT:
                viewAttachment(data.getAttachmentList().get(event.arg1));
                break;
        }
    }


    protected void viewAttachment(MailAttachment attachment) {
        String path = attachment.getFile_path();
        String name = attachment.getFile_name();
        // 查看附件
        ImageViewActivity.goToImageActivity(mActivity,path);
    }

    protected final boolean download(MailAttachment attachment) {
        if (attachment == null || attachment.getDownload_state() != Const.ATAACHMENTS_DOWNLOAD_STATE_DEFAULT) {
            return false;
        }
        attachment.setDownloadStateStart();
        mHandler.sendEvent(EvenId.LIST_NOTIFY_DATA_CHANGE);
        Fetcher.download(attachment, new CommonCallBack<MailAttachment,String>() {
            @Override
            public void success(MailAttachment attach) {
                //更新listview中的数据
                attach.setDownloadStateFinished();
                mHandler.sendEvent(EvenId.LIST_NOTIFY_DATA_CHANGE);
            }

            @Override
            public void fail(String message) {
                MailToast.show(message);
            }
        });
        return true;
    }

    protected final void autoDownLoadAttachment() {
        //小于１MB大小的附件自动下载
        for (MailAttachment _mailAttachment : data.getAttachmentList()) {
            if (_mailAttachment.getFile_size() >= 1000000) {
                continue;
            }
            download(_mailAttachment);
        }
    }

    protected final void downLoadAllAttachment() {
        boolean isAllStart = true;
        //小于１MB大小的附件自动下载
        for (MailAttachment _mailAttachment : data.getAttachmentList()) {
            if (!download(_mailAttachment)) {
                isAllStart = false;
            }
        }
        if (isAllStart) {
            MailToast.show("正在下载");
        }
    }

    protected void startLoadAttachmentFromDB() {
        ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                final List<MailAttachment> list = DBManager.selectMailAttachmentByUID(data.getMail().uid());
                if (list == null || list.size() == 0) {
                    return;
                }
                //probably,can not get message attachment`s name
                for (MailAttachment attachment : list) {
                    if (!TextUtils.isEmpty(attachment.getFile_path()) && new File(attachment.getFile_path()).exists()) {
                        attachment.setDownloadStateFinished();
                    } else {
                        attachment.setDownloadStateDefault();
                    }
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        data.getAttachmentList().clear();
                        data.getAttachmentList().addAll(list);
                        mHandler.sendEvent(EvenId.ATTACHMENT_INFO_LOAD_FINISH);
                    }
                });

            }
        });
    }
}
