package com.creek.mail.pop;

import android.app.Activity;
import android.view.View;

import com.creek.common.MailBean;
import com.creek.common.MailFolder;
import com.creek.common.dialog.PopupLayout;
import com.creek.common.interfaces.ConfirmCallBack;
import com.creek.common.interfaces.EventCallBack;
import com.creek.common.pop.PopItemAction;
import com.creek.common.pop.PopWindow;
import com.mail.tools.context.AppContext;

import java.util.List;

public class Popup {
    public static void clearMail(View.OnClickListener left, View.OnClickListener right) {
        Activity context = AppContext.getTopActivity();
        PopLayout pop = new PopLayout(context);
        PopWindow popWindow = new PopWindow.Builder(context)
                .setStyle(PopWindow.PopWindowStyle.PopAlert)
                .setView(pop)
                .create();

        pop.setTitle("清除数据").
                setContent("仅清理手机端数据，不影响服务端数据。").
                setLeftBtn("取消").
                setRightBtn("确定").
                setPopWindow(popWindow).
                setLeftClick(left).
                setRightClick(right);
        popWindow.show();
    }

    public static void deleteMail(View.OnClickListener left, View.OnClickListener right) {
        Activity context = AppContext.getTopActivity();
        PopLayout pop = new PopLayout(context);
        PopWindow popWindow = new PopWindow.Builder(context)
                .setStyle(PopWindow.PopWindowStyle.PopAlert)
                .setView(pop)
                .create();

        pop.setTitle("删除邮件").
                setLeftBtn("取消").
                setRightBtn("确定").
                setPopWindow(popWindow).
                setLeftClick(left).
                setRightClick(right);
        popWindow.show();
    }

    public static void saveSign(View.OnClickListener left, View.OnClickListener right) {
        Activity context = AppContext.getTopActivity();
        PopLayout pop = new PopLayout(context);
        PopWindow popWindow = new PopWindow.Builder(context)
                .setStyle(PopWindow.PopWindowStyle.PopAlert)
                .setView(pop)
                .create();

        pop.setTitle("签名保存").
                setLeftBtn("取消").
                setRightBtn("确定").
                setPopWindow(popWindow).
                setLeftClick(left).
                setRightClick(right);
        popWindow.show();
    }

    public static void mailTo(View.OnClickListener listener) {
        Activity context = AppContext.getTopActivity();
        PopWindow popWindow = new PopWindow.Builder(context)
                .setStyle(PopWindow.PopWindowStyle.PopUp)
                .setTitle("发送邮件")
                .addItemAction(new PopItemAction("取消", PopItemAction.PopItemStyle.Normal, new PopItemAction.OnClickListener() {
                    @Override
                    public void onClick() {

                    }
                }))
                .addItemAction(new PopItemAction("确定", PopItemAction.PopItemStyle.Warning, new PopItemAction.OnClickListener() {
                    @Override
                    public void onClick() {
                        if (listener != null) {
                            listener.onClick(null);
                        }
                    }
                }))
                .create();
        popWindow.show();
    }

    public static void saveDraft(View.OnClickListener left, View.OnClickListener right) {
        Activity context = AppContext.getTopActivity();
        PopLayout pop = new PopLayout(context);
        PopWindow popWindow = new PopWindow.Builder(context)
                .setStyle(PopWindow.PopWindowStyle.PopAlert)
                .setView(pop)
                .create();

        pop.setTitle("草稿保存").
                setLeftBtn("放弃").
                setRightBtn("保存").
                setPopWindow(popWindow).
                setLeftClick(left).
                setRightClick(right);
        popWindow.show();
    }

    public static void toCompose(View.OnClickListener one, View.OnClickListener two) {
        Activity context = AppContext.getTopActivity();
        PopWindow popWindow = new PopWindow.Builder(context)
                .setStyle(PopWindow.PopWindowStyle.PopUp)
                .addItemAction(new PopItemAction("包含附件", PopItemAction.PopItemStyle.Normal, new PopItemAction.OnClickListener() {
                    @Override
                    public void onClick() {
                        if (one != null) {
                            one.onClick(null);
                        }
                    }
                }))
                .addItemAction(new PopItemAction("不包含附件", PopItemAction.PopItemStyle.Normal, new PopItemAction.OnClickListener() {
                    @Override
                    public void onClick() {
                        if (two != null) {
                            two.onClick(null);
                        }
                    }
                }))
                .addItemAction(new PopItemAction("取消", PopItemAction.PopItemStyle.Cancel))
                .create();
        popWindow.show();
    }

    public static void moreAction(MailBean mailBean, ConfirmCallBack<Integer> listener) {
        Activity context = AppContext.getTopActivity();
        PopMoreAction customView = new PopMoreAction(context);
        customView.setData(mailBean);
        PopWindow popWindow = new PopWindow.Builder(context)
                .setStyle(PopWindow.PopWindowStyle.PopUp)
                .addContentView(customView)
                .addItemAction(new PopItemAction("取消", PopItemAction.PopItemStyle.Cancel))
                .create();
        customView.setOnClick(listener, popWindow);
        popWindow.show();
    }

    public static void quickReply(String quickReplyText, MailBean mailBean, EventCallBack<Integer, String> callBack) {
        Activity context = AppContext.getTopActivity();
        PopMailReply customView = new PopMailReply(context);
        customView.setCallBack(callBack);
        PopupLayout popupLayout = PopupLayout.init(context, customView);
        popupLayout.setUseRadius(true);
        popupLayout.setHeight(202, true);
        customView.setData(quickReplyText, mailBean, popupLayout);
        popupLayout.show(PopupLayout.POSITION_BOTTOM);
        customView.requestInputFocus();
    }

    public static void folderSelect(List<MailFolder> folders, ConfirmCallBack<String> callBack) {
        Activity context = AppContext.getTopActivity();
        PopFolder customView = new PopFolder(context);
        customView.setData(folders,callBack);
        PopWindow popWindow = new PopWindow.Builder(context)
                .addContentView(customView)
                .addItemAction(new PopItemAction("确定", PopItemAction.PopItemStyle.Warning, customView))
                .addItemAction(new PopItemAction("取消", PopItemAction.PopItemStyle.Cancel))
                .create();
        popWindow.show();
    }
}

