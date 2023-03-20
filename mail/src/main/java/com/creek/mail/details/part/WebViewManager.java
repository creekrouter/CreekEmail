package com.creek.mail.details.part;

import static com.creek.mail.details.msg.EvenId.WEB_VIEW_INIT;
import static com.creek.mail.details.msg.EvenId.WEB_VIEW_QUICK_REPLY;

import android.app.Activity;


import com.creek.common.CreekPath;
import com.creek.common.constant.ConstBrowser;
import com.creek.mail.R;
import com.creek.common.EmailData;
import com.creek.mail.details.msg.EvenId;
import com.creek.mail.details.msg.WatchDog;
import com.creek.mail.details.msg.MsgHandler;
import com.creek.mail.details.EmailWebView;
import com.creek.mail.details.msg.Event;
import com.creek.router.CreekRouter;

public class WebViewManager implements WatchDog {

    private Activity mActivity;
    private EmailData data;
    private EmailWebView webViewContent;
    protected MsgHandler mHandler;

    public WebViewManager(Activity activity, MsgHandler handler,EmailData emailData) {
        this.mActivity = activity;
        mHandler = handler;
        data = emailData;
    }

    @Override
    public void onMsgCome(Event event) {
        switch (event.eventID) {
            case WEB_VIEW_INIT:
                initWebView();
                break;
            case WEB_VIEW_QUICK_REPLY:
                mHandler.sendEvent(EvenId.POP_WINDOW_REPLY, webViewContent.getHtmlContent());
                break;
        }
    }

    private void initWebView() {
        webViewContent = mActivity.findViewById(R.id.webv_content_details);
        String uaStr = webViewContent.getSettings().getUserAgentString().concat(ConstBrowser.WebView_User_Agent);
        webViewContent.getSettings().setUserAgentString(ConstBrowser.getCustomUserAgent(uaStr));
        webViewContent.setVerticalScrollBarEnabled(false);
        webViewContent.setHorizontalScrollBarEnabled(false);
        webViewContent.getSettings().setDefaultTextEncodingName("utf-8");
        webViewContent.getSettings().setJavaScriptEnabled(true);
        webViewContent.getSettings().setBuiltInZoomControls(true); // 设置显示缩放按钮
        webViewContent.getSettings().setSupportZoom(true); //支持缩放
        //不显示webview缩放按钮
        webViewContent.getSettings().setDisplayZoomControls(false);
        //使用网页viewport标签
        //  webView.getSettings().setUseWideViewPort(true);
        //  webView.getSettings().setLoadWithOverviewMode(true);
        //允许webview对文件的操作
        //webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webViewContent.getSettings().setAllowFileAccess(true);

        webViewContent.init(mActivity, data.getMail(), mHandler);
    }

}
