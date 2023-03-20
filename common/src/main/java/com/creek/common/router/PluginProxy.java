package com.creek.common.router;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.creek.router.annotation.CreekMethod;

public interface PluginProxy {
    void attach(Activity proxyActivity);
    void resources(Resources res);
    void setContentView(View view);
    void setContentView(int layoutResID);
    <T extends View> T  findViewById(int id);


    /**
     * 生命周期
     */
    void onCreate(Bundle saveInstanceState);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();

    void onSaveInstanceState(Bundle outState);

    boolean onTouchEvent(MotionEvent event);

    void onBackPressed();


    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
}
