package com.creek.mail.sync;

import android.os.Handler;
import android.os.Looper;

import com.creek.common.MailAttachment;
import com.creek.common.MailBean;
import com.creek.common.interfaces.CommonCallBack;
import com.creek.database.DBManager;
import com.creek.sync.MailSync;
import com.libmailcore.IMAPMessage;
import com.mail.tools.MailSp;
import com.mail.tools.context.AppContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Fetcher {
    public static void download(MailAttachment attachment, CommonCallBack<MailAttachment, String> callBack) {

        MailSync.fetchAttach(attachment, new CommonCallBack<MailAttachment, String>() {
            @Override
            public void success(MailAttachment attach) {
                //更新数据库
                DBManager.updateAttachmentFilePath(attach);
                if (Looper.getMainLooper() == Looper.myLooper()) {
                    callBack.success(attach);
                } else {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.success(attach);
                        }
                    });
                }
            }

            @Override
            public void fail(String message) {
                callBack.fail(message);
            }
        });

    }


    public static void htmlCidPhotoArr(String[] cidArr, MailBean mailBean, CommonCallBack<HashMap<String, String>, String> callBack) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            fetchCidList(cidArr, mailBean, callBack);
        } else {
            AppContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fetchCidList(cidArr, mailBean, callBack);
                }
            });
        }
    }


    private static void fetchCidList(String[] cidArr, MailBean mailBean, CommonCallBack<HashMap<String, String>, String> callBack) {
        HashMap<String, String> map = new HashMap<>();
        List<String> listCid = new ArrayList<>();
        for (String cid : cidArr) {
            if (cid.startsWith("cid:")) {
                String key = mailBean.folderName() + "_" + mailBean.uid() + "_" + cid;
                String spPath = MailSp.getString(key, "");
                if (spPath.length() > 0) {
                    map.put(cid, spPath);
                } else {
                    listCid.add(cid);
                }
            }
        }

        if (listCid.size() == 0) {
            callBack.success(map);
            return;
        }
        if (mailBean.getMainPart() == null) {
            MailSync.syncMailDetails(mailBean.folderName(), mailBean.uid(), new CommonCallBack<List<IMAPMessage>, String>() {
                @Override
                public void success(List<IMAPMessage> imapMessages) {
                    if (imapMessages == null || imapMessages.size() == 0) {
                        return;
                    }
                    IMAPMessage imapMessage = imapMessages.get(0);
                    mailBean.setMainPart(imapMessage.mainPart());
                    realFetchCidList(map, cidArr, mailBean, callBack);
                }

                @Override
                public void fail(String s) {
                    callBack.fail(s);
                }
            });
        } else {
            realFetchCidList(map, cidArr, mailBean, callBack);
        }

    }

    private static void realFetchCidList(HashMap<String, String> map, String[] cidArr, MailBean mailBean, CommonCallBack<HashMap<String, String>, String> callBack) {
        MailSync.fetchHtmlPhotoArr(cidArr, mailBean, new CommonCallBack<HashMap<String, String>, String>() {
            @Override
            public void success(HashMap<String, String> hashMap) {
                for (String cid : hashMap.keySet()) {
                    String key = mailBean.folderName() + "_" + mailBean.uid() + "_" + cid;
                    MailSp.putString(key, hashMap.get(cid));
                    map.put(cid, hashMap.get(cid));
                }
                callBack.success(map);
            }

            @Override
            public void fail(String s) {
                callBack.fail(s);

            }
        });
    }
}
