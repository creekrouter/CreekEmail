package com.creek.mail.details;

import android.content.Intent;
import android.os.Bundle;


import com.creek.common.CreekPath;
import com.creek.common.EmailData;
import com.creek.common.MailBean;
import com.creek.mail.R;
import com.creek.mail.details.msg.WatchDog;
import com.creek.mail.details.msg.EvenId;
import com.creek.mail.details.part.DetailMailOperate;
import com.creek.mail.details.part.DetailMailPopupWindow;
import com.creek.mail.details.part.EmailBeanLoader;
import com.creek.mail.details.part.DetailAttachment;
import com.creek.mail.details.part.ViewsManager;
import com.creek.mail.details.part.WebViewManager;
import com.creek.mail.sync.MailRefresh;
import com.creek.router.annotation.CreekBean;
import com.creek.router.annotation.CreekMethod;
import java.util.ArrayList;
import java.util.List;


@CreekBean(path = "Mail_Page_Details_Page_Activity")
public class DetailsPageActivity extends BaseDetailsPageActivity implements MailRefresh {

    public static final String SEARCH_ACTIVITY = "search";
    public static final String INBOX_ACTIVITY = "inbox";
    public static final String INTERACT_ACTIVITY = "interact";
    public static final String OTHER_ACTIVITY = "other";

    public static final String KEY_FROM = "from";
    public static final String KEY_POSITION = "position";
    public static final String KEY_UID = "uid";
    public static final String KEY_FOLDER = "folder";

    /*
    加载步骤：
    １、解析intent中传递过来的数据；
    ２、初始化各个组件；
    ３、初始化view部分；
    ４、获取邮件详情实体Bean，同步或异步获取；
    ５、根据邮件Bean刷新页面；
    ６、加载邮件正文webView相关；
    ７、从数据库加载邮件附件；
    ８、自动下载小于1MB的附件；
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_detail_page);
        if (checkIntentLegal()) {
            initViews();
        } else {
            finish();
        }
    }

    @Override
    public boolean parseIntent(Intent intent) {
        return IntentParser.parseIntent(intent, mData);
    }

    @Override
    public List<WatchDog> createRegister() {
        List<WatchDog> registerList = new ArrayList<>();
        registerList.add(this);
        registerList.add(new DetailMailPopupWindow(mContext, mHandler,mData));
        registerList.add(new EmailBeanLoader(mContext, mHandler,mData));
        registerList.add(new DetailMailOperate(mContext, mHandler,mData));
        registerList.add(new DetailAttachment(mContext, mHandler,mData));
        registerList.add(new ViewsManager(mContext, mHandler,mData));
        registerList.add(new WebViewManager(mContext, mHandler,mData));
        return registerList;
    }

    @Override
    public void initViews() {
        mHandler.sendEvent(EvenId.LOADING_SHOW);
        mHandler.sendEvent(EvenId.FIND_VIEW_BY_ID);
        mHandler.sendEvent(EvenId.MAIL_BEAN_INIT_START);
    }


    @Override
    public void mailBeanInitFinish() {
        mHandler.sendEvent(EvenId.MAIL_READ);
        mHandler.sendEvent(EvenId.INIT_UI_CONTENT);
        mHandler.sendEvent(EvenId.WEB_VIEW_INIT);
    }

    @Override
    public void onWebViewPageFinished() {
        mHandler.sendEvent(EvenId.ATTACHMENT_INFO_LOAD_START);
        mHandler.sendEvent(EvenId.LOADING_DISMISS);
    }

    @Override
    public void attachmentInitFinish() {
        mHandler.sendEvent(EvenId.LIST_DATA_RESET);
        mHandler.sendEvent(EvenId.DOWNLOAD_AUTO_ATTACHMENT);
    }

    @Override
    public void onMailFlagUpdate(List<MailBean> list) {
        for (MailBean mailBean : list) {
            if (mailBean.uid() == mData.getMail().uid()) {
                mData.setMail(mailBean);
                break;
            }
        }
        mHandler.sendEvent(EvenId.UI_VIEW_REFRESH);
    }

    @CreekMethod(path = CreekPath.Mail_Information_Update_Flag)
    public static void onDateUpdate(List<MailBean> list) {
//        for (Activity activity : AppContext.getActivities(DetailsPageActivity.class.getName())) {
//            ((DetailsPageActivity) activity).onMailFlagUpdate(list);
//        }

    }
}
