package com.creek.mail.home.swipe;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import com.libmailcore.MessageFlag;
import com.creek.common.MailBean;
import com.creek.mail.home.status.InboxStatus;
import static com.libmailcore.IMAPFolderFlags.IMAPFolderFlagDrafts;
import static com.libmailcore.IMAPFolderFlags.IMAPFolderFlagFlagged;
import static com.libmailcore.IMAPFolderFlags.IMAPFolderFlagInbox;
import static com.libmailcore.IMAPFolderFlags.IMAPFolderFlagJunk;
import static com.libmailcore.IMAPFolderFlags.IMAPFolderFlagNone;
import static com.libmailcore.IMAPFolderFlags.IMAPFolderFlagSentMail;
import static com.libmailcore.IMAPFolderFlags.IMAPFolderFlagTrash;


public class SwipeMenuAdapter extends BaseAdapter implements SwipeMenuView.OnSwipeItemClickListener {

    private int folderFlag = IMAPFolderFlagInbox;
    private ListAdapter mAdapter;
    private Context mContext;
    public InboxStatus status = InboxStatus.Normal;
    private OnSwipeMenuItemClick onMenuItemClickListener;

    public SwipeMenuAdapter(Context context, ListAdapter adapter) {
        mAdapter = adapter;
        mContext = context;
    }

    public void setFolderFlag(int flag) {
        folderFlag = flag;
    }

    @Override
    public int getCount() {
        return mAdapter.getCount();
    }

    @Override
    public Object getItem(int position) {
        return mAdapter.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return mAdapter.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SwipeMenuLayout layout = null;
        if (convertView == null) {
            View contentView = mAdapter.getView(position, convertView, parent);
            SwipeMenu menu = new SwipeMenu(mContext);
            menu.setViewType(mAdapter.getItemViewType(position));
            createMenu(menu);
            SwipeMenuView menuView = new SwipeMenuView(menu);
            menuView.setOnSwipeItemClickListener(this);
            layout = new SwipeMenuLayout(contentView, menuView);
            layout.setPosition(position);
        } else {
            layout = (SwipeMenuLayout) convertView;
            layout.closeMenu();
            layout.setPosition(position);
            View view = mAdapter.getView(position, layout.getContentView(),
                    parent);
        }
        //根据邮件实体判断是否是未读
        MailBean mailInfo = (MailBean) mAdapter.getItem(position);
        int flag = mailInfo.getEmailFlag();

        //选中状态下隐藏菜单
        if (status == InboxStatus.Select) {
            layout.getMenuView().getChildAt(0).setVisibility(View.GONE);
            layout.getMenuView().getChildAt(1).setVisibility(View.GONE);
            layout.getMenuView().getChildAt(2).setVisibility(View.GONE);
            layout.getMenuView().getChildAt(3).setVisibility(View.GONE);
            layout.getMenuView().getChildAt(4).setVisibility(View.GONE);
            layout.getMenuView().getChildAt(5).setVisibility(View.GONE);
        } else {
            layout.getMenuView().getChildAt(0).setVisibility(View.VISIBLE);
            layout.getMenuView().getChildAt(5).setVisibility(View.VISIBLE);
            hideOrVisiableMenue(flag, layout);
        }


        return layout;
    }

    /**
     * Description:根据文件夹类型 显示当前邮件可以操作的类型
     *
     * @param mailFlag 邮件flag
     * @author jack
     * Created at 2018/7/20 10:32
     */

