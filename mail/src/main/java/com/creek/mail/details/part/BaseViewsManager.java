package com.creek.mail.details.part;

import static com.creek.mail.details.DetailsPageActivity.OTHER_ACTIVITY;

import android.app.Activity;
import android.content.Intent;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import com.creek.common.EmailData;
import com.creek.mail.contact.MessageContactActivity;
import com.creek.mail.details.msg.MsgHandler;
import com.creek.mail.details.msg.EvenId;
import com.libmailcore.MessageFlag;

import com.creek.mail.R;
import com.creek.mail.details.adapter.AttachAdapter;
import com.creek.common.ContactEntity;
import com.creek.common.MailBean;
import com.creek.common.MailAttachment;
import com.mail.tools.ToolTimes;
import com.creek.common.view.CircleIconView;
import com.creek.mail.details.MyScrollView;

import java.util.ArrayList;
import java.util.List;

public class BaseViewsManager implements View.OnClickListener {

    public ImageView preEmail, nextEmail;
    public View backArrow, attachmentIcon, redFlagIcon, seenPoint, forwardIcon, answeredIcon;
    public MyScrollView myScrollView;
    public View moreAction, quickReply;
    public TextView mailSubject, mailTime, senderName, sendTo, downLoadAllAtt;
    public CircleIconView headerIcon;
    public View forwardMail;//转发邮件

    private RecyclerView mRecycleView;
    protected AttachAdapter myAdapter;

    RelativeLayout rrTitle2Detail;
    LinearLayout rlTitle2Detail;
    LinearLayout llTopDetails;

    int rrTitle2DetailTop;

    protected Activity mActivity;
    protected EmailData data;
    protected MsgHandler mHandler;

    public BaseViewsManager(Activity contextActivity, MsgHandler handler,EmailData emailData) {
        mActivity = contextActivity;
        mHandler = handler;
        data = emailData;
    }

    protected void initViews() {
        preEmail = mActivity.findViewById(R.id.pre_details);
        nextEmail = mActivity.findViewById(R.id.next_details);
        preEmail.setOnClickListener(this);
        nextEmail.setOnClickListener(this);
        initEmailPreAndNext();

        backArrow = mActivity.findViewById(R.id.rl_back_mail_detail);
        backArrow.setOnClickListener(this);

        myScrollView = mActivity.findViewById(R.id.msv_details);

        attachmentIcon = mActivity.findViewById(R.id.iv_iscontainattch_details);
        mActivity.findViewById(R.id.mail_info_flag_attach).setOnClickListener(this);//附件按钮点击区域
        mActivity.findViewById(R.id.click_area_attach1).setOnClickListener(this);//附件按钮点击区域
        mActivity.findViewById(R.id.click_area_attach2).setOnClickListener(this);//附件按钮点击区域


        mActivity.findViewById(R.id.second_info_line).setOnClickListener(this);//邮件联系人详情点击区域
        mActivity.findViewById(R.id.first_info_line_left).setOnClickListener(this);//邮件联系人详情点击区域
        mActivity.findViewById(R.id.click_area_detail_1).setOnClickListener(this);//邮件联系人详情点击区域
        mActivity.findViewById(R.id.click_area_detail_2).setOnClickListener(this);//邮件联系人详情点击区域

        mailSubject = mActivity.findViewById(R.id.tv_subject_details);

        moreAction = mActivity.findViewById(R.id.iv_email_more);
        moreAction.setOnClickListener(this);

        quickReply = mActivity.findViewById(R.id.tv_reply_content);
        quickReply.setOnClickListener(this);

        headerIcon = mActivity.findViewById(R.id.email_detail_header_icon);
        headerIcon.setOnClickListener(this);
        mailTime = mActivity.findViewById(R.id.date_details);
        senderName = mActivity.findViewById(R.id.from_details);

        redFlagIcon = mActivity.findViewById(R.id.iv_flag_item_mail);
        seenPoint = mActivity.findViewById(R.id.iv_seen);
        forwardIcon = mActivity.findViewById(R.id.iv_forward_mail);
        answeredIcon = mActivity.findViewById(R.id.iv_answer_mail);

        rrTitle2Detail = mActivity.findViewById(R.id.rr_title2_detail);
        rlTitle2Detail = mActivity.findViewById(R.id.rl_title2_detail);
        llTopDetails = mActivity.findViewById(R.id.ll_top_details);

        sendTo = mActivity.findViewById(R.id.to_details);
        forwardMail = mActivity.findViewById(R.id.btn_forward_details);
        forwardMail.setOnClickListener(this);
        downLoadAllAtt = mActivity.findViewById(R.id.acty_mail_detail);
        downLoadAllAtt.setOnClickListener(this);

        mRecycleView = mActivity.findViewById(R.id.recycleview_email_attach);
        initRecycleView();

        setOnScrollListener();
    }

