package com.mail.tools;

import android.widget.Toast;

import com.mail.tools.context.AppContext;

public class MailToast {

    public static void show(String text) {
        if (text == null) {
            return;
        }
        Toast.makeText(AppContext.getAppContext(), text, Toast.LENGTH_SHORT).show();
    }

}