    private void hideOrVisiableMenue(int mailFlag, SwipeMenuLayout layout) {

        if ((folderFlag & IMAPFolderFlagInbox) > 0) {
            //收件箱  显示已读，未读，更多（红旗，移动）
            if ((mailFlag & MessageFlag.MessageFlagSeen) == MessageFlag.MessageFlagSeen) {
                layout.getMenuView().getChildAt(2).setVisibility(View.VISIBLE);
                layout.getMenuView().getChildAt(1).setVisibility(View.GONE);
            } else {
                layout.getMenuView().getChildAt(2).setVisibility(View.GONE);
                layout.getMenuView().getChildAt(1).setVisibility(View.VISIBLE);
            }
            if ((mailFlag & MessageFlag.MessageFlagFlagged) == MessageFlag.MessageFlagFlagged) {
                layout.getMenuView().getChildAt(3).setVisibility(View.VISIBLE);
                layout.getMenuView().getChildAt(4).setVisibility(View.GONE);
            } else {
                layout.getMenuView().getChildAt(4).setVisibility(View.VISIBLE);
                layout.getMenuView().getChildAt(3).setVisibility(View.GONE);
            }
        } else if ((folderFlag & IMAPFolderFlagDrafts) > 0) {
            //草稿箱 只显示删除
            layout.getMenuView().getChildAt(0).setVisibility(View.GONE);
            layout.getMenuView().getChildAt(1).setVisibility(View.GONE);
            layout.getMenuView().getChildAt(2).setVisibility(View.GONE);
            layout.getMenuView().getChildAt(3).setVisibility(View.GONE);
            layout.getMenuView().getChildAt(4).setVisibility(View.GONE);
            layout.getMenuView().getChildAt(5).setVisibility(View.VISIBLE);

        } else if ((folderFlag & IMAPFolderFlagSentMail) > 0) {
            //已发送  显示已读，未读，更多（红旗，移动）
            layout.getMenuView().getChildAt(0).setVisibility(View.GONE);
            if ((mailFlag & MessageFlag.MessageFlagSeen) == MessageFlag.MessageFlagSeen) {
                layout.getMenuView().getChildAt(2).setVisibility(View.VISIBLE);
                layout.getMenuView().getChildAt(1).setVisibility(View.GONE);
            } else {
                layout.getMenuView().getChildAt(2).setVisibility(View.GONE);
                layout.getMenuView().getChildAt(1).setVisibility(View.VISIBLE);
            }
            if ((mailFlag & MessageFlag.MessageFlagFlagged) == MessageFlag.MessageFlagFlagged) {
                layout.getMenuView().getChildAt(3).setVisibility(View.VISIBLE);
                layout.getMenuView().getChildAt(4).setVisibility(View.GONE);
            } else {
                layout.getMenuView().getChildAt(4).setVisibility(View.VISIBLE);
                layout.getMenuView().getChildAt(3).setVisibility(View.GONE);
            }

        } else if ((folderFlag & IMAPFolderFlagTrash) > 0) {
            //已删除  显示已读，未读，更多（红旗，移动）
            if ((mailFlag & MessageFlag.MessageFlagSeen) == MessageFlag.MessageFlagSeen) {
                layout.getMenuView().getChildAt(2).setVisibility(View.VISIBLE);
                layout.getMenuView().getChildAt(1).setVisibility(View.GONE);
            } else {
                layout.getMenuView().getChildAt(2).setVisibility(View.GONE);
                layout.getMenuView().getChildAt(1).setVisibility(View.VISIBLE);
            }
            if ((mailFlag & MessageFlag.MessageFlagFlagged) == MessageFlag.MessageFlagFlagged) {
                layout.getMenuView().getChildAt(3).setVisibility(View.VISIBLE);
                layout.getMenuView().getChildAt(4).setVisibility(View.GONE);
            } else {
                layout.getMenuView().getChildAt(4).setVisibility(View.VISIBLE);
                layout.getMenuView().getChildAt(3).setVisibility(View.GONE);
            }

        } else if ((folderFlag & IMAPFolderFlagJunk) > 0) {
            //垃圾
            if ((mailFlag & MessageFlag.MessageFlagSeen) == MessageFlag.MessageFlagSeen) {
                layout.getMenuView().getChildAt(2).setVisibility(View.VISIBLE);
                layout.getMenuView().getChildAt(1).setVisibility(View.GONE);
            } else {
                layout.getMenuView().getChildAt(2).setVisibility(View.GONE);
                layout.getMenuView().getChildAt(1).setVisibility(View.VISIBLE);
            }
            if ((mailFlag & MessageFlag.MessageFlagFlagged) == MessageFlag.MessageFlagFlagged) {
                layout.getMenuView().getChildAt(3).setVisibility(View.VISIBLE);
                layout.getMenuView().getChildAt(4).setVisibility(View.GONE);
            } else {
                layout.getMenuView().getChildAt(4).setVisibility(View.VISIBLE);
                layout.getMenuView().getChildAt(3).setVisibility(View.GONE);
            }
        } else if ((folderFlag & IMAPFolderFlagFlagged) > 0) {
            //红旗邮件箱  显示已读，未读，更多（红旗，移动）
            if ((mailFlag & MessageFlag.MessageFlagSeen) == MessageFlag.MessageFlagSeen) {
                layout.getMenuView().getChildAt(2).setVisibility(View.VISIBLE);
                layout.getMenuView().getChildAt(1).setVisibility(View.GONE);
            } else {
                layout.getMenuView().getChildAt(2).setVisibility(View.GONE);
                layout.getMenuView().getChildAt(1).setVisibility(View.VISIBLE);
            }
            if ((mailFlag & MessageFlag.MessageFlagFlagged) == MessageFlag.MessageFlagFlagged) {
                layout.getMenuView().getChildAt(3).setVisibility(View.VISIBLE);
                layout.getMenuView().getChildAt(4).setVisibility(View.GONE);
            } else {
                layout.getMenuView().getChildAt(4).setVisibility(View.VISIBLE);
                layout.getMenuView().getChildAt(3).setVisibility(View.GONE);
            }

        } else if (folderFlag == IMAPFolderFlagNone) {//自定义文件夹
            //红旗邮件箱  显示已读，未读，更多（红旗，移动）
            if ((mailFlag & MessageFlag.MessageFlagSeen) == MessageFlag.MessageFlagSeen) {
                layout.getMenuView().getChildAt(2).setVisibility(View.VISIBLE);
                layout.getMenuView().getChildAt(1).setVisibility(View.GONE);
            } else {
                layout.getMenuView().getChildAt(2).setVisibility(View.GONE);
                layout.getMenuView().getChildAt(1).setVisibility(View.VISIBLE);
            }
            if ((mailFlag & MessageFlag.MessageFlagFlagged) == MessageFlag.MessageFlagFlagged) {
                layout.getMenuView().getChildAt(3).setVisibility(View.VISIBLE);
                layout.getMenuView().getChildAt(4).setVisibility(View.GONE);
            } else {
                layout.getMenuView().getChildAt(4).setVisibility(View.VISIBLE);
                layout.getMenuView().getChildAt(3).setVisibility(View.GONE);
            }
        }
    }

