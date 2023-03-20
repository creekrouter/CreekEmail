package com.creek.common.pop.viewinterface;

import android.view.View;

import com.creek.common.pop.PopItemAction;

public interface PopWindowInterface {

    void setView(View view);

    void addContentView(View view);

    void addItemAction(PopItemAction popItemAction);

    void setIsShowLine(boolean isShowLine);

    void setIsShowCircleBackground(boolean isShow);

    void setPopWindowMargins(int leftMargin, int topMargin, int rightMargin, int bottomMargin);

    interface OnStartShowListener {
        void onStartShow(PopWindowInterface popWindowInterface);
    }

    interface OnStartDismissListener {
        void onStartDismiss(PopWindowInterface popWindowInterface);
    }
}
