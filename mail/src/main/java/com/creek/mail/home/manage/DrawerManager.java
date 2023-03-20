package com.creek.mail.home.manage;

import android.app.Activity;
import android.os.Message;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener;

import com.creek.mail.R;
import com.creek.mail.home.msg.EventID;
import com.creek.mail.home.msg.EventListen;
import com.creek.mail.home.msg.HomeHandler;

public class DrawerManager implements DrawerListener, EventListen {

    private HomeHandler mHandler;
    private DrawerLayout mDrawerLayout;
    private Activity mActivity;


    public DrawerManager(HomeHandler handler, Activity activity) {
        this.mHandler = handler;
        this.mActivity = activity;
        this.mHandler.addRegister(this);
    }


    @Override
    public boolean onMessageCome(int eventId, Message message) {
        if (eventId == EventID.View_Init) {
            initView();
        } else if (eventId == EventID.View_Close_Drawer) {
            mDrawerLayout.closeDrawers();
            return true;
        } else if (eventId == EventID.View_Open_Drawer) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
            return true;
        } else if (eventId == EventID.View_Toggle_Drawer) {
            toggleDrawerMenu();
            return true;
        } else if (eventId == EventID.View_Drawer_Lock_Mode_UNLOCKED) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            return true;
        } else if (eventId == EventID.View_Drawer_Lock_Mode_CLOSED) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            return true;
        }
        return false;
    }

    private void initView() {
        this.mDrawerLayout = this.mActivity.findViewById(R.id.drawer_layout);
        this.mDrawerLayout.addDrawerListener(this);
    }

    public void toggleDrawerMenu() {
        boolean open = mDrawerLayout.isDrawerOpen(Gravity.LEFT);
        if (open) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            mDrawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
        this.mHandler.sendEmptyMessage(EventID.Sync_Folder_Menu_UnRead_Count);
    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

}
