package com.creek.sync;


import com.creek.common.MailAttachment;
import com.creek.common.MailBean;
import com.creek.common.MailFolder;
import com.creek.common.interfaces.CommonCallBack;
import com.creek.common.interfaces.ConfirmCallBack;
import com.creek.sync.impl.SyncImpl;
import com.libmailcore.AbstractPart;
import com.libmailcore.IMAPFolderStatus;
import com.libmailcore.IMAPMessage;
import com.libmailcore.IMAPStoreFlagsRequestKind;
import com.libmailcore.IndexSet;
import com.libmailcore.Range;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
mailcore web: http://libmailcore.com/api/java/index.html
 */

public class MailSync {
    private static SyncImpl impl = new SyncImpl();


    /**
     * get mail folder information
     *
     * @param folderName for example: folderName=INBOX、folderName=DRAFTS、folderName=SENT
     * @param callBack   result callback
     */
    public static void syncFolderStatus(String folderName, CommonCallBack<IMAPFolderStatus, String> callBack) {
        impl.syncFolderStatus(folderName, callBack);
    }

    /**
     * get mail folder list
     *
     * @param userEmail user's email,for example:someone@gmail.com
     * @param callBack  result callback
     */
    public static void syncFolderList(String userEmail, CommonCallBack<List<MailFolder>, String> callBack) {
        impl.syncFolderList(userEmail, callBack);
    }

    /**
     * get mail list
     * an operation to fetch a list of messages by sequence number.
     *
     * @param folderName for example: folderName=INBOX、folderName=DRAFTS、folderName=SENT
     * @param rangeNum   mails on Server count from 1 to MaxCount,range between 1 and MaxCount
     * @param callBack   result callback
     */
    public static void syncMailList(String folderName, Range rangeNum, CommonCallBack<List<IMAPMessage>, String> callBack) {
        impl.syncMailList(folderName, IndexSet.indexSetWithRange(rangeNum), callBack);
    }

    public static void getMailList(String folderName, Range rangeNum, CommonCallBack<List<IMAPMessage>, String> callBack) {
        impl.getMailList(folderName, IndexSet.indexSetWithRange(rangeNum), callBack);
    }

    public static void fetchMailList(String folderName, IndexSet indexSet, CommonCallBack<List<IMAPMessage>, String> callBack) {
        impl.fetchMailList(folderName, indexSet, callBack);
    }

    /**
     * get mail head list
     * an operation to fetch a list of message UIDs .
     *
     * @param folderName for example: folderName=INBOX、folderName=DRAFTS、folderName=SENT
     * @param indexSet   indexSet contains mail UID.
     * @param callBack   result callback
     */
    public static void obtMailList(String folderName, IndexSet indexSet, CommonCallBack<List<IMAPMessage>, String> callBack) {
        impl.obtMailList(folderName, indexSet, callBack);
    }

    /**
     * get mail head list
     * an operation to fetch a list of message Head by UID.
     *
     * @param folderName for example: folderName=INBOX、folderName=DRAFTS、folderName=SENT
     * @param indexSet   indexSet contains mail UID.
     * @param callBack   result callback
     */
    public static void syncMailHead(String folderName, IndexSet indexSet, CommonCallBack<List<IMAPMessage>, String> callBack) {
        impl.syncMailHead(folderName, indexSet, callBack);
    }


    /**
     * get mail details
     * an operation to message details by UID.
     *
     * @param folderName for example: folderName=INBOX、folderName=DRAFTS、folderName=SENT
     * @param mailUid    mail UID.
     * @param callBack   result callback
     */
    public static void syncMailDetails(String folderName, long mailUid, CommonCallBack<List<IMAPMessage>, String> callBack) {
        impl.syncMailDetails(folderName, mailUid, callBack);
    }


    /**
     * get mail plain text
     * an operation to get message plan text.
     *
     * @param folderName for example: folderName=INBOX、folderName=DRAFTS、folderName=SENT
     * @param mailUid    mail UID.
     * @param callBack   result callback
     */
    public static void syncMailPlain(AbstractPart mainPart, String folderName, long mailUid, ConfirmCallBack<String> callBack) {
        impl.syncMailPlain(mainPart, folderName, mailUid, callBack);
    }

