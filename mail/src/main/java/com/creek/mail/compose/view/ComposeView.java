package com.creek.mail.compose.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.creek.mail.compose.js.ComposeJsBridge;
import com.creek.mail.compose.manager.DataManager;
import com.creek.mail.compose.msg.ComposeHandler;
import com.creek.common.constant.ConstBrowser;

public class ComposeView extends RichEditor {
    public ComposeView(Context context) {
        super(context);
        init();
    }

    public ComposeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ComposeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //mEditor.setEditorHeight(400);
        //输入框显示字体的大小
        setEditorFontSize(15);
        //输入框显示字体的颜色
        setEditorFontColor(Color.BLACK);
        //输入框背景设置
        setEditorBackgroundColor(Color.WHITE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        //输入框文本padding
        setPadding(0, 0, 0, 0);
        //输入提示文本
        setPlaceholder("请输入正文内容...");
        //是否允许输入
        //mEditor.setInputEnabled(false);

    }

    public void initWebViewSetting(DataManager dataManager, ComposeHandler handler) {
        String uaStr = getSettings().getUserAgentString().concat(ConstBrowser.WebView_User_Agent);
        getSettings().setUserAgentString(ConstBrowser.getCustomUserAgent(uaStr));

        getSettings().setDefaultTextEncodingName("utf-8");
        getSettings().setJavaScriptEnabled(true);
        //允许webview对文件的操作
//        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
//        getSettings().setAllowFileAccess(true);
//        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        ComposeJsBridge jsBridge = new ComposeJsBridge(dataManager,handler);
        addJavascriptInterface(jsBridge, "jsAndroid");
    }
}
