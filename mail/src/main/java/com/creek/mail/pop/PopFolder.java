package com.creek.mail.pop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.creek.common.MailFolder;
import com.creek.common.interfaces.ConfirmCallBack;
import com.creek.common.pop.PopItemAction;
import com.creek.mail.R;

import java.util.List;

public class PopFolder extends RelativeLayout implements PopItemAction.OnClickListener {
    public PopFolder(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    private ListView listView;
    private FolderMoveAdapter moveAdapter;
    private Context mContext;
    private ConfirmCallBack<String> mCallBack;
    private String folderName;

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.mail_move_folder_layout, this);
        listView = findViewById(R.id.mail_folder_list_view);

    }

    public void setData(List<MailFolder> folders, ConfirmCallBack<String> callBack) {
        mCallBack = callBack;
        moveAdapter = new FolderMoveAdapter(mContext, folders);
        listView.setAdapter(moveAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                folderName = moveAdapter.getItem(position).getFolder_name_en();
                moveAdapter.refresh(position);
            }
        });
    }


    @Override
    public void onClick() {
        if (folderName != null && mCallBack != null) {
            mCallBack.onConfirm(folderName);
        }
    }
}
