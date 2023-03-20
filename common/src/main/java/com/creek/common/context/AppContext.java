package com.creek.common.context;

import android.app.Application;
import android.content.Context;

import com.creek.common.CreekPath;
import com.creek.router.annotation.CreekMethod;

public class AppContext {
    private static Context appContext;
    private static Application application;

    @CreekMethod(path = CreekPath.Mail_BroadCast_Application_Init)
    public static void onReceive(Context context, Application app) {
        appContext = context.getApplicationContext();
        application = app;
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static String getPackageName() {
        return appContext.getPackageName();
    }

    public static Context getApplication() {
        return application;
    }
}
