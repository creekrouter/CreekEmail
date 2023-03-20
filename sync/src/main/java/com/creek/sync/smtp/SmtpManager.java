package com.creek.sync.smtp;


import com.creek.common.UserInfo;
import com.libmailcore.ConnectionType;
import com.libmailcore.SMTPSession;


public class SmtpManager {
    private SMTPSession session;

    private static SmtpManager theSingleton;


    public static SmtpManager singleton() {
        if (theSingleton == null) {
            theSingleton = new SmtpManager();
        }
        return theSingleton;
    }

    public SMTPSession getSession() {
        return session;
    }

    private SmtpManager() {
        init();
    }

    private void init(){
        String hostname="smtp.163.com";
        session = new SMTPSession();
        session.setUsername(UserInfo.userEmail);
        session.setPassword(UserInfo.sessionId);
        session.setHostname(hostname);
        session.setPort(465);
        session.setConnectionType(ConnectionType.ConnectionTypeTLS);
    }

}
