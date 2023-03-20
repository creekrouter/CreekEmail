package com.creek.common.pop.viewinterface;

import android.view.View;

import com.creek.common.pop.PopItemAction;
import com.creek.common.pop.PopWindow;

public interface PopViewInterface {

    void setTitleColor(int color);

    void setTitleTextSize(int textSize);

    void setMessageColor(int color);

    void setMessageTextSize(int textSize);

    void setTitleAndMessage(CharSequence title, CharSequence message);

    void addContentView(View view);

    void setPopWindow(PopWindow popWindow);

    void addItemAction(PopItemAction popItemAction);

    boolean showAble();

    void refreshBackground();

    void setIsShowLine(boolean isShowLine);

    void setIsShowCircleBackground(boolean isShow);
}
