package com.creek.mail.sync;

import android.text.TextUtils;

import com.creek.common.constant.Const;
import com.mail.tools.MailSp;

public class MailSign {

    public static String get() {

        String signatureHtml = "";

        String sigatureSetting = MailSp.getString(Const.get_KEY_SIGNATURE(), "");
        if (TextUtils.isEmpty(sigatureSetting)) {
            signatureHtml = Const.MAIL_SIGNATURE;
        } else {
            signatureHtml = sigatureSetting;
        }
        signatureHtml = signatureHtml + Const.MAIL_SIGNATURE_SUFFIX;
        return signatureHtml;
    }
}
