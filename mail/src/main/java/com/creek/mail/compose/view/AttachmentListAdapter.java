package com.creek.mail.compose.view;

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
import com.creek.mail.compose.interfaces.OnItemClickListener;
import com.mail.tools.FileTool;

import java.util.List;


public class AttachmentListAdapter extends RecyclerView.Adapter<AttachmentListAdapter.ViewHolder> {
    private final Context mContext;
    List<MailAttachment> mList;
    private OnItemClickListener mOnItemClickListener;
    private OnDeleteListener mCountListener;

    public void setData(List<MailAttachment> attachList) {
        mList = attachList;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //绑定行布局
        View view = View.inflate(parent.getContext(), R.layout.mail_item_attach_write_mail, null);
        //实例化ViewHolder
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    public void setOnClickItemListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
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
                holder.ivDelete.setVisibility(View.GONE);
                break;
            case Const.ATAACHMENTS_DOWNLOAD_STATE_START:
                holder.ivDefault.setVisibility(View.GONE);
                holder.ivFileType.setVisibility(View.GONE);
                holder.mProgressBar.setVisibility(View.VISIBLE);
                holder.ivDelete.setVisibility(View.GONE);
                break;
            case Const.ATAACHMENTS_DOWNLOAD_STATE_FIINSHED:
                holder.ivDefault.setVisibility(View.GONE);
                holder.ivFileType.setVisibility(View.VISIBLE);
                holder.mProgressBar.setVisibility(View.GONE);
                holder.ivDelete.setVisibility(View.VISIBLE);
                break;
        }

        if (attach.getDownload_state() == Const.ATAACHMENTS_DOWNLOAD_STATE_FIINSHED) {
            String fileName = attach.getFile_name();
            int resId = FileTool.getFileLogo(fileName);
            holder.ivFileType.setBackgroundResource(resId);
          /*  //名字为空时
            if (TextUtils.isEmpty(attach.getFile_name())) {
                holder.ivFileType.setBackgroundResource(R.drawable.email_file_attach);
                return;
            }
            if (MediaFile.isImageFile(attach.getFile_name())) {//附件为图片

                holder.ivFileType.setBackgroundResource(R.drawable.email_image_attach);

            } else {

                holder.ivFileType.setBackgroundResource(R.drawable.email_file_attach);
            }*/
        }

        holder.rlParentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, position);
            }
        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.remove(position);
                notifyDataSetChanged();
                mCountListener.delete();
            }
        });
    }


    public void setOnDeleteListenr(OnDeleteListener listener) {
        mCountListener = listener;
    }

    public interface OnDeleteListener {
        void delete();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    //构造
    public AttachmentListAdapter(Context context, List<MailAttachment> list) {
        mList = list;
        mContext = context;
    }

    //内部类
    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDefault, ivFileType, ivDelete;
        ProgressBar mProgressBar;
        TextView tvFileName, tvFileSize;
        RelativeLayout rlParentView;


        /*RelativeLayout llItem;*/
        public ViewHolder(View itemView) {
            super(itemView);
            ivDefault = (ImageView) itemView.findViewById(R.id.item_iv_default_email_attach);
            ivFileType = (ImageView) itemView.findViewById(R.id.item_iv_email_attach_down_finnished);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.mail_attach_progressbar);
            tvFileName = (TextView) itemView.findViewById(R.id.tv_file_name);
            tvFileSize = (TextView) itemView.findViewById(R.id.tv_file_size);
            rlParentView = (RelativeLayout) itemView.findViewById(R.id.rl_mail_attachments_download);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete_attch_mail_detail);
            /*  llItem=(RelativeLayout)itemView*/
        }
    }

    public void notifity(List<MailAttachment> mailAttachments) {
        this.mList = mailAttachments;
        notifyDataSetChanged();

    }
}