package com.creek.mail.home.adapter;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.List;

import static com.libmailcore.IMAPFolderFlags.IMAPFolderFlagDrafts;
import static com.libmailcore.IMAPFolderFlags.IMAPFolderFlagFlagged;
import static com.libmailcore.IMAPFolderFlags.IMAPFolderFlagInbox;
import static com.libmailcore.IMAPFolderFlags.IMAPFolderFlagJunk;
import static com.libmailcore.IMAPFolderFlags.IMAPFolderFlagNone;
import static com.libmailcore.IMAPFolderFlags.IMAPFolderFlagSentMail;
import static com.libmailcore.IMAPFolderFlags.IMAPFolderFlagTrash;

import com.mail.tools.ToolDip;
import com.creek.mail.R;
import com.creek.common.MailFolder;


public class FolderAdapter extends BaseAdapter {
    private Context context;
    private List<MailFolder> list;

    public FolderAdapter(Context context, List<MailFolder> list) {
        this.context = context;
        this.list = list;
    }


    public void refresh(List<MailFolder> list) {
        if (this.list != list) {
            this.list = list;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_mail_menu_adpter, null);
            viewHolder = new ViewHolder();
            viewHolder.text = convertView.findViewById(R.id.tv_drawer_inbox);
            viewHolder.no_read_num = convertView.findViewById(R.id.tv_drawer_inbox_count);
            viewHolder.ivBackground = convertView.findViewById(R.id.iv_drawer_inbox);
            viewHolder.ll = convertView.findViewById(R.id.rl_drawer_inbox_item);
            viewHolder.bottomLine = convertView.findViewById(R.id.line0_fragment_mail);

            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.text.setText(list.get(position).getFolder_name_ch());

        long no_read_num = list.get(position).getUnreadNum();

        viewHolder.no_read_num.setText(no_read_num == 0 ? "" : String.valueOf(no_read_num));

        int flag = list.get(position).getFolder_flag();
        boolean selecte = list.get(position).isSelected();


        setBackground(viewHolder, flag, selecte);


        return convertView;
    }

    private void setBackground(ViewHolder viewHolder, int flag, boolean selecte) {
        if (flag == -1) {
            viewHolder.ivBackground.setVisibility(View.GONE);
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            viewHolder.text.setTextColor(Color.parseColor("#888888"));
            viewHolder.bottomLine.setVisibility(View.GONE);
            return;
        } else {
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            viewHolder.text.setTextColor(Color.parseColor("#333333"));
            viewHolder.ivBackground.setVisibility(View.VISIBLE);
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.ll.getLayoutParams();
        if (selecte) {
            viewHolder.ll.setBackgroundResource(R.drawable.mail_folder_list_item_bg);
            params.setMargins(ToolDip.dip2px(16), ToolDip.dip2px(3), ToolDip.dip2px(3), ToolDip.dip2px(5));
            viewHolder.no_read_num.setTextColor(ContextCompat.getColor(context, R.color.white));
            viewHolder.text.setTextColor(ContextCompat.getColor(context, R.color.white));
            viewHolder.bottomLine.setVisibility(View.GONE);
        } else {
            viewHolder.ll.setBackgroundResource(0);
            params.setMargins(0, 0, 0, 0);
            viewHolder.no_read_num.setTextColor(ContextCompat.getColor(context, R.color.text_tv_normal));
            viewHolder.text.setTextColor(ContextCompat.getColor(context, R.color.text_tv_normal));
            viewHolder.bottomLine.setVisibility(View.VISIBLE);
        }
        viewHolder.ll.setLayoutParams(params);
        if ((flag & IMAPFolderFlagInbox) > 0) {
            if (!selecte) {
                viewHolder.ivBackground.setImageResource(R.drawable.mail_color_inbox);
            } else {
                viewHolder.ivBackground.setImageResource(R.drawable.mail_color_inbox_select);
            }
        } else if ((flag & IMAPFolderFlagDrafts) > 0) {
            if (!selecte) {
                viewHolder.ivBackground.setImageResource(R.drawable.icon_vision_draft);
            } else {
                viewHolder.ivBackground.setImageResource(R.drawable.mail_color_draftbox_select);
            }
        } else if ((flag & IMAPFolderFlagSentMail) > 0) {
            if (!selecte) {
                viewHolder.ivBackground.setImageResource(R.drawable.mail_color_sent);
            } else {
                viewHolder.ivBackground.setImageResource(R.drawable.mail_color_sent_select);
            }
        } else if ((flag & IMAPFolderFlagTrash) > 0) {
            if (!selecte) {
                viewHolder.ivBackground.setImageResource(R.drawable.mail_color_delbox);
            } else {
                viewHolder.ivBackground.setImageResource(R.drawable.mail_color_delbox_select);
            }
        } else if ((flag & IMAPFolderFlagJunk) > 0) {
            if (!selecte) {
                viewHolder.ivBackground.setImageResource(R.drawable.mail_color_rubbish);
            } else {
                viewHolder.ivBackground.setImageResource(R.drawable.mail_color_rubbish_select);
            }
        } else if ((flag & IMAPFolderFlagFlagged) > 0) {
            if (!selecte) {
                viewHolder.ivBackground.setImageResource(R.drawable.mail_redemail_drawer);
            } else {
                viewHolder.ivBackground.setImageResource(R.drawable.mail_redemail_drawer_select);
            }
        } else if (flag == IMAPFolderFlagNone) {
            if (!selecte) {
                viewHolder.ivBackground.setImageResource(R.drawable.mail_color_normal);
            } else {
                viewHolder.ivBackground.setImageResource(R.drawable.mail_color_normal_select);
            }
        }
    }

    private class ViewHolder {
        TextView text;
        TextView no_read_num;
        ImageView ivBackground;
        RelativeLayout ll;
        View bottomLine;
    }
}