    public void createMenu(SwipeMenu menu) {
        // TestAba Code
        SwipeMenuItem item = new SwipeMenuItem(mContext);
        item.setTitle("Item 1");
        item.setBackground(new ColorDrawable(Color.GRAY));
        item.setWidth(300);
        menu.addMenuItem(item);

        item = new SwipeMenuItem(mContext);
        item.setTitle("Item 2");
        item.setBackground(new ColorDrawable(Color.RED));
        item.setWidth(300);
        menu.addMenuItem(item);
    }

    @Override
    public void onItemClick(SwipeMenuView view, SwipeMenu menu, int index) {
        if (onMenuItemClickListener != null) {
            onMenuItemClickListener.onMenuItemClick(view.getPosition(), menu,
                    index);
        }
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mAdapter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mAdapter.unregisterDataSetObserver(observer);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return mAdapter.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int position) {
        return mAdapter.isEnabled(position);
    }

    @Override
    public boolean hasStableIds() {
        return mAdapter.hasStableIds();
    }

    @Override
    public int getItemViewType(int position) {
        return mAdapter.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return mAdapter.getViewTypeCount();
    }

    @Override
    public boolean isEmpty() {
        return mAdapter.isEmpty();
    }

//	@Override
//	public ListAdapter getWrappedAdapter() {
//		return mAdapter;
//	}

}
