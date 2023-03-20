package com.mail.tools.context;

import static com.creek.common.router.RouterInit.isApk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.creek.common.CreekPath;
import com.creek.router.CreekRouter;
import com.creek.router.annotation.CreekMethod;

import java.util.ArrayList;
import java.util.List;

public class AppContext {

    private static Context appContext;
    private static Application application;
    private static ActivityLifecycle mLifecycle = new ActivityLifecycle();
    private static Handler sHandler = new Handler(Looper.getMainLooper());

    @CreekMethod(path = CreekPath.Mail_BroadCast_Application_Init)
    public static void onReceive(Context context, Application app) {
        appContext = context.getApplicationContext();
        application = app;
        application.registerActivityLifecycleCallbacks(mLifecycle);
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

    public static Activity getTopActivity() {
        return mLifecycle.getTopActivity();
    }

    public static List<Activity> getActivities(String clsName) {
        return mLifecycle.getActivityMap(clsName);
    }

    public static List<Activity> getAllActivity() {
        List<Activity> list = new ArrayList<>();
        for (String key : mLifecycle.getMap().keySet()) {
            list.addAll(mLifecycle.getActivityMap(key));
        }
        return list;
    }

    public static Activity getActivity(String clsName) {
        List<Activity> list = getActivities(clsName);
        if (list == null || list.size() == 0) {
            return null;
        }
        if (isApk) {
            return list.get(0);
        } else {
            return CreekRouter.methodCall("host_get_plugin_activity_instance", list.get(0));
        }
    }

    public static void runOnUiThread(Runnable runnable) {
        sHandler.post(runnable);
    }
}