    private void initRecycleView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 2);
        mRecycleView.setLayoutManager(gridLayoutManager);
        List<MailAttachment> attList = new ArrayList<>();
        myAdapter = new AttachAdapter(mActivity, attList);
        mRecycleView.setAdapter(myAdapter);
        myAdapter.setOnClickItemListener(new AttachAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MailAttachment attachment = myAdapter.getList().get(position);
                if (attachment.isDownLoadFinished()) {
                    mHandler.sendEvent(EvenId.VIEW_ATTACHMENT, position);
                } else {
                    mHandler.sendEvent(EvenId.DOWNLOAD_SINGLE_ATTACHMENT, position);
                }
            }
        });
    }

    protected void resetListData() {
        List<MailAttachment> list = data.getAttachmentList();
        myAdapter.getList().clear();
        if (list != null && list.size() > 0) {
            downLoadAllAtt.setVisibility(View.VISIBLE);
            downLoadAllAtt.setText("全部附件（" + list.size() + "个)下载全部");
            myAdapter.getList().addAll(list);
        }
        myAdapter.notifyDataSetChanged();
        resetHeight(myAdapter.getList().size());
    }

    private void initEmailPreAndNext() {
        if (getPreMinusPos(data) < 0) {
            setPreEmailUnClick();
        }
        if (getNextAddPos(data) < 0) {
            setNextEmailUnClick();
        }
    }

    private void resetHeight(int listSize) {
        float dpValue = 136 * (listSize / 2 + listSize % 2);
        final float scale = mActivity.getResources().getDisplayMetrics().density;
        ViewGroup.LayoutParams lp = mRecycleView.getLayoutParams();
        lp.height = (int) (dpValue * scale + 0.5f);
        mRecycleView.setLayoutParams(lp);
    }

    public void setPreEmailUnClick() {
        preEmail.setEnabled(false);
        preEmail.setImageResource(R.drawable.new_top_pre_icon_disable);
    }

    public void setNextEmailUnClick() {
        nextEmail.setEnabled(false);
        nextEmail.setImageResource(R.drawable.new_top_next_disable);
    }

    public void setMailSubject(String subject) {
        if (mailSubject == null) {
            return;
        }
        if (TextUtils.isEmpty(subject)) {
            mailSubject.setText("无主题");
        } else {
            mailSubject.setText(subject);
        }
    }

    public void setSenderName(String name) {
        if (name != null) {
            senderName.setText(name.replace("\"", "").trim());
        }
    }

    public void setEmailTime(String time) {
        mailTime.setText(time);
    }

    public void setEmailTime(long time) {
        String mailTime = ToolTimes.getTime(time, "yyyy-MM-dd HH:mm");
        setEmailTime(mailTime);
    }

    public void setHeaderText(String text) {
        headerIcon.setName(text);
    }

    public void setHeaderText(MailBean mail) {
        if (mail.getDisplayName() == null || mail.getDisplayName().length() == 0) {
            setHeaderText(mail.getSend_email());
        } else {
            setHeaderText(mail.getDisplayName());
        }
    }

    public void setAttachmentIconHide(int attachmentSize) {
        if (attachmentSize > 0) {
            attachmentIcon.setVisibility(View.VISIBLE);
        } else {
            attachmentIcon.setVisibility(View.GONE);
        }
    }

    public void setRedFlagIconHide(int emailFlag) {
        if ((emailFlag & MessageFlag.MessageFlagFlagged) == MessageFlag.MessageFlagFlagged) {
            redFlagIcon.setVisibility(View.VISIBLE);
        } else {
            redFlagIcon.setVisibility(View.GONE);
        }
    }

    public void setSentToPeople(MailBean mail) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("发至 ");
        String receiverName = "";
        if (TextUtils.isEmpty(mail.getCc_email_display_name())) {
            receiverName = mail.getReceiver_email_display_name();
            buffer.append(TextUtils.isEmpty(receiverName) ? "" : receiverName);
        } else {
            receiverName = mail.getReceiver_email_display_name() + "," + mail.getCc_email_display_name();
            buffer.append(TextUtils.isEmpty(receiverName) ? "" : receiverName);
        }
        sendTo.setText(buffer);
    }

    protected void onWindowFocus() {
        rrTitle2DetailTop = rrTitle2Detail.getTop();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_back_mail_detail) {
            mActivity.finish();
        } else if (id == R.id.click_area_attach1 || id == R.id.click_area_attach2 || id == R.id.mail_info_flag_attach) {
            if (attachmentIcon.getVisibility() == View.VISIBLE) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        } else if (id == R.id.click_area_detail_1 || id == R.id.click_area_detail_2 || id == R.id.first_info_line_left || id == R.id.second_info_line) {
            ArrayList<ContactEntity> contactList = ContactEntity.getContactList(data.getMail());
            Intent intent = new Intent(mActivity, MessageContactActivity.class);
            intent.putExtra("message", contactList);
            mActivity.startActivity(intent);
        } else if (id == R.id.btn_forward_details) {
            mHandler.sendEvent(EvenId.POP_WINDOW_FORWARD);
        } else if (id == R.id.iv_email_more) {
            mHandler.sendEvent(EvenId.POP_WINDOW_MORE_ACTIONS);
        } else if (id == R.id.pre_details) {
            mHandler.sendEvent(EvenId.CAT_PRE_MAIL);
        } else if (id == R.id.next_details) {
            mHandler.sendEvent(EvenId.CAT_NEXT_MAIL);
        } else if (id == R.id.acty_mail_detail) {
            mHandler.sendEvent(EvenId.DOWNLOAD_ALL_ATTACHMENT, "");
        } else if (id == R.id.tv_reply_content) {
            mHandler.sendEvent(EvenId.WEB_VIEW_QUICK_REPLY);
        } else if (id == R.id.email_detail_header_icon) {
            mHandler.sendEvent(EvenId.MAIL_PERSON_INFO);
        }
    }

    private void setOnScrollListener() {
        myScrollView.setOnScrollListener(new MyScrollView.OnScrollListener() {
            @Override
            public void onScroll(int scrollY, int oldscrollY, boolean isUp) {
                if (scrollY >= rrTitle2DetailTop) {
                    if (rlTitle2Detail.getParent() != llTopDetails) {
                        rrTitle2Detail.removeView(rlTitle2Detail);
                        llTopDetails.addView(rlTitle2Detail);
                    }
                } else {
                    if (rlTitle2Detail.getParent() != rrTitle2Detail) {
                        llTopDetails.removeView(rlTitle2Detail);
                        rrTitle2Detail.addView(rlTitle2Detail);
                    }
                }
            }
        });
    }

    public int getPreMinusPos(EmailData dataPkg) {
        if (dataPkg == null || OTHER_ACTIVITY.equals(dataPkg.getFrom())) {
            return -1;
        }
        if (dataPkg.getPosition() == 0) {
            return -1;
        }
        for (int i = dataPkg.getPosition() - 1; i >= 0; i--) {
            if (dataPkg.getMailList().get(i).uid() > 0) {
                return i;
            }
        }
        return -1;
    }

    public int getNextAddPos(EmailData dataPkg) {
        if (dataPkg == null || OTHER_ACTIVITY.equals(dataPkg.getFrom())) {
            return -1;
        }
        if (dataPkg.getPosition() == dataPkg.getMailList().size() - 1) {
            return -1;
        }
        for (int i = dataPkg.getPosition() + 1; i < dataPkg.getMailList().size(); i++) {
            if (dataPkg.getMailList().get(i).uid() > 0) {
                return i;
            }
        }
        return -1;
    }
}
