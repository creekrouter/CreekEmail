package com.creek.mail.details.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.creek.mail.R;
import com.creek.common.MailAttachment;
import com.creek.common.constant.Const;
import com.mail.tools.FileTool;

import java.util.List;


public class AttachAdapter extends RecyclerView.Adapter<AttachAdapter.ViewHolder> {
    private final Context mContext;
    private List<MailAttachment> mList;
    private OnItemClickListener mOnItemClickListener;
    private OnAttachActionListener mOnItemLongClickListener;

    public  interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public  interface OnAttachActionListener {
        void onAttachItemAction(View view, int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //绑定行布局
        View view = View.inflate(parent.getContext(), R.layout.attachment_layout, null);
        //实例化ViewHolder
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    public void setOnClickItemListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setOnAttachItemActionListener(OnAttachActionListener listener) {
        mOnItemLongClickListener = listener;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        //获取当前实体类对象
        MailAttachment attach = mList.get(position);
        String sizeStr = Formatter.formatFileSize(mContext, attach.getFile_size());
        holder.tvFileSize.setText(sizeStr);
        holder.tvFileName.setText(TextUtils.isEmpty(attach.getFile_name()) ? "未命名" : attach.getFile_name());

        switch (attach.getDownload_state()) {
            case Const.ATAACHMENTS_DOWNLOAD_STATE_DEFAULT:
                holder.ivDefault.setVisibility(View.VISIBLE);
                holder.ivFileType.setVisibility(View.GONE);
                holder.mProgressBar.setVisibility(View.GONE);
                break;
            case Const.ATAACHMENTS_DOWNLOAD_STATE_START:
                holder.ivDefault.setVisibility(View.GONE);
                holder.ivFileType.setVisibility(View.GONE);
                holder.mProgressBar.setVisibility(View.VISIBLE);
                break;
            case Const.ATAACHMENTS_DOWNLOAD_STATE_FIINSHED:
                holder.ivDefault.setVisibility(View.GONE);
                holder.ivFileType.setVisibility(View.VISIBLE);
                holder.mProgressBar.setVisibility(View.GONE);
                break;
        }

        if (attach.getDownload_state() == Const.ATAACHMENTS_DOWNLOAD_STATE_FIINSHED) {
            String fileName = attach.getFile_name();
            int resId = FileTool.getFileLogo(fileName);
            holder.ivFileType.setBackgroundResource(resId);
        }

        holder.rlParentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, position);
            }
        });
        holder.ivAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemLongClickListener.onAttachItemAction(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public List<MailAttachment> getList(){
        return mList;
    }
    //构造
    public AttachAdapter(Context context, List<MailAttachment> list) {
        mList = list;
        mContext = context;
    }

    //内部类
    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDefault, ivFileType,ivAction;
        ProgressBar mProgressBar;
        TextView tvFileName, tvFileSize;
        RelativeLayout rlParentView;

        /*RelativeLayout llItem;*/
        public ViewHolder(View itemView) {
            super(itemView);
            ivDefault =  itemView.findViewById(R.id.item_iv_default_email_attach);
            ivFileType =  itemView.findViewById(R.id.item_iv_email_attach_down_finnished);
            mProgressBar =  itemView.findViewById(R.id.mail_attach_progressbar);
            tvFileName =  itemView.findViewById(R.id.tv_file_name);
            tvFileSize =  itemView.findViewById(R.id.tv_file_size);
            rlParentView = itemView.findViewById(R.id.rl_mail_attachments_download);
            ivAction = itemView.findViewById(R.id.img_mail_detail_attach_action);
            /*  llItem=(RelativeLayout)itemView*/
        }
    }

}
