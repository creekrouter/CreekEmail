package com.creek.mail.compose.js;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.creek.mail.compose.manager.DataManager;
import com.creek.mail.compose.msg.ComposeHandler;
import com.creek.mail.pages.ImageViewActivity;
import com.mail.tools.context.AppContext;


public class ComposeJsBridge {


    private DataManager manager;
    private ComposeHandler mHandler;

    public ComposeJsBridge(DataManager dataManager, ComposeHandler handler) {
        manager = dataManager;
        mHandler = handler;
    }


    @JavascriptInterface
    public void openImage(String img) {
        ImageViewActivity.goToImageActivity(AppContext.getTopActivity(), img.replaceFirst("file://",""));
    }

    @JavascriptInterface
    public void onHtmlChanged(String html, String imagePathList, String imageCidList) {

        Log.e("lv123",imagePathList);
        Log.e("lv123",imageCidList);
        Log.e("lv123","~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"+html);
    }
}
