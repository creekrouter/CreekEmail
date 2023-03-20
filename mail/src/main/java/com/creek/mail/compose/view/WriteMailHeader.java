package com.creek.mail.compose.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.creek.common.MailContact;
import com.creek.common.interfaces.CommonCallBack;
import com.creek.mail.R;
import com.creek.mail.compose.autocompleteview.ContactsCompletionView;
import com.creek.mail.compose.autocompleteview.TokenCompleteTextView;

import java.util.List;

public class WriteMailHeader extends LinearLayout implements TokenCompleteTextView.TokenListener<MailContact> {
    public WriteMailHeader(Context context) {
        super(context);
        initView(context);
    }

    public WriteMailHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private View mRootView, mSubjectLayout;
    private ContactViewLayout viewTo, viewCc, viewBcc;
    private EditText mSubject;
    private CommonCallBack<Void,String> mTokenCallBack;


    private void initView(Context context) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.compose_email_header, this);

        viewTo = mRootView.findViewById(R.id.compose_contact_view_mail_to);
        viewCc = mRootView.findViewById(R.id.compose_contact_view_mail_cc);
        viewBcc = mRootView.findViewById(R.id.compose_contact_view_mail_bcc);

        mSubject = mRootView.findViewById(R.id.compose_mail_subject);
        mSubjectLayout = mRootView.findViewById(R.id.compose_mail_subject_layout);

        viewTo.setTitle(context.getResources().getString(R.string.receiver));
        viewCc.setTitle(context.getResources().getString(R.string.copy_to));
        viewBcc.setTitle(context.getResources().getString(R.string.blind_carbon_copy));

        viewTo.setTokenListener(this);
        viewCc.setTokenListener(this);
        viewBcc.setTokenListener(this);

        viewTo.requestFocus();

        mSubjectLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSubject.requestFocus();
            }
        });
    }

    public void insertContactTo(MailContact person) {
        viewTo.insertContactViewData(person);
    }

    public void insertContactTo(List<MailContact> personList) {
        viewTo.insertContactViewData(personList);
    }

    public List<MailContact> getContactListTo() {
        return viewTo.getContactList();
    }

    public void insertContactCc(MailContact person) {
        viewCc.insertContactViewData(person);
    }

    public void insertContactCc(List<MailContact> personList) {
        viewCc.insertContactViewData(personList);
    }

    public List<MailContact> getContactListCc() {
        return viewCc.getContactList();
    }

    public void insertContactBcc(MailContact person) {
        viewBcc.insertContactViewData(person);
    }

    public void insertContactBcc(List<MailContact> personList) {
        viewBcc.insertContactViewData(personList);
    }

    public List<MailContact> getContactListBcc() {
        return viewBcc.getContactList();
    }

    public ContactsCompletionView getEditTextTo() {
        return viewTo.getCompletionView();
    }

    public ContactsCompletionView getEditTextCc() {
        return viewCc.getCompletionView();
    }

    public ContactsCompletionView getEditTextBcc() {
        return viewBcc.getCompletionView();
    }

    public EditText getSubject() {
        return mSubject;
    }

    public void setSubjectText(String text) {
        mSubject.setText(text);
    }

    public String getSubjectText() {
        return mSubject.getText().toString();
    }

    public void setTokenListener(CommonCallBack<Void,String> callBack) {
        mTokenCallBack = callBack;
    }

    @Override
    public void onTokenAdded(MailContact var1) {
        if (mTokenCallBack != null) {
            mTokenCallBack.success(null);
        }
    }

    @Override
    public void onTokenRemoved(MailContact var1) {
        if (mTokenCallBack != null) {
            mTokenCallBack.success(null);
        }
    }
}
