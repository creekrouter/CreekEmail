package com.creek.mail.contact;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.creek.mail.R;
import com.creek.common.view.CircleIconView;

import java.util.ArrayList;
import java.util.List;

public class SortAdapter extends RecyclerView.Adapter<SortAdapter.ViewHolder> {

    private List<SortBean> list = new ArrayList<>();
    private Context mContext;

    public SortAdapter(Context context) {
        this.mContext = context;
    }

    public SortAdapter(Context context, List<SortBean> data) {
        this.mContext = context;
        this.list = data;
    }

    public void setNewData(List<SortBean> data) {
        list = data;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //绑定行布局
        View view = View.inflate(parent.getContext(), R.layout.itemview_sort, null);
        //实例化ViewHolder
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SortBean bean = list.get(position);
        holder.tvKey.setText(bean.getWord());
        if (position == 0 || !bean.getWord().equals(list.get(position - 1).getWord())) {
            holder.tvKey.setVisibility(View.VISIBLE);
        } else {
            holder.tvKey.setVisibility(View.GONE);
        }
        holder.header.setName(list.get(position).getName());
        holder.tvName.setText(list.get(position).getName());
        holder.tvEmail.setText(list.get(position).getAddress());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvKey;
        CircleIconView header;
        TextView tvName;
        TextView tvEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvKey = itemView.findViewById(R.id.tv_key);
            header = itemView.findViewById(R.id.header_icon);
            tvName = itemView.findViewById(R.id.tv_name);
            tvEmail = itemView.findViewById(R.id.tv_address);
        }
    }
}
