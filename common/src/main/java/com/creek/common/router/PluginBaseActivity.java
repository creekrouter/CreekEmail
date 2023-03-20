package com.creek.common.router;

import static com.creek.common.router.RouterInit.isApk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;


import com.creek.router.CreekRouter;
import com.creek.router.annotation.CreekMethod;

public class PluginBaseActivity extends FragmentActivity implements PluginProxy {
    public Activity mContext = this;
    public Resources mResources;
    public View mRootView;


    @Override
    @CreekMethod(path = "plugin_activity_attache")
    public void attach(Activity proxyActivity) {
        this.mContext = proxyActivity;
    }

    @CreekMethod(path = "plugin_activity_root_view")
    public void rootView(RelativeLayout rootView){

    }

    @Override
    @CreekMethod(path = "plugin_activity_resources")
    public void resources(Resources res) {
        this.mResources = res;
    }

    @Override
    @CreekMethod(path = "plugin_activity_setContentView_view")
    public void setContentView(View view) {
        if (isApk) {
            super.setContentView(view);
            return;
        }
        if (mContext != null) {
            mContext.setContentView(view);
        }
    }

    @Override
    @CreekMethod(path = "plugin_activity_setContentView_id")
    public void setContentView(int layoutResID) {
        if (isApk) {
            super.setContentView(layoutResID);
            return;
        }
        if (mContext != null) {
            XmlResourceParser parser = mResources.getLayout(layoutResID);
            mRootView = LayoutInflater.from(mContext).inflate(parser, null);
            mContext.setContentView(mRootView);
        }
    }


    @Override
    @CreekMethod(path = "plugin_activity_findViewById")
    public <T extends View> T findViewById(@IdRes int id) {
        if (isApk) {
            return super.findViewById(id);
        }
        return mContext.findViewById(id);
    }

    public Intent getIntent() {
        if (isApk) {
            return super.getIntent();
        }
        return mContext.getIntent();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    @CreekMethod(path = "plugin_activity_onCreate")
    public void onCreate(Bundle saveInstanceState) {
        if (isApk) {
            super.onCreate(saveInstanceState);
        }

    }

    @SuppressLint("MissingSuperCall")
    @Override
    @CreekMethod(path = "plugin_activity_onStart")
    public void onStart() {
        if (isApk) {
            super.onStart();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    @CreekMethod(path = "plugin_activity_onResume")
    public void onResume() {
        if (isApk) {
            super.onResume();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    @CreekMethod(path = "plugin_activity_onPause")
    public void onPause() {
        if (isApk) {
            super.onPause();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    @CreekMethod(path = "plugin_activity_onStop")
    public void onStop() {
        if (isApk) {
            super.onStop();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    @CreekMethod(path = "plugin_activity_onDestroy")
    public void onDestroy() {
        if (isApk) {
            super.onDestroy();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    @CreekMethod(path = "plugin_activity_onSaveInstanceState")
    public void onSaveInstanceState(Bundle outState) {
        if (isApk) {
            super.onSaveInstanceState(outState);
        }
    }


    @Override
    @CreekMethod(path = "plugin_activity_onTouchEvent")
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    @CreekMethod(path = "plugin_activity_onBackPressed")
    public void onBackPressed() {
        if (isApk){
            super.onBackPressed();
        }
    }

    public void finish() {
        if (isApk) {
            super.finish();
            return;
        }
        mContext.finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isApk){
            return super.onKeyDown(keyCode,event);
        }
        return mContext.onKeyDown(keyCode, event);
    }

    @SuppressLint("MissingSuperCall")
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (isApk) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @SuppressLint("MissingSuperCall")
    @CreekMethod(path = "plugin_activity_onActivityResult")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (isApk) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @CreekMethod(path = "plugin_activity_onWindowFocusChanged")
    public void onWindowFocusChanged(boolean hasFocus) {
        if (isApk) {
            super.onWindowFocusChanged(hasFocus);
        }
    }


    public final void startActivity(String annotateBeanPath) {
        CreekRouter.methodRun("plugin_helper_startActivity", mContext, annotateBeanPath);
    }

    public final void startActivity(Context context, Class<?> aimActivity, Bundle bundle) {
        Launcher.startActivity(context, aimActivity, bundle);
    }

    public final void startActivity(Context context, Class<?> aimActivity) {
        Launcher.startActivity(context, aimActivity);
    }
}
