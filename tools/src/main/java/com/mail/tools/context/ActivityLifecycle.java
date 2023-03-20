package com.mail.tools.context;

import static com.creek.common.router.RouterInit.isApk;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.creek.router.CreekRouter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


class ActivityLifecycle implements Application.ActivityLifecycleCallbacks {

    private Activity mTopActivity;
    private HashMap<String, List<Activity>> map = new HashMap<>();

    public Activity getTopActivity() {
        return mTopActivity;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mTopActivity = activity;
        String activityName = getActivityName(activity);
        if (map.containsKey(activityName)) {
            map.get(activityName).add(activity);
        } else {
            List<Activity> list = new ArrayList<>();
            list.add(activity);
            map.put(activityName, list);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        mTopActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        mTopActivity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        String activityName = getActivityName(activity);
        if (map.containsKey(activityName)) {
            map.get(activityName).remove(activity);
        }
    }

    public List<Activity> getActivityMap(String className) {
        return map.get(className);
    }

    public HashMap<String, List<Activity>> getMap() {
        return map;
    }

    private String getActivityName(Activity activity) {
        String activityName = null;
        if (isApk) {
            activityName = activity.getClass().getName();
        } else {
            Object obj = CreekRouter.methodCall("host_get_plugin_activity_instance", activity);
            if (obj != null) {
                activityName = obj.getClass().getName();
            }
        }
        return activityName;
    }
}
