package com.creek.common;

public final class CreekPath {
    public static final String GROUP = "CreekMail";
    public static final String App_Context = "mail_global_app_context";

    public static final String Launch_Activity_EmailDetailsPage = "mail_HomePage_GoToEmailDetails";

    /*
    Router Init
     */
    public static final String Mail_Plugin_Init = "Mail_Plugin_Init";
    public static final String Mail_Plugin_Get_First_Page_Path = "Mail_Plugin_Get_First_Page_Path";


    /*
    Broadcast Method
     */
    public static final String Mail_BroadCast_Application_Init = "Mail_BroadCast_Application_Init";
    public static final String Mail_BroadCast_User_Init = "Mail_BroadCast_User_Init";
    public static final String Mail_BroadCast_Inbox_Folder_Changed = "Mail_BroadCast_Inbox_Folder_Changed";


    /*
    fetch mail
     */

    public static final String Fetch_History_Lost_Mail = "Fetch_History_Lost_Mail";


    /*
    sql db
     */
    public static final String SQL_Get_Folder_Name_By_ImapFlag = "SQL_Get_Folder_Name_By_ImapFlag";
    public static final String SQL_Update_Message_List_Folder_Name = "SQL_Update_Message_List_Folder_Name";
    public static final String SQL_Update_Message_ImapFlag = "SQL_Update_Message_ImapFlag";
    public static final String SQL_Delete_Message_List = "SQL_Delete_Message_List";


    public static final String Mail_Message_List_Share_Home = "Mail_Message_List_Share_Home";


    /*
    Global Mails Update for Activities
     */
    public static final String Mail_Information_Update_Flag = "Mail_Information_Update_Flag";

    /*
    Tools
     */
    public static final String Tools_Thread_Pool_Execute_Runnable = "Tools_Thread_Pool_Execute_Runnable";
    public static final String Tools_Save_File_To_SdCard = "Tools_Save_File_To_SdCard";


    /*
    Activity pages
     */
    public static final String Mail_Page_Login_Activity = "Mail_Page_Login_Activity";
}
