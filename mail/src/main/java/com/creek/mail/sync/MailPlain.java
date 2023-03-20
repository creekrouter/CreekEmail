package com.creek.mail.sync;


import android.os.Looper;
import android.text.TextUtils;
import android.widget.TextView;

import com.creek.common.MailBean;
import com.creek.common.interfaces.ConfirmCallBack;
import com.creek.database.DBManager;
import com.creek.database.constants.SQLiteConstant;
import com.creek.sync.MailSync;
import com.mail.tools.context.AppContext;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MailPlain {

    public static final int MAX_THREADS = 10;
    private ExecutorService mThreadPool = Executors.newFixedThreadPool(MAX_THREADS);

    public void load(MailBean mail, TextView textView) {
        if (mail.getPlainTxt() != null) {
            textView.setText(mail.getPlainTxt());
            return;
        }

        textView.setTag(mail.uid());
        mThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                fetch(mail, new ConfirmCallBack<String>() {
                    @Override
                    public void onConfirm(String text) {
                        String content = getCharsetText(mail.getPlainCharset(), text);
                        //插入数据库
                        DBManager.updatePlainByUid(SQLiteConstant.MAIL_DETAIL, mail.folderName(), mail.uid(), content);
                        mail.setPlainTxt(content);
                        if ((Long) textView.getTag() == mail.uid()) {
                            textView.setText(mail.getPlainTxt());
                        }
                    }
                });
            }
        });

    }

    private String getCharsetText(String charset, String text) {
        if (!TextUtils.isEmpty(charset) && !("utf-8".equalsIgnoreCase(charset) || "us-ascii".equalsIgnoreCase(charset))) {
            try {
                return new String(text.getBytes(StandardCharsets.ISO_8859_1), charset);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return text;
    }

    private static void fetch(MailBean mailBean, ConfirmCallBack<String> callBack) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            if (mailBean.getMainPart() != null) {
                MailSync.syncMailPlain(mailBean.getMainPart(), mailBean.folderName(), mailBean.uid(), callBack);
            } else {
                MailSync.getMailPlain(mailBean.folderName(), mailBean.uid(), callBack);
            }
        } else {
            AppContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mailBean.getMainPart() != null) {
                        MailSync.syncMailPlain(mailBean.getMainPart(), mailBean.folderName(), mailBean.uid(), callBack);
                    } else {
                        MailSync.getMailPlain(mailBean.folderName(), mailBean.uid(), callBack);
                    }
                }
            });
        }
    }
}