package com.creek.mail;

import com.creek.common.router.RouterInit;
import android.app.Application;


public class MailApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RouterInit.initApplication(getApplicationContext(), this);
    }

}
