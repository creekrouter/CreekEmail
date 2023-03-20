package com.creek.mail.compose.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creek.common.MailAttachment;
import com.creek.mail.compose.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class AttachmentView extends RecyclerView {
    public AttachmentView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AttachmentView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private AttachmentListAdapter attachAdapter;
    private List<MailAttachment> mAttachList = new ArrayList<>();

    private void init(Context context) {

        //创建LinearLayoutManager
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        //设置为横向滑动
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        //设置
        setLayoutManager(manager);

        attachAdapter = new AttachmentListAdapter(getContext(), mAttachList);
        //设置适配器
        setAdapter(attachAdapter);
        attachAdapter.setOnDeleteListenr(new AttachmentListAdapter.OnDeleteListener() {
            @Override
            public void delete() {
//                tvCount.setText(manager.getAttachList().size() + "");

            }
        });
        attachAdapter.setOnClickItemListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 查看附件
            }
        });
    }

    public List<MailAttachment> getAttachList() {
        return mAttachList;
    }

    public AttachmentListAdapter getAttachAdapter() {
        return attachAdapter;
    }
}
