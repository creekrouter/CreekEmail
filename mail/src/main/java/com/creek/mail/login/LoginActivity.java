package com.creek.mail.login;

import static com.creek.common.constant.Const.SP_USER_EMAIL;
import static com.creek.common.constant.Const.SP_USER_PWD;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.creek.common.BaseActivity;
import com.creek.common.CreekPath;
import com.creek.common.UserInfo;
import com.creek.mail.R;
import com.creek.mail.home.HomePageActivity;
import com.mail.tools.InputWatcher;
import com.creek.router.annotation.CreekBean;
import com.mail.tools.MailSp;
import com.mail.tools.MailToast;
import com.mail.tools.ToolDip;

@CreekBean(path = CreekPath.Mail_Page_Login_Activity)
public class LoginActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener, TextWatcher {


    private EditText mEtLoginUsername;
    private EditText mEtLoginPwd;
    private LinearLayout mLlLoginUsername;
    private ImageView mIvLoginUsernameDel;
    private Button mBtLoginSubmit;
    private LinearLayout mLlLoginPwd;
    private ImageView mIvLoginPwdDel;
    private ImageView mIvLoginLogo;
    private TextView mTvLoginForgetPwd;
    private Button mBtLoginRegister;


    private int mLogoHeight;
    private int mLogoWidth;
    private AnimatorHandler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();

        String address = MailSp.getString(SP_USER_EMAIL, "");
        String session = MailSp.getString(SP_USER_PWD, "");

        if (address.length() > 0 && session.length() > 0) {
            UserInfo.setUserMail(address.trim());
            UserInfo.setSessionId(session.trim());
            startActivity(mContext, HomePageActivity.class);
            finish();
        }

    }

    //初始化视图
    private void initView() {
        //登录层、下拉层、其它登录方式层


        //logo
        mIvLoginLogo = findViewById(R.id.iv_login_logo);

        //username
        mLlLoginUsername = findViewById(R.id.ll_login_username);
        mEtLoginUsername = findViewById(R.id.et_login_username);
        mIvLoginUsernameDel = findViewById(R.id.iv_login_username_del);

        //passwd
        mLlLoginPwd = findViewById(R.id.ll_login_pwd);
        mEtLoginPwd = findViewById(R.id.et_login_pwd);
        mIvLoginPwdDel = findViewById(R.id.iv_login_pwd_del);

        //提交、注册
        mBtLoginSubmit = findViewById(R.id.bt_login_submit);
        mBtLoginRegister = findViewById(R.id.bt_login_register);

        //忘记密码
        mTvLoginForgetPwd = findViewById(R.id.tv_login_forget_pwd);
        mTvLoginForgetPwd.setOnClickListener(this);

        //注册点击事件

        mEtLoginUsername.setOnClickListener(this);
        mIvLoginUsernameDel.setOnClickListener(this);
        mBtLoginSubmit.setOnClickListener(this);
        mBtLoginRegister.setOnClickListener(this);
        mEtLoginPwd.setOnClickListener(this);
        mIvLoginPwdDel.setOnClickListener(this);

        //注册其它事件
        mEtLoginUsername.setOnFocusChangeListener(this);
        mEtLoginUsername.addTextChangedListener(this);
        mEtLoginPwd.setOnFocusChangeListener(this);
        mEtLoginPwd.addTextChangedListener(this);

        mLogoHeight = ToolDip.dip2px(160);
        mLogoWidth = ToolDip.dip2px(160);
        mHandler = new AnimatorHandler(mLogoHeight, mLogoWidth, mIvLoginLogo);

        InputWatcher.setListener(mContext, new InputWatcher.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int h) {
                mHandler.keyBoardShow();
            }

            @Override
            public void keyBoardHide(int h) {
                mHandler.keyBoardHide();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.et_login_username) {
            mEtLoginPwd.clearFocus();
            mEtLoginUsername.setFocusableInTouchMode(true);
            mEtLoginUsername.requestFocus();
        } else if (id == R.id.et_login_pwd) {
            mEtLoginUsername.clearFocus();
            mEtLoginPwd.setFocusableInTouchMode(true);
            mEtLoginPwd.requestFocus();
        } else if (id == R.id.iv_login_username_del) {//清空用户名
            mEtLoginUsername.setText(null);
        } else if (id == R.id.iv_login_pwd_del) {//清空密码
            mEtLoginPwd.setText(null);
        } else if (id == R.id.bt_login_submit) {//登录
            loginRequest();
        }
    }

    //用户名密码焦点改变
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();

        if (id == R.id.et_login_username) {
            if (hasFocus) {
                mLlLoginUsername.setActivated(true);
                mLlLoginPwd.setActivated(false);
            }
        } else {
            if (hasFocus) {
                mLlLoginPwd.setActivated(true);
                mLlLoginUsername.setActivated(false);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setStatusBarColor(Color.BLACK, 0);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    //用户名密码输入事件
    @Override
    public void afterTextChanged(Editable s) {
        String username = mEtLoginUsername.getText().toString().trim();
        String pwd = mEtLoginPwd.getText().toString().trim();

        //是否显示清除按钮
        if (username.length() > 0) {
            mIvLoginUsernameDel.setVisibility(View.VISIBLE);
        } else {
            mIvLoginUsernameDel.setVisibility(View.INVISIBLE);
        }
        if (pwd.length() > 0) {
            mIvLoginPwdDel.setVisibility(View.VISIBLE);
        } else {
            mIvLoginPwdDel.setVisibility(View.INVISIBLE);
        }

        //登录按钮是否可用
        if (!TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(username)) {
            mBtLoginSubmit.setBackgroundResource(R.drawable.bg_login_submit);
            mBtLoginSubmit.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            mBtLoginSubmit.setBackgroundResource(R.drawable.bg_login_submit_lock);
            mBtLoginSubmit.setTextColor(mContext.getResources().getColor(R.color.account_lock_font_color));
        }
    }

    //登录
    private void loginRequest() {

        String address = mEtLoginUsername.getText().toString();
        String session = mEtLoginPwd.getText().toString();

        if (address == null || session == null || address.length() == 0 || session.length() == 0) {
            MailToast.show("session 地址 不能为空");
        }

        UserInfo.setUserMail(address.trim());
        UserInfo.setSessionId(session.trim());

        startActivity(mContext, HomePageActivity.class);
    }

}
