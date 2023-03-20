package com.creek.database.init;

import android.app.Application;
import android.content.Context;

import com.creek.common.CreekPath;
import com.creek.router.annotation.CreekMethod;

public class GlobalVar {
    private static Context appContext;
    private static String DBName = "CreekEmail";

    @CreekMethod(path = CreekPath.Mail_BroadCast_Application_Init)
    public static void init(Context context, Application app) {
        appContext = context.getApplicationContext();
    }

    @CreekMethod(path = CreekPath.Mail_BroadCast_User_Init)
    public static void init(String userEmail) {
        DBName = userEmail;
    }


    public static Context getAppContext() {
        return appContext;
    }

    public static String getDBName() {
        return DBName;
    }

}
