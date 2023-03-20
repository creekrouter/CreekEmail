package com.creek.mail.contact;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creek.common.BaseActivity;
import com.creek.common.MailContact;
import com.creek.common.sidebar.SideBarLayout;
import com.creek.database.DBManager;
import com.creek.mail.R;
import com.creek.mail.search.SearchBoxView;
import com.creek.router.annotation.CreekBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CreekBean(path = "mail_activity_contacts_page_activity")
public class ContactsPageActivity extends BaseActivity implements SearchBoxView.EditChangedListener {

    RecyclerView recyclerView;
    SideBarLayout sidebarView;
    SearchBoxView searchBoxView;

    SortAdapter mSortAdapter;
    List<SortBean> mList;
    private int mScrollState = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_page);

        recyclerView = findViewById(R.id.recyclerView);
        sidebarView = findViewById(R.id.sidebar);
        searchBoxView = findViewById(R.id.contact_search_box_cs);

        //ImmersionBar.with(this).transparentStatusBar().fitsSystemWindows(false).statusBarDarkFont(false).init();
//        edtSearch.addTextChangedListener(this);
        searchBoxView.setChangedListener(this);


        mScrollState = -1;
        initData();
        connectData();

        findViewById(R.id.contact_tv_cancel_cs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.finish();
            }
        });
    }

    private void initData() {
        List<MailContact> contacts = DBManager.selectMailContactByKey("");
        mList = new ArrayList<>();

        for (MailContact contact:contacts){
            SortBean bean = new SortBean(contact.getDisplay_name(),contact.getEmail_addr());
            mList.add(bean);
        }


        //进行排序
        Collections.sort(mList, new SortComparator());
        mSortAdapter = new SortAdapter(mContext, mList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //设置LayoutManager为LinearLayoutManager
        recyclerView.setAdapter(mSortAdapter);
        recyclerView.setNestedScrollingEnabled(false);//解决滑动不流畅

    }

    private void connectData() {
        //侧边栏滑动 --> item
        sidebarView.setSideBarLayout(new SideBarLayout.OnSideBarLayoutListener() {
            @Override
            public void onSideBarScrollUpdateItem(String word) {
                //循环判断点击的拼音导航栏和集合中姓名的首字母,如果相同recyclerView就跳转指定位置
                for (int i = 0; i < mList.size(); i++) {
                    if (mList.get(i).getWord().equals(word)) {
                        recyclerView.smoothScrollToPosition(i);
                        break;
                    }
                }
            }
        });
        //item滑动 --> 侧边栏
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int scrollState) {
                super.onScrollStateChanged(recyclerView, scrollState);
                mScrollState = scrollState;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mScrollState != -1) {
                    //第一个可见的位置
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    //判断是当前layoutManager是否为LinearLayoutManager
                    // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
                    int firstItemPosition = 0;
                    if (layoutManager instanceof LinearLayoutManager) {
                        LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                        //获取第一个可见view的位置
                        firstItemPosition = linearManager.findFirstVisibleItemPosition();
                    }

                    sidebarView.OnItemScrollUpdateText(mList.get(firstItemPosition).getWord());
                    if (mScrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        mScrollState = -1;
                    }
                }
            }
        });


    }

    /**
     * 匹配输入数据
     *
     * @param keyword
     * @param list
     * @return
     */
    public List<SortBean> matcherSearch(String keyword, List<SortBean> list) {
        List<SortBean> results = new ArrayList<>();
        String patten = Pattern.quote(keyword);
        Pattern pattern = Pattern.compile(patten, Pattern.CASE_INSENSITIVE);
        for (int i = 0; i < list.size(); i++) {
            //根据首字母
            Matcher matcherWord = pattern.matcher((list.get(i)).getWord());
            //根据拼音
            Matcher matcherPin = pattern.matcher((list.get(i)).getPinyin());
            //根据简拼
            Matcher matcherJianPin = pattern.matcher((list.get(i)).getJianpin());
            //根据名字
            Matcher matcherName = pattern.matcher((list.get(i)).getName());
            if (matcherWord.find() || matcherPin.find() || matcherName.find() || matcherJianPin.find()) {
                results.add(list.get(i));
            }
        }

        return results;
    }

    @Override
    public void changedSearch(String keyWord) {
        if (mList == null || mList.size() <= 0) {
            return;
        }
        if (!keyWord.equals("")) {
            if (matcherSearch(keyWord, mList).size() > 0) {
                sidebarView.OnItemScrollUpdateText(matcherSearch(keyWord, mList).get(0).getWord());
            }
            mSortAdapter.setNewData(matcherSearch(keyWord, mList));
        } else {
            sidebarView.OnItemScrollUpdateText(mList.get(0).getWord());
            mSortAdapter.setNewData(mList);
        }
    }
}