    public static void getMailPlain(String folderName, long uid, ConfirmCallBack<String> callBack) {
        impl.getMailPlain(folderName, uid, callBack);
    }


    /**
     * delete mail by uid indexSet
     */
    public static void messageDeleted(List<MailBean> mailList, CommonCallBack<Void, String> callBack) {
        impl.messageDeleted(mailList, callBack);
    }

    public static void messageDeleted(MailBean mailBean, CommonCallBack<Void, String> callBack) {
        List<MailBean> list = new ArrayList<>();
        list.add(mailBean);
        messageDeleted(list, callBack);
    }

    /**
     * move mail to other folder by uid indexSet
     * first copy mail to 'toFolder',then delete mail from 'fromFolder'
     */
    public static void messageMove(String desFolder, List<MailBean> mailList, CommonCallBack<Void, String> callBack) {
        impl.messageMove(desFolder, mailList, callBack);
    }

    public static void messageMove(String desFolder, MailBean mailBean, CommonCallBack<Void, String> callBack) {
        List<MailBean> list = new ArrayList<>();
        list.add(mailBean);
        impl.messageMove(desFolder, list, callBack);
    }

    public static void messageFlagSet(int imapFlag, List<MailBean> mailList, CommonCallBack<Void, String> callBack) {
        impl.messageUpdateFlag(IMAPStoreFlagsRequestKind.IMAPStoreFlagsRequestKindSet, imapFlag, mailList, callBack);
    }

    public static void messageFlagAdd(int imapFlag, List<MailBean> mailList, CommonCallBack<Void, String> callBack) {
        impl.messageUpdateFlag(IMAPStoreFlagsRequestKind.IMAPStoreFlagsRequestKindAdd, imapFlag, mailList, callBack);
    }

    public static void messageFlagRemove(int imapFlag, List<MailBean> mailList, CommonCallBack<Void, String> callBack) {
        impl.messageUpdateFlag(IMAPStoreFlagsRequestKind.IMAPStoreFlagsRequestKindRemove, imapFlag, mailList, callBack);
    }

    public static void messageFlagSet(int imapFlag, MailBean mail, CommonCallBack<Void, String> callBack) {
        List<MailBean> list = new ArrayList<>();
        list.add(mail);
        messageFlagSet(imapFlag, list, callBack);
    }

    public static void messageFlagUpdate(int requestKind, int imapFlag, List<MailBean> mailList, CommonCallBack<Void, String> callBack) {
        impl.messageUpdateFlag(requestKind, imapFlag, mailList, callBack);
    }

    public static void messageFlagUpdate(int requestKind, int imapFlag, MailBean mail, CommonCallBack<Void, String> callBack) {
        List<MailBean> list = new ArrayList<>();
        list.add(mail);
        messageFlagUpdate(requestKind, imapFlag, list, callBack);
    }

    public static void messageFlagAdd(int imapFlag, MailBean mail, CommonCallBack<Void, String> callBack) {
        List<MailBean> list = new ArrayList<>();
        list.add(mail);
        messageFlagAdd(imapFlag, list, callBack);
    }

    public static void messageFlagRemove(int imapFlag, MailBean mail, CommonCallBack<Void, String> callBack) {
        List<MailBean> list = new ArrayList<>();
        list.add(mail);
        messageFlagRemove(imapFlag, list, callBack);
    }

    /**
     * download message attachment
     */
    public static void fetchAttach(MailAttachment attach, CommonCallBack<MailAttachment, String> callBack) {
        impl.fetchAttach(attach, callBack);
    }

    /**
     * download mail body html cid photo
     */
    public static void fetchHtmlPhoto(String photoCID, MailBean mailBean, CommonCallBack<HashMap<String, String>, String> callBack) {
        impl.fetchHtmlPhoto(photoCID, mailBean, callBack);
    }

    public static void fetchHtmlPhotoArr(String[] cidArr, MailBean mailBean, CommonCallBack<HashMap<String, String>, String> callBack) {
        impl.fetchHtmlPhotoArr(cidArr, mailBean, callBack);
    }

}
