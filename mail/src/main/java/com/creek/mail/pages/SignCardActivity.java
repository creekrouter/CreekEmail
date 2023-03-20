package com.creek.mail.pages;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.creek.common.BaseActivity;
import com.creek.mail.pop.Popup;
import com.creek.router.annotation.CreekBean;
import com.mail.tools.MailToast;
import com.creek.mail.R;
import com.mail.tools.MailSp;
import com.creek.common.constant.Const;

@CreekBean(path = "mail_activity_sing_card_activity")
public class SignCardActivity extends BaseActivity {

    private EditText signEditor;
    private Button btn_save;
    private String signature = "";
    private String originalSignature = "";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_card);
        signEditor = findViewById(R.id.sign_editor);
        btn_save = findViewById(R.id.btn_save);

        signature = MailSp.getString(Const.get_KEY_SIGNATURE(), "");
        originalSignature = signature;

        if (null != signature && !signature.equals("")) {
            signEditor.setText(signature);
        }


        findViewById(R.id.iv_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                signature = signEditor.getText().toString();
                if (originalSignature.length()==0){
                    if (null != signature && signature.length() != 0) {
                        MailSp.putString(Const.get_KEY_SIGNATURE(), signature);
                        MailToast.show("设置生效");
                        finish();
                    } else {
                        MailToast.show("您尚未编辑签名");
                    }
                }else {
                    if (!originalSignature.equals(signature)){
                        MailSp.putString(Const.get_KEY_SIGNATURE(), signature);
                        MailToast.show("设置生效");
                    }
                    finish();
                }
            }
        });
    }

    public void onBackPressed() {
        signature = signEditor.getText().toString();
        if (!originalSignature.equals(signature)) {
            Popup.saveSign(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MailSp.putString(Const.get_KEY_SIGNATURE(), signature);
                    MailToast.show("设置生效");
                    finish();
                }
            });

        } else {
            finish();
        }

    }
}
