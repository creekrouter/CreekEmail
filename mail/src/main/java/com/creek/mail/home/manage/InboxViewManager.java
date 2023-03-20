package com.creek.mail.home.manage;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.IdRes;

import com.creek.common.router.Launcher;
import com.creek.mail.R;
import com.creek.mail.compose.Compose;
import com.creek.mail.compose.ComposeActivity;
import com.creek.mail.search.SearchActivity;
import com.creek.mail.home.msg.EventID;
import com.creek.mail.home.msg.EventListen;
import com.creek.mail.home.msg.HomeHandler;


public class InboxViewManager implements EventListen, View.OnClickListener {

    private Activity mActivity;
    private HomeHandler mHandler;
    private FolderManager folderManager;

    private TextView tvTitle, tvCount;
    private View headLayout, menuToggle, searchMail;
    private View composeMail;
    private View selectFooter, selectHeader, moreAction, noneEmail;
    private TextView mailSelectInfo;
    private CheckBox mailToggleSelect;
    private TextView mailToggleChoose;
    private View mailToggleLayout;


    public InboxViewManager(Activity activity, HomeHandler handler, FolderManager folderManager) {
        this.mActivity = activity;
        this.mHandler = handler;
        this.folderManager = folderManager;
        this.mHandler.addRegister(this);
    }

    @Override
    public boolean onMessageCome(int eventId, Message message) {
        if (eventId == EventID.View_Init) {
            initView();
        } else if (eventId == EventID.On_Folder_Changed) {
            tvTitle.setText(folderManager.getFolder().getFolder_name_ch());
        } else if (eventId == EventID.Inbox_Status_Normal) {
            onInboxStatusNormal();
        } else if (eventId == EventID.Inbox_Status_Select) {
            onInboxStatusSelect();
        } else if (eventId == EventID.Sync_Folder_UnRead_Count_Local_Over) {
            setLocalUnreadCount(folderManager.getFolder().getUnreadNum());
            return true;
        } else if (eventId == EventID.Clear_Folder_Mail) {
            setLocalUnreadCount(0);
        } else if (eventId == EventID.Inbox_Empty_View_Refresh) {
            noneEmail.setVisibility(message.arg1 > 0 ? View.GONE : View.VISIBLE);
            return true;
        } else if (eventId == EventID.Inbox_List_Select_Change) {
            onMailSelectCountChange(message.arg1, message.arg2);
            return true;
        }
        return false;
    }

    private <T extends View> T findViewById(@IdRes int id) {
        return findViewById(id, true);
    }

    private <T extends View> T findViewById(@IdRes int id, boolean onClick) {
        T v = mActivity.findViewById(id);
        if (onClick) {
            v.setOnClickListener(this);
        }
        return v;
    }


    private void initView() {
        headLayout = findViewById(R.id.inbox_head_layout, false);

        menuToggle = findViewById(R.id.img_left_title_inbox);
        tvTitle = findViewById(R.id.tv_center_title);
        tvCount = findViewById(R.id.tv_center_title_unread, false);
        searchMail = findViewById(R.id.inbox_mail_search);
        noneEmail = findViewById(R.id.inbox_empty_view_layout, false);

        composeMail = findViewById(R.id.compose_mail);
        selectFooter = findViewById(R.id.inbox_footer_select, false);
        selectHeader = findViewById(R.id.rl_title_inbox_edite_state, false);
        moreAction = findViewById(R.id.inbox_more_action);

        mailSelectInfo = findViewById(R.id.tv_count_title_mail, false);
        mailToggleSelect = findViewById(R.id.check_item_mail, false);
        mailToggleChoose = findViewById(R.id.checkbox_select_mail_title, false);
        mailToggleLayout = findViewById(R.id.mail_select_layout);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.img_left_title_inbox || id == R.id.tv_center_title) {
            mHandler.sendEmptyMessage(EventID.View_Toggle_Drawer);
        } else if (id == R.id.compose_mail) {
            Bundle bundle = new Bundle();
            bundle.putInt("type", Compose.TYPE_WRITE_MAIL);
            Launcher.startActivity(mActivity,ComposeActivity.class,bundle);
        } else if (id == R.id.inbox_mail_search) {

            Bundle bundle = new Bundle();
            bundle.putString("folder_name", folderManager.getFolderNameEn());
            Launcher.startActivity(mActivity,SearchActivity.class,bundle);
        } else if (id == R.id.inbox_more_action) {

        } else if (id == R.id.mail_select_layout) {
            toggleSelectAll();
        }
    }

    private void toggleSelectAll() {
        int eventId = mailToggleSelect.isChecked() ? EventID.Inbox_List_Select_None : EventID.Inbox_List_Select_All;
        mHandler.sendEvent(eventId);
    }

    public void setLocalUnreadCount(long count) {
        if (count == 0) {
            tvCount.setText("");
        } else {
            tvCount.setText("(" + count + ")");
        }
    }

    private void onMailSelectCountChange(int select, int total) {
        mailSelectInfo.setText("已经选择" + select + "封");
        if (select < total) {
            mailToggleSelect.setChecked(false);
            mailToggleChoose.setText("全选");
        } else {
            mailToggleSelect.setChecked(true);
            mailToggleChoose.setText("取消全选");
        }
    }

    private void onInboxStatusNormal() {
        menuToggle.setVisibility(View.VISIBLE);
        composeMail.setVisibility(View.VISIBLE);
        selectFooter.setVisibility(View.GONE);
        selectHeader.setVisibility(View.GONE);
    }

    private void onInboxStatusSelect() {
        menuToggle.setVisibility(View.INVISIBLE);
        composeMail.setVisibility(View.GONE);
        selectFooter.setVisibility(View.VISIBLE);
        selectHeader.setVisibility(View.VISIBLE);

    }
}
