package com.creek.mail.sync;

import static com.mail.tools.FileTool.readFile;

import android.text.TextUtils;

import com.creek.common.MailBean;
import com.creek.common.constant.ConstPath;
import com.creek.common.interfaces.CommonCallBack;
import com.mail.tools.FileTool;
import com.creek.sync.MailSync;
import com.creek.sync.imap.ImapManager;
import com.libmailcore.IMAPMessage;
import com.libmailcore.IMAPMessageRenderingOperation;
import com.libmailcore.MailException;
import com.libmailcore.OperationCallback;
import com.mail.tools.ThreadPool;
import com.mail.tools.context.AppContext;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MailBody {
    public static void saveHtml(String html, File desFile) {
        ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                FileTool.writeFile(html, desFile);
            }
        });
    }


    public static void readHtml(File htmlFile, CommonCallBack<String, String> callBack) {
        if (!htmlFile.exists()) {
            callBack.fail("no file");
            return;
        }

        ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                String htmlContent = readFile(htmlFile);
                if (htmlContent == null || htmlContent.length() == 0) {
                    AppContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callBack.fail("no file");
                        }
                    });
                } else {
                    AppContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callBack.success(htmlContent);
                        }
                    });
                }
            }
        });
    }

    public static void fetchHtml(MailBean mailBean, CommonCallBack<String, String> callBack) {
        File htmlFile = ConstPath.getMailBody(mailBean);
        if (htmlFile.exists()) {
            readHtml(htmlFile, callBack);
            return;
        }

        MailSync.syncMailDetails(mailBean.folderName(), mailBean.uid(), new CommonCallBack<List<IMAPMessage>, String>() {
            @Override
            public void success(List<IMAPMessage> imapMessages) {
                if (imapMessages == null || imapMessages.size() == 0) {
                    return;
                }
                final IMAPMessage imapMessage = imapMessages.get(0);
                if (imapMessage.requiredPartsForRendering().size() == 0) {
                    return;
                }
                String mineType = imapMessage.requiredPartsForRendering().get(0).mimeType();
                if ("text/plain".equals(mineType)) {
                    fetchHtml_TextPlain(mailBean, imapMessage, callBack);
                } else {
                    fetchHtml_Other(mailBean, imapMessage, callBack);
                }

            }

            @Override
            public void fail(String s) {
                callBack.fail(s);
            }
        });
    }

    private static void fetchHtml_TextPlain(MailBean mailBean, IMAPMessage imapMessage, CommonCallBack<String, String> callBack) {
        final IMAPMessageRenderingOperation renderingOperationOp = ImapManager.singleton().getSession().
                plainTextBodyRenderingOperation(imapMessage, mailBean.folderName(), true);
        renderingOperationOp.start(new OperationCallback() {
            @Override
            public void succeeded() {
                String htmlContent = renderingOperationOp.result();
                try {
                    String charset = imapMessage.requiredPartsForRendering().get(0).charset();
                    if (!TextUtils.isEmpty(charset) && !("utf-8".equalsIgnoreCase(charset) || "us-ascii".equalsIgnoreCase(charset))) {
                        htmlContent = new String(htmlContent.getBytes(StandardCharsets.ISO_8859_1), charset);
                    }
                } catch (Exception e) {
                }
                callBack.success(htmlContent);
            }

            @Override
            public void failed(MailException e) {
                callBack.fail(e.toString());
            }
        });

    }

    private static void fetchHtml_Other(MailBean mailBean, IMAPMessage imapMessage, CommonCallBack<String, String> callBack) {
        final IMAPMessageRenderingOperation renderingOperationOp = ImapManager.singleton().getSession().
                htmlBodyRenderingOperation(imapMessage, mailBean.folderName());
        renderingOperationOp.start(new OperationCallback() {
            @Override
            public void succeeded() {
                String htmlContent = renderingOperationOp.result();
                if (htmlContent.contains("<html") && htmlContent.contains("</html>")) {
                    htmlContent = htmlContent.substring(htmlContent.indexOf("<html"), htmlContent.lastIndexOf("</html>") + "</html>".length());
                }
                try {
                    String charset = imapMessage.requiredPartsForRendering().get(0).charset();
                    if (!TextUtils.isEmpty(charset) && !("utf-8".equalsIgnoreCase(charset) || "us-ascii".equalsIgnoreCase(charset))) {
                        htmlContent = new String(htmlContent.getBytes(StandardCharsets.ISO_8859_1), charset);
                    }
                } catch (Exception e) {
                }
                callBack.success(htmlContent);
            }

            @Override
            public void failed(MailException e) {
                callBack.fail(e.toString());
            }
        });
    }
}
