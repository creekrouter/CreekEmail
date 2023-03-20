package com.creek.mail.details.msg;

public final class EvenId {
    public static final int DEFAULT_EVENT_ID = 0;    //默认无意义操作
    /*
    邮件包含的操作：
    １、删除
    ２、标记已读
    ３、标记未读
    ４、标记红旗
    ５、取消红旗
    ６、移动到其他文件夹
     */
    public static final int MAIL_DELETE = 100;
    public static final int MAIL_READ = 110;
    public static final int MAIL_UNREAD = 111;
    public static final int MAIL_TOGGLE_READ = 112;   //在已读、未读之间切换
    public static final int MAIL_RED_FLAG = 120;
    public static final int MAIL_CANCEL_RED_FLAG = 121;
    public static final int MAIL_TOGGLE_RED_FLAG = 122;  //在标记红旗、取消红旗之间切换
    public static final int MAIL_MOVE = 130;
    public static final int MAIL_ANSWERED = 140;

    /*
    详情页面UI组件刷新
    １、已读和未读显示。
    ２、红旗显示隐藏。
     */
    public static final int UI_VIEW_REFRESH = 200;

    /*
    附件列表UI刷新；
    附件列表从新设定；
     */
    public static final int LIST_NOTIFY_DATA_CHANGE = 210;
    public static final int LIST_DATA_RESET = 211;

    /*
    邮件详情初始化view的引用
    */
    public static final int FIND_VIEW_BY_ID = 220;


    /*
    首次进入邮件详情，初始化页面　UI、内容，
    可能从以下几个地方进如：
    １、邮件首页列表；
    ２、邮件搜索列表；
    ３、即时通等消息列表，直接进入；
     */
    public static final int INIT_UI_CONTENT = 230;

    /*
    activity window 获取到焦点
     */
    public static final int ACTIVITY_WINDOW_FOCUS = 300;


    /*
    邮件上下翻动
    １、上一封邮件；
    ２、下一封邮件；
     */
    public static final int CAT_PRE_MAIL = 400;
    public static final int CAT_NEXT_MAIL = 410;

    /*
    邮件详情中的弹窗popup window：
    １、点击快速回复；
    2、邮件正文中的联系人链接
    ３、邮件正文的http链接
    ４、邮件正文加载中loading
    ５、转发邮件，带附件或者不带附件
    6、邮件详情底部更多按钮，点击弹窗展示多种操作
     */
    public static final int POP_WINDOW_REPLY = 500;
    public static final int POP_WINDOW_MAIL_TO = 510; //邮件正文中链接
    public static final int POP_WINDOW_MAIL_HTTP = 520; //邮件正文中链接
    public static final int LOADING_SHOW = 530;   //邮件正文加载中
    public static final int LOADING_DISMISS = 540;
    public static final int POP_WINDOW_FORWARD = 550; //邮件转发
    public static final int POP_WINDOW_MORE_ACTIONS = 560;
    public static final int MAIL_REPLAY_SENDER = 570;
    public static final int MAIL_REPLAY_ALL = 580;

    /*
    附件相关：
    １、数据库中获取有关附件的信息；
     */
    public static final int ATTACHMENT_INFO_LOAD_START = 610;
    public static final int ATTACHMENT_INFO_LOAD_FINISH = 620;

    /*
    附件下载，包含以下操作：
    １、一键下载所有附件;
    ２、自动下载小于１MB的附件;
    ３、下载单个附件;
    4、查看附件内容；
     */
    public static final int DOWNLOAD_ALL_ATTACHMENT = 630;
    public static final int DOWNLOAD_AUTO_ATTACHMENT = 640;
    public static final int DOWNLOAD_SINGLE_ATTACHMENT = 650;
    public static final int VIEW_ATTACHMENT = 660;


    /*
    邮件获取完毕，分两种情况：
    １、首页列表页、搜索列表页传递过来的邮件Mail;
    2、根据邮件的uid、文件夹，访问网络获取邮件；
     */
    public static final int MAIL_BEAN_INIT_START = 700;
    public static final int MAIL_BEAN_INIT_FINISH = 710;


    /*
    webView加载相关：
    １、webView初始化
    ２、webView加载完成；
     */
    public static final int WEB_VIEW_INIT = 800;
    public static final int WEB_VIEW_LOAD_FINISHED = 810;
    public static final int WEB_VIEW_QUICK_REPLY = 820;

    /*
    往来邮件,个人信息
     */
    public static final int MAIL_PERSON_INFO = 900;
}
