package com.creek.mail.compose.action;

import static com.creek.mail.compose.Compose.TYPE_DRAFTS;

import com.creek.common.MailAttachment;
import com.creek.common.MailContact;
import com.creek.common.UserInfo;

import com.creek.common.interfaces.CommonCallBack;
import com.creek.mail.compose.manager.DataManager;
import com.creek.mail.sync.MailDraft;
import com.mail.tools.MailToast;

import java.util.ArrayList;
import java.util.List;

public class SaveEmail {

    public static void saveToDraft(DataManager manager) {


        String fromAddress = UserInfo.userEmail;  //发发件人

        List<String> toAddressList = new ArrayList<>();  //收件人
        for (MailContact contactEntity : manager.toList) {
            toAddressList.add(contactEntity.getEmail_addr());
        }


        List<String> ccAddressList = new ArrayList<>();  //抄送
        for (MailContact contactEntity : manager.ccList) {
            ccAddressList.add(contactEntity.getEmail_addr());
        }

        List<String> bccAddressList = new ArrayList<>();  //密送
        for (MailContact contactEntity : manager.bccList) {
            bccAddressList.add(contactEntity.getEmail_addr());
        }


        String subject = manager.mSubjectText;        //主题

        String body = manager.mContentHtml;        //正文


        List<String> fileName = new ArrayList<>();    //附件

        for (MailAttachment attach : manager.mAttachmentList) {
            fileName.add(attach.getFile_name());
        }

        List<String> filePath = new ArrayList<>();
        for (MailAttachment attach : manager.mAttachmentList) {
            filePath.add(attach.getFile_path());
        }


        MailDraft.save(fromAddress, toAddressList,
                bccAddressList, ccAddressList, subject, body, fileName, filePath,
                new CommonCallBack<Void,String>() {
                    @Override
                    public void success(Void unused) {
                        MailToast.show("保存成功");
                        //如果该邮件是草稿箱中的邮件，需要删除
                        if (manager.mMail != null && manager.mFrom == TYPE_DRAFTS && manager.mMail.folderName().equals("Drafts")) {
                            //删除服务器中的该邮件
//                            mailActionSender.sendOperateAction(ComposeActivity.this, MailOperate.Operate.delete_mail, mMail);
                        }
//                        finish();
                    }

                    @Override
                    public void fail(String errorStr) {
                        MailToast.show("保存失败");
                    }
                });
    }

}
