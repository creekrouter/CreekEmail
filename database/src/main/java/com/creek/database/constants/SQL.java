package com.creek.database.constants;

public class SQL {
    public static String getMinUid(String userEmail, String folderName) {
        return "select * from " + SQLiteConstant.MAIL_DETAIL + " where user_email = '" + userEmail + "' and " +
                "email_folder = '" + folderName + "'  order by email_uid limit 0,3";
    }
}
