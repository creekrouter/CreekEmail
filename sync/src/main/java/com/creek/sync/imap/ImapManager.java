package com.creek.sync.imap;


import com.creek.common.UserInfo;
import com.libmailcore.ConnectionType;
import com.libmailcore.IMAPIdentity;
import com.libmailcore.IMAPSession;

public class ImapManager {
    private IMAPSession session;

    public static ImapManager theSingleton;

    public static ImapManager singleton() {
        if (theSingleton == null) {
            synchronized (ImapManager.class) {
                if (theSingleton == null) {
                    theSingleton = new ImapManager();
                }
            }
        }
        return theSingleton;
    }

    public IMAPSession getSession() {
        return session;
    }


    private ImapManager() {
        initSession();
    }

    private void initSession() {
        String hostname="imap.163.com";
        session = new IMAPSession();
        session.setUsername(UserInfo.userEmail);
        session.setPassword(UserInfo.sessionId);
        session.setHostname(hostname);
        session.setPort(993);
        session.setConnectionType(ConnectionType.ConnectionTypeTLS);
        IMAPIdentity imapIdentity = session.clientIdentity();
        imapIdentity.setName("jack");
        imapIdentity.setVendor("jack");
        imapIdentity.setVersion("0.0.1");

    }
}
