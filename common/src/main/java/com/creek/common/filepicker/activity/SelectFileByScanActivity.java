package com.creek.common.filepicker.activity;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.creek.common.BaseActivity;
import com.creek.common.R;
import com.creek.common.filepicker.SelectOptions;
import com.creek.common.filepicker.adapter.FragmentPagerAdapter;
import com.creek.common.filepicker.loader.EssMimeTypeCollection;
import com.creek.common.filepicker.model.EssFile;
import com.creek.common.filepicker.model.FileScanActEvent;
import com.creek.common.filepicker.model.FileScanFragEvent;
import com.creek.common.filepicker.model.FileScanSortChangedEvent;
import com.creek.common.filepicker.util.Const;
import com.creek.common.filepicker.util.FileUtils;
import com.creek.common.filepicker.view.FilePickerTabLayout;
import com.creek.router.annotation.CreekBean;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 通过扫描来选择文件
 */
@CreekBean(path = "activity_select_file_by_scan_activity")
public class SelectFileByScanActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    /*todo 是否可预览文件，默认可预览*/
    private boolean mCanPreview = true;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TextView tvOk, tvTitle;

    private ArrayList<EssFile> mSelectedFileList = new ArrayList<>();
    /*当前选中排序方式的位置*/
    private int mSelectSortTypeIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext.setTheme(R.style.FilePicker_light);
        setContentView(R.layout.activity_select_file_by_scan);
        EventBus.getDefault().register(this);

        initUi();
    }


    private void initUi() {
        mViewPager = findViewById(R.id.vp_select_file_scan);
        FilePickerTabLayout filePickerTabLayout = findViewById(R.id.tabl_select_file_scan);
        mTabLayout = filePickerTabLayout.getTabLayout();
        tvOk = findViewById(R.id.select);
        tvTitle = findViewById(R.id.title);

        mTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        tvTitle.setText(String.format(mContext.getResources().getString(R.string.selected_file_count), String.valueOf(mSelectedFileList.size()), String.valueOf(SelectOptions.getInstance().maxCount)));

        List<Fragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < SelectOptions.getInstance().getFileTypes().length; i++) {
            fragmentList.add(FileTypeListFragment.newInstance(SelectOptions.getInstance().getFileTypes()[i], SelectOptions.getInstance().isSingle, SelectOptions.getInstance().maxCount, SelectOptions.getInstance().getSortType(), EssMimeTypeCollection.LOADER_ID + i));
        }
        PagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager(), fragmentList, Arrays.asList(SelectOptions.getInstance().getFileTypes()));
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(fragmentList.size() - 1);
        mViewPager.addOnPageChangeListener(this);

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    /**
     * Fragment中选择文件后
     *
     * @param event event
     */
    @Subscribe
    public void onFragSelectFile(FileScanFragEvent event) {
        if (event.isAdd()) {
            if (SelectOptions.getInstance().isSingle) {
                mSelectedFileList.add(event.getSelectedFile());
                Intent result = new Intent();
                result.putParcelableArrayListExtra(Const.EXTRA_RESULT_SELECTION, mSelectedFileList);
                setResult(RESULT_OK, result);
                super.onBackPressed();
                return;
            }
            mSelectedFileList.add(event.getSelectedFile());
        } else {
            mSelectedFileList.remove(event.getSelectedFile());
        }
        tvTitle.setText(String.format(mContext.getResources().getString(R.string.selected_file_count), String.valueOf(mSelectedFileList.size()), String.valueOf(SelectOptions.getInstance().maxCount)));
        EventBus.getDefault().post(new FileScanActEvent(SelectOptions.getInstance().maxCount - mSelectedFileList.size()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.browser_select_count) {
            //选中
            if (mSelectedFileList.isEmpty()) {
                return true;
            }
            //不为空
            Intent result = new Intent();
            result.putParcelableArrayListExtra(Const.EXTRA_RESULT_SELECTION, mSelectedFileList);
            setResult(RESULT_OK, result);
            super.onBackPressed();
        } else if (i == R.id.browser_sort) {
            //排序
            new AlertDialog
                    .Builder(mContext)
                    .setSingleChoiceItems(R.array.sort_list_scan, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mSelectSortTypeIndex = which;
                        }
                    })
                    .setNegativeButton("降序", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (mSelectSortTypeIndex) {
                                case 0:
                                    SelectOptions.getInstance().setSortType(FileUtils.BY_NAME_DESC);
                                    break;
                                case 1:
                                    SelectOptions.getInstance().setSortType(FileUtils.BY_TIME_ASC);
                                    break;
                                case 2:
                                    SelectOptions.getInstance().setSortType(FileUtils.BY_SIZE_DESC);
                                    break;
                            }
                            EventBus.getDefault().post(new FileScanSortChangedEvent(SelectOptions.getInstance().getSortType(), mViewPager.getCurrentItem()));
                        }
                    })
                    .setPositiveButton("升序", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (mSelectSortTypeIndex) {
                                case 0:
                                    SelectOptions.getInstance().setSortType(FileUtils.BY_NAME_ASC);
                                    break;
                                case 1:
                                    SelectOptions.getInstance().setSortType(FileUtils.BY_TIME_DESC);
                                    break;
                                case 2:
                                    SelectOptions.getInstance().setSortType(FileUtils.BY_SIZE_ASC);
                                    break;
                            }
                            EventBus.getDefault().post(new FileScanSortChangedEvent(SelectOptions.getInstance().getSortType(), mViewPager.getCurrentItem()));
                        }
                    })
                    .setTitle("请选择")
                    .show();

        }
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        EventBus.getDefault().post(new FileScanActEvent(SelectOptions.getInstance().maxCount - mSelectedFileList.size()));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
