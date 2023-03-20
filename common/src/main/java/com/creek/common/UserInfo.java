package com.creek.common;

public class UserInfo {

    public static String userEmail;
    public static String userName;
    public static String sessionId;

    public static boolean setUserMail(String mail) {
        if (mail != null && mail.contains("@")) {
            String[] arr = mail.split("@");
            if (arr.length == 2) {
                userEmail = mail;
                userName = arr[0];
                return true;
            }
        }
        return false;
    }


    public static void setSessionId(String sessionId) {
        UserInfo.sessionId = sessionId;
    }

}
