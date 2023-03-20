package com.mail.tools;

import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.mail.tools.context.AppContext;

/**
 * 1、获取屏幕宽高 2、dp、px、sp之间转换 Created by yxl on 2016/5/3.
 */
public class ToolDip {

    /**
     * 获得屏幕宽度
     */
    public static int getScreenWidth() {

        WindowManager wm = (WindowManager) AppContext.getAppContext().getSystemService(AppContext.getAppContext().WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕高度
     */
    public static int getScreenHeight() {
        WindowManager wm = (WindowManager) AppContext.getAppContext()
                .getSystemService(AppContext.getAppContext().WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(float pxValue) {
        final float scale = AppContext.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @return
     */

    public static int dip2px(float dipValue) {
        final float scale = AppContext.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(float pxValue) {
        final float fontScale = AppContext.getAppContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(float spValue) {
        final float fontScale = AppContext.getAppContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
