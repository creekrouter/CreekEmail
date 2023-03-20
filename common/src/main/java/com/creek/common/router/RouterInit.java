package com.creek.common.router;

import android.app.Application;
import android.content.Context;

import com.creek.common.CreekPath;
import com.creek.router.CreekRouter;
import com.creek.router.annotation.CreekMethod;

public class RouterInit {
    public static Context appContext;

    public static boolean isApk = true;
    public static String unZipPath;

    @CreekMethod(path = CreekPath.Mail_Plugin_Init)
    public static void init(Context context, Application application, String unArPath) {
        isApk = false;
        unZipPath = unArPath;
        appContext = context.getApplicationContext();
        CreekRouter.functionRun(CreekPath.Mail_BroadCast_Application_Init, appContext, application);
    }

    @CreekMethod(path = CreekPath.Mail_Plugin_Get_First_Page_Path)
    public static String getFirstPage() {
        return CreekPath.Mail_Page_Login_Activity;
    }

    public static void initApplication(Context context, Application application) {
        isApk = true;
        appContext = context.getApplicationContext();
        CreekRouter.functionRun(CreekPath.Mail_BroadCast_Application_Init, appContext, application);
    }

    @CreekMethod(path = CreekPath.App_Context)
    public static Context getAppContext() {
        return appContext;
    }
}
