package com.creek.mail.search;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.creek.mail.R;
import com.creek.common.MailBean;
import com.creek.mail.sync.MailPlain;
import com.mail.tools.ToolTimes;
import com.creek.common.view.CircleIconView;

import java.util.ArrayList;
import java.util.List;


public class SearchAdapter extends BaseAdapter {

    private Activity context;
    private List<MailBean> list = new ArrayList<>();
    private MailPlain mailPlain;
    private String keyword = "";


    public SearchAdapter(Activity context) {
        this.context = context;
        mailPlain = new MailPlain();
    }

    public List<MailBean> getMailDataList() {
        return list;
    }


    public void setKeyword(String keyword) {
        if (keyword != null) {
            this.keyword = keyword;
        }
    }


    @Override
    public int getCount() {
        return list.size();
    }


    @Override
    public MailBean getItem(int position) {
        if (position < list.size()) {
            return list.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.convert_view_search_mail_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tvMailForm = convertView.findViewById(R.id.tv_from_item_mail);
            viewHolder.tvSubject = convertView.findViewById(R.id.tv_subject_item_mail);
            viewHolder.tvContent = convertView.findViewById(R.id.tv_content_item_mail);
            viewHolder.tvTime = convertView.findViewById(R.id.tv_date_item_mail);
            viewHolder.tvCount = convertView.findViewById(R.id.tv_iscontainattch_item_mail);
            viewHolder.ivAttachments = convertView.findViewById(R.id.iv_iscontainattch_item_mail);
            viewHolder.ivSeen = convertView.findViewById(R.id.iv_seen);
            viewHolder.ivForward = convertView.findViewById(R.id.iv_forward_mail);
            viewHolder.ivAnswer = convertView.findViewById(R.id.iv_answer_mail);
            viewHolder.ivFlag = convertView.findViewById(R.id.iv_flag_item_mail);
            viewHolder.headerIcon = convertView.findViewById(R.id.email_item_header_icon);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();


        MailBean mailInfo = list.get(position);

        int count = mailInfo.getAttachmentsCount();
        if (count > 0) {
            viewHolder.ivAttachments.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivAttachments.setVisibility(View.INVISIBLE);
        }

        String dpName = mailInfo.getDisplayName();
        if (dpName == null || dpName.length() == 0) {
            dpName = mailInfo.getSend_email();
        }
        viewHolder.tvMailForm.setText(SearchKeyText.getKeyPart(keyword, dpName));
        viewHolder.headerIcon.setName(viewHolder.tvMailForm.getText().toString());

        if (TextUtils.isEmpty(mailInfo.getSubject())) {
            viewHolder.tvSubject.setText("无主题");
        } else {

            viewHolder.tvSubject.setText(SearchKeyText.getKeyPart(keyword, mailInfo.getSubject()));
        }

        if (TextUtils.isEmpty(mailInfo.getPlainTxt())) {
            mailPlain.load(mailInfo, viewHolder.tvContent);
        } else {
            viewHolder.tvContent.setText(mailInfo.getPlainTxt());
        }

        String timeStr = ToolTimes.getMailTimeFormate(mailInfo.getSendTime());
        viewHolder.tvTime.setText(timeStr);

        viewHolder.headerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        if (mailInfo.isAnsweredMail()) {
            viewHolder.ivAnswer.setVisibility(View.VISIBLE);
            viewHolder.ivSeen.setVisibility(View.GONE);
            viewHolder.ivForward.setVisibility(View.GONE);
        } else if (mailInfo.isForwardMail()) {
            viewHolder.ivAnswer.setVisibility(View.GONE);
            viewHolder.ivSeen.setVisibility(View.GONE);
            viewHolder.ivForward.setVisibility(View.VISIBLE);
        } else if (!mailInfo.isSeenMail()) {
            viewHolder.ivAnswer.setVisibility(View.GONE);
            viewHolder.ivSeen.setVisibility(View.VISIBLE);
            viewHolder.ivForward.setVisibility(View.GONE);
        } else {
            viewHolder.ivAnswer.setVisibility(View.GONE);
            viewHolder.ivSeen.setVisibility(View.GONE);
            viewHolder.ivForward.setVisibility(View.GONE);
        }
        viewHolder.ivFlag.setVisibility(mailInfo.isRedFlagMail() ? View.VISIBLE : View.GONE);

        return convertView;
    }


    private class ViewHolder {
        TextView tvMailForm;
        TextView tvSubject;
        TextView tvContent;
        TextView tvTime;
        ImageView ivAttachments;
        ImageView ivSeen, ivForward, ivAnswer;
        TextView tvCount;
        ImageView ivFlag;
        CircleIconView headerIcon;
    }
}
