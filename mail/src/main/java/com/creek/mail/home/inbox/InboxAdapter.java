package com.creek.mail.home.inbox;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.creek.mail.R;
import com.creek.common.MailBean;
import com.creek.mail.home.status.InboxStatus;
import com.creek.mail.sync.MailPlain;
import com.mail.tools.ToolTimes;
import com.creek.common.view.CircleIconView;

import java.util.List;

public class InboxAdapter extends BaseAdapter {


    private Activity context;
    private List<MailBean> list;
    private MailPlain mailPlain;
    public InboxStatus status = InboxStatus.Normal;

    public InboxAdapter(Activity context, List<MailBean> list) {
        this.context = context;
        this.list = list;
        mailPlain = new MailPlain();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.convert_view_search_mail_item, null);
            viewHolder = new ViewHolder();
            viewHolder.rlCheckBox = convertView.findViewById(R.id.rl_inbox_adapter_selector);
            viewHolder.tvMailFrom = convertView.findViewById(R.id.tv_from_item_mail);
            viewHolder.tvSubject = convertView.findViewById(R.id.tv_subject_item_mail);
            viewHolder.tvContent = convertView.findViewById(R.id.tv_content_item_mail);
            viewHolder.tvTime = convertView.findViewById(R.id.tv_date_item_mail);
            viewHolder.tvCount = convertView.findViewById(R.id.tv_iscontainattch_item_mail);
            viewHolder.ivAttachments = convertView.findViewById(R.id.iv_iscontainattch_item_mail);
            viewHolder.ivSeen = convertView.findViewById(R.id.iv_seen);
            viewHolder.ivForward = convertView.findViewById(R.id.iv_forward_mail);
            viewHolder.ivAnswer = convertView.findViewById(R.id.iv_answer_mail);
            viewHolder.ivFlag = convertView.findViewById(R.id.iv_flag_item_mail);
            viewHolder.mCheckBox = convertView.findViewById(R.id.cb_item_mail);
            viewHolder.headerIcon = convertView.findViewById(R.id.email_item_header_icon);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();


        int count = list.get(position).getAttachmentsCount();
        if (count > 0) {
            viewHolder.ivAttachments.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivAttachments.setVisibility(View.INVISIBLE);
        }

        if (status == InboxStatus.Select) {
            viewHolder.rlCheckBox.setVisibility(View.VISIBLE);
        } else {
            viewHolder.rlCheckBox.setVisibility(View.GONE);
        }


        viewHolder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = ((CheckBox) v).isChecked();
                list.get(position).setSelected(isChecked);
                notifyDataSetChanged();
            }
        });

        viewHolder.mCheckBox.setChecked(list.get(position).isSelected());

        MailBean mailInfo = list.get(position);
        String headEmail = null;
        String headNickName = null;
        if ("Sent Items".equals(mailInfo.folderName())) { // 如果是已发送文件夹，展示收件人和抄送人信息
            if (!TextUtils.isEmpty(mailInfo.getCc_email())) {
                viewHolder.tvMailFrom.setText(mailInfo.getReceiver_email_display_name() + ";" + mailInfo.getCc_email_display_name());
            } else {
                viewHolder.tvMailFrom.setText(mailInfo.getReceiver_email_display_name());
            }
            headEmail = mailInfo.getReceiver_email() + ";" + mailInfo.getCc_email();
            headEmail = headEmail.split(";")[0];
            headNickName = viewHolder.tvMailFrom.getText().toString().split(";")[0];
        } else if ("Drafts".equals(mailInfo.folderName())) { // 如果是草稿箱文件夹
            StringBuilder strFrom = new StringBuilder();
            if (!TextUtils.isEmpty(mailInfo.getReceiver_email_display_name())) {
                strFrom.append(mailInfo.getReceiver_email_display_name());
            }
            if (!TextUtils.isEmpty(mailInfo.getCc_email())) {
                if (strFrom.length() > 0) {
                    strFrom.append(";");
                }
                strFrom.append(mailInfo.getCc_email_display_name());
            }
            if (strFrom.length() == 0) {
                strFrom.append("无收件人");
            } else {
                String emailNames = mailInfo.getReceiver_email() + ";" + mailInfo.getCc_email() + ";" + mailInfo.getBcc_email();
                String[] spNames = emailNames.split(";");
                if (spNames.length > 0) {
                    headEmail = spNames[0];
                }
                headNickName = strFrom.toString().split(";")[0];
            }
            viewHolder.tvMailFrom.setText(strFrom);
        } else {
            String dpName = list.get(position).getDisplayName();
            headEmail = mailInfo.getSend_email();
            if (dpName == null || dpName.length() == 0) {
                dpName = headEmail;
            }
            viewHolder.tvMailFrom.setText(dpName);
            headNickName = dpName;
        }
        final String finalHeadEmail = headEmail;
        final String finalHeadNick = headNickName;
        viewHolder.headerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        viewHolder.headerIcon.setName(viewHolder.tvMailFrom.getText().toString());
        if (TextUtils.isEmpty(list.get(position).getSubject())) {
            viewHolder.tvSubject.setText("无主题");
        } else {
            viewHolder.tvSubject.setText(list.get(position).getSubject());
        }

        viewHolder.tvContent.setText("");
        if (TextUtils.isEmpty(list.get(position).getPlainTxt())) {
            mailPlain.load(list.get(position), viewHolder.tvContent);
        } else {
            viewHolder.tvContent.setText(list.get(position).getPlainTxt());
        }

        String timeStr = ToolTimes.getMailTimeFormate(list.get(position).getSendTime());

        viewHolder.tvTime.setText(timeStr);

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
        TextView tvMailFrom;
        TextView tvSubject;
        TextView tvContent;
        TextView tvTime;
        ImageView ivAttachments;
        ImageView ivSeen, ivForward, ivAnswer;
        TextView tvCount;
        ImageView ivFlag;
        CheckBox mCheckBox;
        CircleIconView headerIcon;
        RelativeLayout rlCheckBox;
    }


}
