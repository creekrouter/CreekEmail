package com.creek.mail.details;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.creek.common.constant.ConstPath;
import com.creek.common.interfaces.CommonCallBack;
import com.creek.mail.details.msg.MsgHandler;
import com.creek.mail.details.web.BrowserClient;
import com.creek.mail.sync.Fetcher;
import com.creek.mail.sync.MailBody;
import com.libmailcore.Attachment;
import com.creek.common.MailBean;
import com.creek.mail.pages.ImageViewActivity;
import com.mail.tools.FileTool;
import com.mail.tools.MailToast;
import com.mail.tools.ThreadPool;

import java.io.File;
import java.util.HashMap;


public class EmailWebView extends WebView {
    protected MsgHandler mHandler;

    public EmailWebView(Context context) {
        super(context);
    }

    public EmailWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private String htmlContent;
    private File webViewFile = null;
    private Activity activity;
    private MailBean mMail;

    public void init(Activity context, MailBean mail, MsgHandler handler) {
        mHandler = handler;
        activity = context;
        mMail = mail;
        webViewFile = ConstPath.getMailBody(mMail);
        addJavascriptInterface(this, "jsAndroid");
        setWebViewClient(new BrowserClient(handler));

        MailBody.readHtml(ConstPath.getMailBody(mMail), new CommonCallBack<String, String>() {
            @Override
            public void success(String html) {
                htmlContent = html;
                initWebData();
            }

            @Override
            public void fail(String s) {
                initWebData();
            }
        });
    }

    private void initWebData() {

        if (webViewFile.exists() && !TextUtils.isEmpty(htmlContent)) {  //离线
            loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null);
            return;
        }

        MailBody.fetchHtml(mMail, new CommonCallBack<String, String>() {
            @Override
            public void success(String html) {
                htmlContent = html;
                loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null);
            }

            @Override
            public void fail(String s) {
                MailToast.show("正文获取失败！" + s);
            }
        });
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    @JavascriptInterface
    public void openImage(String img) {
        ImageViewActivity.goToImageActivity(activity, img.replaceFirst("file://",""));
    }


    @JavascriptInterface
    public void jsBridgeCall(String cidList, String innerHtml,String bodyHtml) {
        htmlContent = innerHtml;
        if (cidList == null || cidList.length() == 0) {
            MailBody.saveHtml(htmlContent, ConstPath.getMailBody(mMail));
            MailBody.saveHtml(bodyHtml, ConstPath.getMailBody_TagBody(mMail));
            return;
        }
        String[] cidArr = cidList.split(";");
        if (cidArr == null || cidArr.length == 0) {
            return;
        }
        Fetcher.htmlCidPhotoArr(cidArr, mMail, new CommonCallBack<HashMap<String, String>, String>() {
            @Override
            public void success(HashMap<String, String> hashMap) {

                for (String cid : hashMap.keySet()) {
                    String filePath = hashMap.get(cid);
                    htmlContent = htmlContent.replaceFirst(cid, "file://" + filePath);
                    ThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            byte[] data = FileTool.getBytes(filePath);
                            Attachment attachment = Attachment.attachmentWithData(filePath.substring(filePath.lastIndexOf("/") + 1), data);
                            attachment.setContentID(cid);
                            mMail.inlineAttachments.add(attachment);
                        }
                    });
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null);
                    }
                });
            }

            @Override
            public void fail(String s) {

            }
        });
    }
}
