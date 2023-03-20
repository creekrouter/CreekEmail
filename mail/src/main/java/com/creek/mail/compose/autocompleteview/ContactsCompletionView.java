package com.creek.mail.compose.autocompleteview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;


import com.creek.mail.R;
import com.creek.common.UserInfo;
import com.creek.common.MailContact;

import java.util.List;

public class ContactsCompletionView extends TokenCompleteTextView<MailContact> {

    InputConnection testAccessibleInputConnection;

    public ContactsCompletionView(Context context) {
        super(context);
        initConfig();
    }

    public ContactsCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initConfig();
    }

    public ContactsCompletionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initConfig();
    }

    private void initConfig() {
        setThreshold(1);//该行作用：输入一个字符就开始匹配联想
        setDropDownWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setDropDownBackgroundDrawable(new ColorDrawable(Color.WHITE));
        setTokenClickStyle(TokenClickStyle.Select);
        //禁止重复添加
        allowDuplicates(false);

        //允许折叠
        allowCollapse(true);

        char[] splitChar = {',', ';', '，', ' ', ' '};
        setSplitChar(splitChar);
    }

    @Override
    protected View getViewForObject(MailContact person) {

        LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = l.inflate(R.layout.mail_contact_token, (ViewGroup) getParent(), false);
        TokenTextView token = view.findViewById(R.id.token_text_view);
        token.setTag(person);

//如果名字等于邮件地址，则手动填写的，否则搜索出来的
        if (person.getEmail_addr().equals(person.getDisplay_name())) {
            //根据邮件格式是否正确，设置字体颜色
            if (!isEmail(person.getEmail_addr())) {
                token.setTextColor(Color.RED);
            }
            token.setText(person.getEmail_addr());
        } else {
            token.setText(person.getDisplay_name());
        }

        return view;
    }

    public void insertContactViewData(MailContact person) {

        addObject(person);
    }


    private View addLinkMan;

    public void setAddConnectPersonView(View view) {
        addLinkMan = view;
    }

    @Override
    public void onFocusChanged(boolean hasFocus, int direction, Rect previous) {
        super.onFocusChanged(hasFocus, direction, previous);
        if (addLinkMan != null) {
            addLinkMan.setVisibility(hasFocus ? VISIBLE : GONE);
        }
    }

    public List<MailContact> getContactViewData(MailContact person) {

        return getObjects();
    }

    public void removeContactViewData(MailContact person) {

        removeObject(person);
    }

    @Override
    public boolean isTokenRemovable(MailContact person) {
        return super.isTokenRemovable(person);
    }

    //邮箱验证
    public static boolean isEmail(String strEmail) {
        String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        if (TextUtils.isEmpty(strPattern)) {
            return false;
        } else {
            return strEmail.matches(strPattern);
        }
    }

    /**
     * Description:用户完成搜索，输入符号“，”回调此事件
     * 提示：用户将键盘上的字体全部输入到控件上起作用
     *
     * @author jack
     * Created at 2018/7/27 12:35
     */
    @Override
    protected MailContact defaultObject(String completionText) {
        //Stupid simple example of guessing if we have an email or not
        int index = completionText.indexOf('@');

        if (index == -1) {

            return new MailContact(UserInfo.userEmail, completionText, completionText);
        } else {
            return new MailContact(UserInfo.userEmail, completionText, completionText);
        }
    }

    @Override
    public InputConnection onCreateInputConnection(@NonNull EditorInfo outAttrs) {
        testAccessibleInputConnection = super.onCreateInputConnection(outAttrs);
        return testAccessibleInputConnection;
    }
}
