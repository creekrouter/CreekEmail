package com.creek.common.filepicker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

public class FilePickerTabLayout extends RelativeLayout {
    public FilePickerTabLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public FilePickerTabLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FilePickerTabLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private TabLayout tabLayout;

    private void init(Context context) {
        tabLayout = new TabLayout(context);
        tabLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.addView(tabLayout);
    }

    public TabLayout getTabLayout() {
        return tabLayout;
    }
}
