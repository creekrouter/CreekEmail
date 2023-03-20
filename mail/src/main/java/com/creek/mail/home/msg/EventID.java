package com.creek.mail.home.msg;

public class EventID {
    public static final int Refresh_List = 100;


    public static final int Sync_Folder_From_Local = 200;
    public static final int Sync_Folder_From_Server = 201;
    public static final int Sync_Folder_Local_Over = 202;
    public static final int Sync_Folder_Server_Over = 203;
    public static final int Sync_Folder_UnRead_Count_Server = 204;
    public static final int On_New_Folder_Selected = 205;
    public static final int On_Folder_Changed = 206;
    public static final int Folder_Flag_UnRead_Refresh = 207;
    public static final int Folder_Normal_UnRead_Refresh = 208;
    public static final int Sync_Folder_Menu_UnRead_Count = 209;
    public static final int Folder_Menu_List_Click = 210;
    public static final int Folder_Menu_List_Refresh = 211;
    public static final int Sync_Folder_UnRead_Count_Local = 212;
    public static final int Sync_Folder_UnRead_Count_Local_Over = 213;


    public static final int Clear_Folder_Mail = 300;


    public static final int View_Init = 600;
    public static final int View_Close_Drawer = 610;
    public static final int View_Open_Drawer = 611;
    public static final int View_Toggle_Drawer = 612;
    public static final int View_Drawer_Lock_Mode_UNLOCKED = 613;
    public static final int View_Drawer_Lock_Mode_CLOSED = 614;

    public static final int Inbox_Status_Normal = 700;
    public static final int Inbox_Status_Select = 701;
    public static final int Inbox_Status_Filter = 702;
    public static final int Inbox_Empty_View_Refresh = 703;

    public static final int Inbox_List_Select_Change = 710;
    public static final int Inbox_List_Select_All = 711;
    public static final int Inbox_List_Select_None = 712;

    public static final int Inbox_List_Stop_LoadMore = 720;
    public static final int Inbox_List_Stop_Refresh = 721;

    public static final int Inbox_List_Notify_Data_Changed = 730;

    public static final int Inbox_List_Refresh = 740;
    public static final int Inbox_List_LoadMore = 741;

    public static final int Inbox_Init_Get_Message_From_DB = 800;
}
