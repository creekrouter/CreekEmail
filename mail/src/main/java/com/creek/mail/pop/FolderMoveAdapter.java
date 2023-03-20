package com.creek.mail.pop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.creek.mail.R;
import com.creek.common.MailFolder;

import java.util.List;

public class FolderMoveAdapter extends BaseAdapter {
    private Context context;
    private List<MailFolder> list;
    public int lastPos = -1;

    public FolderMoveAdapter(Context context, List<MailFolder> list) {
        this.context = context;
        this.list = list;
    }


    public void refresh(int currentPos) {
        if (currentPos == lastPos)
            return;
        if (lastPos>=0){
            list.get(lastPos).setSelected(false);
            list.get(currentPos).setSelected(true);
        }else {
            list.get(currentPos).setSelected(true);
        }
        lastPos = currentPos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public MailFolder getItem(int position) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_mail_move_new,null);
            viewHolder = new ViewHolder();
            viewHolder.text = convertView.findViewById(R.id.mail_move_tv_type);
            viewHolder.ivFolder = convertView.findViewById(R.id.mail_move_iv_type);
            viewHolder.ivChoose = convertView.findViewById(R.id.mail_choose);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.text.setText(list.get(position).getFolder_name_ch());

        // String folder=list.get(position).getFolder_name_ch();
        if (list.get(position).isSelected()) {
            viewHolder.ivChoose.setVisibility(View.VISIBLE);
        }else {
            viewHolder.ivChoose.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private class ViewHolder {
        TextView text;
        ImageView ivFolder;
        ImageView ivChoose;
    }
}
