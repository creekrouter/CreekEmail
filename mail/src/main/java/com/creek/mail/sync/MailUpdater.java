package com.creek.mail.sync;

import static com.creek.mail.sync.ActionType.Answer;
import static com.creek.mail.sync.ActionType.Flag;
import static com.creek.mail.sync.ActionType.Seen;
import static com.creek.mail.sync.ActionType.UnFlag;
import static com.creek.mail.sync.ActionType.UnSeen;

import com.creek.common.CreekPath;
import com.creek.common.MailBean;
import com.creek.common.MailFolder;
import com.creek.common.interfaces.CommonCallBack;
import com.creek.common.interfaces.ConfirmCallBack;
import com.creek.database.DBManager;
import com.creek.common.dialog.LoadingDialog;
import com.creek.mail.pop.Popup;
import com.creek.router.CreekRouter;
import com.creek.sync.MailSync;
import com.libmailcore.IMAPStoreFlagsRequestKind;
import com.libmailcore.MessageFlag;
import com.mail.tools.MailToast;
import com.mail.tools.context.AppContext;

import java.util.ArrayList;
import java.util.List;


public class MailUpdater {

    public static void delete(MailBean mail) {
        if (mail == null) {
            return;
        }
        List<MailBean> list = new ArrayList<>();
        list.add(mail);
        delete(list);
    }

    public static void delete(List<MailBean> mailList) {
        if (mailList == null || mailList.size() == 0) {
            return;
        }
        LoadingDialog dialog = new LoadingDialog.Builder().setText("正在删除...").cancelAble(false).build(AppContext.getTopActivity());
        dialog.setBackPressDismiss(true);
        dialog.show();


        MailSync.messageDeleted(mailList, new CommonCallBack<Void, String>() {
            @Override
            public void success(Void unused) {
                CreekRouter.functionRun(CreekPath.Mail_Information_Update_Flag, mailList);
                dialog.dismiss();
            }

            @Override
            public void fail(String message) {

                dialog.dismiss();
                MailToast.show(message);
            }
        });
    }


    public static void move(List<MailBean> mailList) {
        List<MailFolder> folderList = DBManager.selectMoveFolders();

        Popup.folderSelect(folderList, new ConfirmCallBack<String>() {
            @Override
            public void onConfirm(String desFolder) {
                move(desFolder, mailList);
            }
        });
    }

    public static void move(MailBean mail) {
        List<MailFolder> folderList = DBManager.selectMoveFolders();
        Popup.folderSelect(folderList, new ConfirmCallBack<String>() {
            @Override
            public void onConfirm(String desFolder) {
                move(desFolder, mail);
            }
        });
    }

    public static void move(String desFolder, List<MailBean> mailList) {
        if (mailList == null || mailList.size() == 0) {
            return;
        }

        LoadingDialog dialog = new LoadingDialog.Builder().setText("正在移动...").cancelAble(false).build(AppContext.getTopActivity());
        dialog.setBackPressDismiss(true);
        dialog.show();

        MailSync.messageMove(desFolder, mailList, new CommonCallBack<Void, String>() {
            @Override
            public void success(Void unused) {
                CreekRouter.functionRun(CreekPath.Mail_Information_Update_Flag, mailList);
                dialog.dismiss();
            }

            @Override
            public void fail(String s) {
                dialog.dismiss();
                MailToast.show(s);
            }
        });

    }

    public static void move(String desFolder, MailBean mail) {
        if (mail == null) {
            return;
        }
        List<MailBean> list = new ArrayList<>();
        list.add(mail);
        move(desFolder, list);
    }

    public static void flag(ActionType type, List<MailBean> mailList) {
        if (mailList == null || mailList.size() == 0) {
            return;
        }

        LoadingDialog dialog = new LoadingDialog.Builder().setText("正在更新...").cancelAble(false).build(AppContext.getTopActivity());
        dialog.setBackPressDismiss(true);
        dialog.show();

        int kind = IMAPStoreFlagsRequestKind.IMAPStoreFlagsRequestKindRemove;
        if (type == Seen || type == Flag || type == Answer) {
            kind = IMAPStoreFlagsRequestKind.IMAPStoreFlagsRequestKindAdd;
        }

        int flag = -1;
        if (type == Seen || type == UnSeen) {
            flag = MessageFlag.MessageFlagSeen;
        } else if (type == Flag || type == UnFlag) {
            flag = MessageFlag.MessageFlagFlagged;
        } else if (type == Answer) {
            flag = MessageFlag.MessageFlagAnswered;
        }

        if (flag < 0) {
            return;
        }

        int finalKind = kind;
        int finalFlag = flag;
        MailSync.messageFlagUpdate(kind, flag, mailList, new CommonCallBack<Void, String>() {
            @Override
            public void success(Void unused) {
                dialog.dismiss();
                for (MailBean bean : mailList) {
                    int newFlag = bean.getEmailFlag();
                    if (finalKind == IMAPStoreFlagsRequestKind.IMAPStoreFlagsRequestKindRemove) {
                        newFlag = newFlag & ~finalFlag;
                    } else if (finalKind == IMAPStoreFlagsRequestKind.IMAPStoreFlagsRequestKindAdd) {
                        newFlag = newFlag | finalFlag;
                    } else {
                        newFlag = finalFlag;
                    }
                    bean.setEmailFlag(newFlag);
                }
                CreekRouter.functionRun(CreekPath.Mail_Information_Update_Flag, mailList);
            }

            @Override
            public void fail(String s) {
                dialog.dismiss();
                MailToast.show(s);
            }
        });
    }

    public static void flag(ActionType type, MailBean mail) {
        if (mail == null) {
            return;
        }

        List<MailBean> list = new ArrayList<>();
        list.add(mail);
        flag(type, list);
    }

}
