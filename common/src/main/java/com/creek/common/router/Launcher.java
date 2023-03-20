package com.creek.common.router;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.creek.common.constant.ConstRouter;
import com.creek.router.CreekRouter;
import com.creek.router.annotation.CreekBean;
import com.creek.router.annotation.CreekClass;

public class Launcher {

    public static void startActivity(Context context, Class<?> aimActivity) {
        startActivity(context, aimActivity, null);
    }

    public static void startActivityForResult(Fragment fragment, Class<?> aimActivity, int requestCode) {
        if (RouterInit.isApk) {
            Intent intent = new Intent(fragment.getContext(), aimActivity);
            fragment.startActivityForResult(intent, requestCode);
        }else {
            CreekBean creekBean = aimActivity.getAnnotation(CreekBean.class);
            if (creekBean == null) {
                return;
            }
            String path = creekBean.path();
            CreekRouter.methodRun(ConstRouter.Start_Activity2, fragment, path, "mail", requestCode);
        }

    }

    public static void startActivityForResult(Activity context, Class<?> aimActivity, int requestCode) {
        startActivityForResult(context, aimActivity, null, requestCode);
    }

    public static void startActivity(Context context, Class<?> aimActivity, Bundle bundle) {
        startActivityForResult((Activity) context, aimActivity, bundle, 0);
    }

    public static void startActivityForResult(Activity context, Class<?> aimActivity, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        if (RouterInit.isApk) {
            ComponentName cmp = new ComponentName(context, aimActivity);
            intent.setComponent(cmp);

            context.startActivityForResult(intent, requestCode);
        } else {
            CreekBean creekBean = aimActivity.getAnnotation(CreekBean.class);
            if (creekBean == null) {
                return;
            }
            String path = creekBean.path();
            CreekRouter.methodRun(ConstRouter.Start_Activity, context, path, "mail", intent, requestCode);
        }
    }


    private static boolean isPluginActivity(Class<?> aimActivity) {
        Class<?> clazz = aimActivity;
        while (clazz != null) {
            if (clazz == PluginBaseActivity.class) {
                return true;
            }
            clazz = clazz.getSuperclass();
        }
        return false;
    }

}
