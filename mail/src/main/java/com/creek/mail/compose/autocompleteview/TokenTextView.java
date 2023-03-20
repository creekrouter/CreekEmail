package com.creek.mail.compose.autocompleteview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

import com.creek.mail.R;
import com.creek.common.MailContact;


public class TokenTextView extends TextView {

    public TokenTextView(Context context) {
        super(context);
    }

    public TokenTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);

        MailContact contact = (MailContact) getTag();

        if (contact.getEmail_addr().equals(contact.getDisplay_name())) {

            String text = getText().toString();
            Boolean mailFormate = ContactsCompletionView.isEmail(text);
            //根据邮件格式 设置背景颜色和字体颜色
            setNewBgByFormate(selected, mailFormate);
        } else {
            setNewBgByFormate(selected, true);
        }
        /*  setCompoundDrawablesWithIntrinsicBounds(0, 0, selected ? R.drawable.token_background : 0, 0);*/
    }

    private void setSelectedBackGroundByFormate(boolean selected, Boolean mailFormate) {
        if (mailFormate) {
            if (selected) {
                setBackgroundColor(Color.parseColor("#3370FF"));
                setTextColor(Color.parseColor("#ffffff"));
            } else {
                setBackgroundColor(Color.parseColor("#ffffff"));
                setTextColor(Color.parseColor("#EFF0F1"));
            }
        } else {
            if (selected) {
                setBackgroundColor(Color.RED);
                setTextColor(Color.parseColor("#ffffff"));
            } else {
                setBackgroundColor(Color.parseColor("#ffffff"));
                setTextColor(Color.RED);
            }
        }
    }

    private void setNewBgByFormate(boolean selected, Boolean mailFormate) {
        if (mailFormate) {
            if (selected) {
                setBackgroundResource(R.drawable.mail_token_textview_bg2);
                setTextColor(Color.parseColor("#ffffff"));
            } else {
                setBackgroundResource(R.drawable.mail_token_textview_bg);
                setTextColor(Color.BLACK);
            }
        } else {
            if (selected) {
                setBackgroundResource(R.drawable.mail_token_textview_bg3);
                setTextColor(Color.WHITE);
            } else {
                setBackgroundResource(R.drawable.mail_token_textview_bg);
                setTextColor(Color.RED);
            }
        }
    }
}
