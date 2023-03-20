package com.creek.mail.home.swipe;

import static com.mail.tools.ToolDip.dip2px;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

public class NewSwipeMenuCreator extends SwipeMenuCreator {
    private int menuWidth = 56;//菜单宽度


    public NewSwipeMenuCreator(Context context) {
        super(context);
    }

    public void create(SwipeMenu menu) {

        SwipeMenuItem moveItem = new SwipeMenuItem(context);
        moveItem.setBackground(new ColorDrawable(Color.parseColor("#3CD498")));
        moveItem.setWidth(dip2px(menuWidth));
        moveItem.setTitle("移动");
        moveItem.setTitleSize(16);
        moveItem.setTitleColor(Color.WHITE);
        menu.addMenuItem(moveItem);

        SwipeMenuItem readItem = new SwipeMenuItem(context);
        readItem.setBackground(new ColorDrawable(Color.parseColor("#36AAFB")));
        readItem.setWidth(dip2px(menuWidth));
        readItem.setTitle("标为已读");
        readItem.setTitleSize(16);
        readItem.setTitleColor(Color.WHITE);
        menu.addMenuItem(readItem);

        SwipeMenuItem unreadItem = new SwipeMenuItem(context);
        unreadItem.setBackground(new ColorDrawable(Color.parseColor("#36AAFB")));
        unreadItem.setWidth(dip2px(menuWidth));
        unreadItem.setTitle("标为未读");
        unreadItem.setTitleSize(16);
        unreadItem.setTitleColor(Color.WHITE);
        menu.addMenuItem(unreadItem);

        SwipeMenuItem flagItem = new SwipeMenuItem(context);
        flagItem.setBackground(new ColorDrawable(Color.parseColor("#F5981D")));
        flagItem.setWidth(dip2px(menuWidth));
        flagItem.setTitle("取消红旗");
        flagItem.setTitleSize(16);
        flagItem.setTitleColor(Color.WHITE);
        menu.addMenuItem(flagItem);


        SwipeMenuItem unflagItem = new SwipeMenuItem(context);
        unflagItem.setBackground(new ColorDrawable(Color.parseColor("#F5981D")));
        unflagItem.setWidth(dip2px(menuWidth));
        unflagItem.setTitle("标为红旗");
        unflagItem.setTitleSize(16);
        unflagItem.setTitleColor(Color.WHITE);
        menu.addMenuItem(unflagItem);


        SwipeMenuItem deleteItem = new SwipeMenuItem(context);
        deleteItem.setBackground(new ColorDrawable(Color.parseColor("#FF5757")));
        deleteItem.setWidth(dip2px(menuWidth));
        deleteItem.setTitle("删除");
        deleteItem.setTitleSize(16);
        deleteItem.setTitleColor(Color.WHITE);
        menu.addMenuItem(deleteItem);
    }

}
