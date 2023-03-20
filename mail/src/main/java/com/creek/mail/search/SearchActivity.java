package com.creek.mail.search;

import static com.libmailcore.IMAPFolderFlags.IMAPFolderFlagDrafts;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.creek.common.BaseActivity;
import com.creek.mail.compose.Compose;
import com.creek.mail.details.DetailsPageActivity;
import com.mail.tools.MailSp;
import com.creek.mail.R;
import com.creek.common.UserInfo;
import com.creek.common.MailBean;
import com.creek.mail.compose.ComposeActivity;
import com.creek.common.dialog.LoadingDialog;
import com.mail.tools.MailToast;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SearchActivity extends BaseActivity implements EmailSearch {
    private final String SHARE_PREFERENCES_KEY = UserInfo.userEmail + "_mail_search_history";
    private String folderName = "";
    private TextView cancel;
    private EditText mEditText;
    private TextView mNoData;
    private SearchAdapter mAdapter;
    private View rlClearHistory;
    SearchListView lvResult;
    private TagFlowLayout mHistoryListLayout;
    private List<String> mHistoryList = new ArrayList<>();
    private TagAdapter<String> tagAdapter;
    private ImageView mClearAllHistory;

    private SearchBoxView mSearchBox;
    private LocalSearchManager localSearchManager;
    private LoadingDialog mRemoteSearchDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_search);
        localSearchManager = new LocalSearchManager(mContext, this);
        mAdapter = new SearchAdapter(mContext);
        mRemoteSearchDialog = new LoadingDialog.Builder().setText("正在查询").cancelAble(false).build(mContext);
        mRemoteSearchDialog.setBackPressDismiss(true);
        initView();
        folderName = getIntent().getStringExtra("folder_name");
        initSearchListView();
    }

    /**
     * 初始化
     */
    private void initView() {
        rlClearHistory = findViewById(R.id.rl_search_mail);
        lvResult = findViewById(R.id.lv_mail_search);

        mNoData = findViewById(R.id.mail_tv_no_data);
        cancel = findViewById(R.id.contact_tv_cancel_cs);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSearchBox = findViewById(R.id.contact_search_box_cs);
        mEditText = mSearchBox.getSearchInputET();
        mEditText.setEllipsize(TextUtils.TruncateAt.END);
        mSearchBox.getSearchInputET().setFocusable(true);
        mSearchBox.getSearchInputET().setFocusableInTouchMode(true);
        mSearchBox.getSearchInputET().requestFocus();


        mSearchBox.setSearchClickListener(new SearchBoxView.SearchClickListener() {
            @Override
            public void startSearch(String searchKey) {
                String searchStr = mSearchBox.getInputText().trim();
                SearchActivity.this.startSearch(searchStr.trim());
            }
        });

        mSearchBox.setOnClearClickListener(new SearchBoxView.OnClearClickListener() {
            @Override
            public void onClear() {
                //清空搜索内容，搜索列表重置，各种值初始化
                rlClearHistory.setVisibility(View.VISIBLE);
                lvResult.setVisibility(View.GONE);
                mNoData.setVisibility(View.GONE);
                resetSearchHistory();
            }
        });

        resetSearchHistory();
        mHistoryListLayout = findViewById(R.id.email_history_list_view);
        mClearAllHistory = findViewById(R.id.email_iv_clear_search_history);
        mClearAllHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MailSp.clear(SHARE_PREFERENCES_KEY);
                mHistoryList.clear();
                tagAdapter.notifyDataChanged();
            }
        });
        tagAdapter = new TagAdapter<String>(mHistoryList) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_flow_contact_hist, mHistoryListLayout, false);
                tv.setText(s);
                return tv;
            }
        };
        mHistoryListLayout.setAdapter(tagAdapter);
        mHistoryListLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                //触发选中
                mSearchBox.getSearchInputET().setText(mHistoryList.get(position));
                startSearch(mHistoryList.get(position));
                return false;
            }
        });

    }

    private void resetSearchHistory() {
        mHistoryList.clear();
        mHistoryList.addAll(Arrays.asList(MailSp.getStringArray(SHARE_PREFERENCES_KEY)));
        if (tagAdapter != null) {
            tagAdapter.notifyDataChanged();
        }
    }

    private void initSearchListView() {
        lvResult.setAdapter(mAdapter);
        lvResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                jumpActivity(position, mAdapter.getMailDataList());
            }
        });
    }


    @Override
    public void preLocalSearch() {
        mAdapter.getMailDataList().clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLocalSearchResult(List<MailBean> results) {
        rlClearHistory.setVisibility(View.GONE);
        lvResult.setVisibility(View.VISIBLE);
        mNoData.setVisibility(View.GONE);
        mAdapter.getMailDataList().addAll(results);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void localSearchNone() {
        MailToast.show("未查询到符合条件的结果");
        rlClearHistory.setVisibility(View.GONE);
        lvResult.setVisibility(View.VISIBLE);
        mNoData.setVisibility(View.GONE);
        mAdapter.getMailDataList().clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLocalSearchFinish() {
        mAdapter.notifyDataSetChanged();
    }


    private void startSearch(final String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            MailToast.show("搜索条件不能为空");
            return;
        }
        MailSp.appendSaveArray(SHARE_PREFERENCES_KEY, keyword);
        mAdapter.setKeyword(keyword);
        localSearchManager.startLocalSearch(keyword);
    }

    private void jumpActivity(int position, List<MailBean> list) {
        Bundle bundle = new Bundle();
        //草稿箱 直接到写邮件
        if ((list.get(position).getFolderFlag() & IMAPFolderFlagDrafts) > 0) {
            bundle.putInt("type", Compose.TYPE_DRAFTS);
            bundle.putSerializable("mail", list.get(position));
            startActivity(mContext, ComposeActivity.class, bundle);
        } else {
            bundle.putInt(DetailsPageActivity.KEY_POSITION, position);
            bundle.putString(DetailsPageActivity.KEY_FROM, DetailsPageActivity.SEARCH_ACTIVITY);
            startActivity(mContext, DetailsPageActivity.class, bundle);
        }
    }

}
