package com.creek.mail.home.swipe;

import static com.mail.tools.ToolDip.dip2px;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;


public class SwipeMenuCreator {
    protected Context context;
    private int menuWidth = 60;//菜单宽度


    public SwipeMenuCreator(Context context) {
        super();
        this.context = context;
    }

    public void create(SwipeMenu menu) {
        SwipeMenuItem moreItem = new SwipeMenuItem(context);
        moreItem.setBackground(new ColorDrawable(Color.parseColor("#C6C9CC")));
        moreItem.setWidth(dip2px(menuWidth));
        moreItem.setTitle("更多");
        moreItem.setTitleSize(12);
        moreItem.setTitleColor(Color.WHITE);
        menu.addMenuItem(moreItem);

        SwipeMenuItem flagItem = new SwipeMenuItem(context);
        flagItem.setBackground(new ColorDrawable(Color.parseColor("#2A83F2")));
        flagItem.setWidth(dip2px(menuWidth));
        flagItem.setTitle("标为已读");
        flagItem.setTitleSize(12);
        flagItem.setTitleColor(Color.WHITE);
        menu.addMenuItem(flagItem);

        SwipeMenuItem unreadItem = new SwipeMenuItem(context);
        unreadItem.setBackground(new ColorDrawable(Color.parseColor("#2A83F2")));
        unreadItem.setWidth(dip2px(menuWidth));
        unreadItem.setTitle("标为未读");
        unreadItem.setTitleSize(12);
        unreadItem.setTitleColor(Color.WHITE);
        menu.addMenuItem(unreadItem);

        SwipeMenuItem deleteItem = new SwipeMenuItem(context);
        deleteItem.setBackground(new ColorDrawable(Color.parseColor("#FF3D2C")));
        deleteItem.setWidth(dip2px(menuWidth));
        deleteItem.setTitle("删除");
        deleteItem.setTitleSize(12);
        deleteItem.setTitleColor(Color.WHITE);
        menu.addMenuItem(deleteItem);
    }


}
