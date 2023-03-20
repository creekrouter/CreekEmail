package com.creek.mail.home.manage;

import android.app.Activity;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.IdRes;

import com.creek.mail.R;
import com.creek.mail.home.adapter.FolderAdapter;
import com.creek.mail.home.msg.EventID;
import com.creek.mail.home.msg.EventListen;
import com.creek.mail.home.msg.HomeHandler;

public class MenuListManager implements EventListen {
    private Activity mActivity;
    private HomeHandler mHandler;
    private ListView menuList;
    private FolderAdapter adapter;
    private FolderManager folderManager;


    public MenuListManager(Activity activity, HomeHandler handler, FolderManager folderManager) {
        this.mActivity = activity;
        this.mHandler = handler;
        this.mHandler.addRegister(this);
        this.folderManager = folderManager;

    }

    @Override
    public boolean onMessageCome(int eventId, Message message) {
        if (eventId == EventID.View_Init) {
            initView();
        } else if (eventId == EventID.Folder_Menu_List_Refresh) {
            refresh();
            return true;
        }
        return false;
    }

    private <T extends View> T findViewById(@IdRes int id) {
        return mActivity.findViewById(id);
    }


    private void initView() {
        menuList = findViewById(R.id.lv_mail_menu);
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message msg = mHandler.obtainMessage();
                msg.what = EventID.Folder_Menu_List_Click;
                msg.arg1 = position;
                mHandler.sendMessage(msg);
            }
        });

        adapter = new FolderAdapter(mActivity, folderManager.getFolders());
        menuList.setAdapter(adapter);
        setListViewHeight(menuList);
    }


    private void refresh() {
        adapter.refresh(folderManager.getFolders());
        setListViewHeight(menuList);
    }

    private void setListViewHeight(ListView listView) {
        if (listView == null) {
            return;
        }
        FolderAdapter adapter = (FolderAdapter) listView.getAdapter();
        if (adapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View item = adapter.getView(i, null, listView);
            item.measure(0, 0);
            totalHeight += item.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
