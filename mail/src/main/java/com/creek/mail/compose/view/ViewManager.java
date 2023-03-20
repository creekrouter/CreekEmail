package com.creek.mail.compose.view;

import android.app.Activity;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.IdRes;

import com.creek.common.MailAttachment;
import com.creek.common.interfaces.CommonCallBack;
import com.creek.mail.R;
import com.creek.mail.compose.manager.DataManager;
import com.creek.mail.compose.msg.ComposeHandler;
import com.creek.mail.compose.msg.EventID;
import com.creek.mail.compose.msg.EventWatcher;
import com.mail.tools.InputWatcher;

import java.util.List;

public class ViewManager implements EventWatcher, View.OnClickListener, View.OnFocusChangeListener {
    private Activity mActivity;

    private ComposeHandler mHandler;

    private WriteMailTitleBar mActionBar;
    private WriteMailHeader mHeader;

    private ComposeView mComposeEditor;
    private AttachmentLayout mAttachLayout;


    private DataManager mDataManager;

    public ViewManager(Activity activity, ComposeHandler handler, DataManager dataManager) {
        mActivity = activity;
        mHandler = handler;
        mDataManager = dataManager;
        mHandler.addRegister(this);
    }

    private <T extends View> T findViewById(@IdRes int id) {
        return mActivity.findViewById(id);
    }


    @Override
    public boolean onEventHappen(int eventId, Message message) {
        if (eventId == EventID.Compose_View_ActionBar_Title) {
            String title = (String) message.obj;
            mActionBar.setTitle(title);
            return true;
        } else if (eventId == EventID.Compose_View_ActionBar_Progress_Show) {
            mActionBar.setProgressShow();
            return true;
        } else if (eventId == EventID.Compose_View_ActionBar_Progress_hide) {
            mActionBar.setProgressHide();
            return true;
        } else if (eventId == EventID.Compose_Mail_Refresh_Header) {
            mActionBar.setTitle(mDataManager.mTitle);
            mHeader.setSubjectText(mDataManager.mSubjectText);
            mHeader.insertContactTo(mDataManager.toList);
            mHeader.insertContactCc(mDataManager.ccList);
            mHeader.insertContactBcc(mDataManager.bccList);
        } else if (eventId == EventID.Compose_Mail_Refresh_Content_Html) {
            mComposeEditor.setHtml(mDataManager.mContentHtml);
        } else if (eventId == EventID.Compose_Mail_Refresh_Attachment_List) {
            mAttachLayout.getAttachView().getAttachList().clear();
            mAttachLayout.getAttachView().getAttachList().addAll(mDataManager.mAttachmentList);
            mAttachLayout.getAttachView().getAttachAdapter().notifyDataSetChanged();
        } else if (eventId == EventID.Compose_Mail_Notify_Attachment_List) {
            mAttachLayout.getAttachView().getAttachAdapter().notifyDataSetChanged();
        } else if (eventId == EventID.Compose_Mail_Refresh_All_View) {
            refreshAll();
        }
        return false;
    }

    public void initView() {
        mActionBar = findViewById(R.id.compose_action_bar);
        mActionBar.setHandler(mHandler);
        mHeader = findViewById(R.id.compose_mail_header);
        mComposeEditor = findViewById(R.id.compose_email_rich_editor);
        mAttachLayout = findViewById(R.id.compose_email_attach_views_layout);
        mAttachLayout.setManager(mDataManager,mHandler);

        //监听键盘收起
        InputWatcher.setListener(mActivity, new InputWatcher.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                mAttachLayout.getAttachView().setVisibility(View.GONE);
            }

            @Override
            public void keyBoardHide(int height) {
                mAttachLayout.getAttachView().setVisibility(View.VISIBLE);
            }
        });

        mHeader.setTokenListener(new CommonCallBack<Void,String>() {
            @Override
            public void success(Void unused) {
                mDataManager.toList = mHeader.getContactListTo();
                mDataManager.ccList = mHeader.getContactListCc();
                mDataManager.bccList = mHeader.getContactListBcc();
            }

            @Override
            public void fail(String message) {

            }
        });

        mComposeEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                mDataManager.mContentHtml = mComposeEditor.getHtml();
                mDataManager.mSendHtml = mDataManager.mContentHtml;
            }
        });

        mHeader.getSubject().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mDataManager.mSubjectText = s.toString();

            }
        });
    }

    private void refreshAll() {
        mActionBar.setTitle(mDataManager.mTitle);
        mHeader.setSubjectText(mDataManager.mSubjectText);
        mHeader.insertContactTo(mDataManager.toList);
        mHeader.insertContactCc(mDataManager.ccList);
        mHeader.insertContactBcc(mDataManager.bccList);

        mComposeEditor.setHtml(mDataManager.mContentHtml);
        mComposeEditor.initWebViewSetting(mDataManager,mHandler);

        mAttachLayout.getAttachView().getAttachList().clear();
        mAttachLayout.getAttachView().getAttachList().addAll(mDataManager.mAttachmentList);
        mAttachLayout.getAttachView().getAttachAdapter().notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

    }

    public WriteMailHeader getHeader() {
        return mHeader;
    }

    public ComposeView getComposeEditor() {
        return mComposeEditor;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (mHeader.getSubject().hasFocus()
                || mHeader.getEditTextTo().hasFocus()
                || mHeader.getEditTextCc().hasFocus()
                || mHeader.getEditTextBcc().hasFocus()
                || mComposeEditor.hasFocus()) {
            mAttachLayout.getAttachView().setVisibility(View.GONE);
        }
    }

    public AttachmentView getAttachView() {
        return mAttachLayout.getAttachView();
    }

    public AttachmentListAdapter getAttachAdapter() {
        return mAttachLayout.getAttachView().getAttachAdapter();
    }

}
