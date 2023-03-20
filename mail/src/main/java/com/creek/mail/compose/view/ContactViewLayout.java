package com.creek.mail.compose.view;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.creek.common.MailContact;
import com.creek.database.DBManager;
import com.creek.mail.R;
import com.creek.mail.compose.autocompleteview.ContactsCompletionView;
import com.creek.mail.compose.autocompleteview.TokenCompleteTextView;
import com.mail.tools.ToolInput;

import java.util.List;

public class ContactViewLayout extends LinearLayout {
    public ContactViewLayout(Context context) {
        super(context);
        initView(context);
    }

    public ContactViewLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }


    private TextView mTitle;
    private ImageView mAddContact;
    private ContactsCompletionView completionView;

    private List<MailContact> searchResults;
    private ArrayAdapter<MailContact> adapter;

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.compose_email_header_item, this);
        mTitle = rootView.findViewById(R.id.compose_header_item_name);
        mAddContact = rootView.findViewById(R.id.compose_header_item_add_contacts);
        completionView = rootView.findViewById(R.id.compose_header_item_input);

        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                completionView.requestFocus();
                ToolInput.show(context, completionView);
            }
        });

        initAdapter();

        completionView.setAddConnectPersonView(mAddContact);
        completionView.setDropDownAnchor(R.id.compose_header_divide_line);
        completionView.setAdapter(adapter);
        completionView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                mOptionSearch.optionSearch(s.toString());
            }
        });
    }

    private void initAdapter() {
        List<MailContact> contacts = DBManager.selectMailContactByKey("");

        if (contacts != null & contacts.size() > 0) {
            searchResults = contacts;
        }

        adapter = new FilterAdapter<MailContact>(getContext(), R.layout.person_layout, searchResults) {
            @Override
            protected boolean keepObject(MailContact contactEntity, String mask) {
                String name = contactEntity.getDisplay_name();
                String email = contactEntity.getEmail_addr();
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email)) {
                    return name.toLowerCase().contains(mask.toLowerCase()) || email.toLowerCase().contains(mask.toLowerCase());
                } else {
                    return false;
                }
            }


            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                    convertView = l.inflate(R.layout.person_layout, parent, false);
                }
                MailContact p = getItem(position);
                ((TextView) convertView.findViewById(R.id.name)).setText(p.getDisplay_name());
                ((TextView) convertView.findViewById(R.id.email)).setText(p.getEmail_addr());
                if (!TextUtils.isEmpty(p.getDepartment())) {
                    ((TextView) convertView.findViewById(R.id.department)).setText("(" + p.getDepartment() + ")");
                } else {
                    ((TextView) convertView.findViewById(R.id.department)).setText("");
                }
                return convertView;
            }

        };
    }

    public void setTitle(String text) {
        mTitle.setText(text);
    }

    public void insertContactViewData(MailContact person) {
        completionView.insertContactViewData(person);
    }

    public void insertContactViewData(List<MailContact> personList) {
        for (MailContact contact : personList) {
            completionView.insertContactViewData(contact);
        }
    }

    public List<MailContact> getContactList() {
        return completionView.getObjects();
    }

    public ContactsCompletionView getCompletionView() {
        return completionView;
    }

    public void setTokenListener(TokenCompleteTextView.TokenListener<MailContact> listener) {
        if (listener != null) {
            completionView.setTokenListener(listener);
        }

    }
}
