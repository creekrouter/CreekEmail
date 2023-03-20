package com.creek.mail.details.web;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.creek.common.constant.ConstJs;
import com.creek.mail.details.msg.EvenId;
import com.creek.mail.details.msg.MsgHandler;

public class BrowserClient extends WebViewClient {
    protected MsgHandler mHandler;

    public BrowserClient(MsgHandler handler) {
        mHandler = handler;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith("mailto:")) {
            mHandler.sendEvent(EvenId.POP_WINDOW_MAIL_TO, url.substring("mailto:".length()));
        } else if (url.startsWith("http")) {
            mHandler.sendEvent(EvenId.POP_WINDOW_MAIL_HTTP, url);
        }
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        view.loadUrl(ConstJs.resetHtml);
        mHandler.sendEvent(EvenId.WEB_VIEW_LOAD_FINISHED);

    }
}
