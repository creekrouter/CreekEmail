package com.creek.mail.contact;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.creek.mail.R;
import com.creek.common.ContactEntity;
import com.creek.common.constant.Const;

import java.util.List;

class MessageContactAdapter extends BaseAdapter {


    private Context context;
    private List<ContactEntity> list;
    private final int MAX_TYPE = 2;
    private final int TYPE_EMAIL = 0;
    private final int TYPE_TITLE = 1;


    public MessageContactAdapter(Context context, List<ContactEntity> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getViewTypeCount() {
        return MAX_TYPE;
    }

    @Override
    public int getItemViewType(int position) {
        int type = list.get(position).getType();
        if (Const.MAIL_CC_TAG == type || Const.MAIL_TO_TAG == type || Const.MAIL_SEND_TAG == type) {
            return TYPE_TITLE;
        } else {
            return TYPE_EMAIL;
        }
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
        if (getItemViewType(position) == TYPE_TITLE) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_mail_contacts_title, null);
                viewHolder = new ViewHolder();
                viewHolder.tvTitle = convertView.findViewById(R.id.tv_tag_mail_contacts);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            switch (list.get(position).getType()) {
                case Const.MAIL_TO_TAG:
                    viewHolder.tvTitle.setText("收件人");
                    break;
                case Const.MAIL_CC_TAG:
                    viewHolder.tvTitle.setText("抄送人");
                    break;
                case Const.MAIL_SEND_TAG:
                    viewHolder.tvTitle.setText("发件人");
                    break;
            }
            return convertView;
        }


        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.convert_view_contact, null);
            viewHolder = new ViewHolder();
            viewHolder.tvName =  convertView.findViewById(R.id.item_mail_name);
            viewHolder.tvEmail =  convertView.findViewById(R.id.item_mail_adress);
            viewHolder.ivHasPersonInfo =  convertView.findViewById(R.id.contacts_arrow);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();

        String email = list.get(position).getEmail();
        viewHolder.tvEmail.setText(TextUtils.isEmpty(email) ? "" : email);

        String name = list.get(position).getName();
        viewHolder.tvName.setText(TextUtils.isEmpty(name) ? "" : name);
        viewHolder.ivHasPersonInfo.setVisibility(View.VISIBLE);

        return convertView;
    }


    private class ViewHolder {

        TextView tvTitle;
        TextView tvName;
        TextView tvEmail;
        ImageView ivHasPersonInfo;

    }


}
