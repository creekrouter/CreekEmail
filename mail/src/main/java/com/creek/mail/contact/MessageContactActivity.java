package com.creek.mail.contact;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import com.creek.mail.R;
import com.creek.common.BaseActivity;
import com.creek.common.ContactEntity;
import com.creek.router.annotation.CreekBean;

import java.util.ArrayList;

@CreekBean(path = "mail_activity_contacts_activity")
public class MessageContactActivity extends BaseActivity {

    private ListView listview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_contacts);


        findViewById(R.id.inbox_head_layout).setVisibility(View.GONE);
        findViewById(R.id.rl_title_contact).setVisibility(View.VISIBLE);
        findViewById(R.id.img_left_title_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listview = findViewById(R.id.lv_mail_contact);



        ArrayList<ContactEntity> contactEntities = (ArrayList<ContactEntity>) getIntent().getSerializableExtra("message");
        final MessageContactAdapter adapter = new MessageContactAdapter(mContext, contactEntities);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactEntity contactEntity = (ContactEntity) adapter.getItem(position);
                String email = contactEntity.getEmail();


            }
        });
    }

}
