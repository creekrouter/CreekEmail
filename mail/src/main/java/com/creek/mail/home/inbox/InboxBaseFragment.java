package com.creek.mail.home.inbox;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;

import com.creek.mail.home.manage.HomeManager;
import com.creek.mail.home.HomePageActivity;

public abstract class InboxBaseFragment extends Fragment {

    protected HomeManager manager;
    protected View mRootView;


    public InboxBaseFragment(HomeManager manager) {
        this.manager = manager;
    }


    protected <T extends View> T findViewById(@IdRes int id) {
        T v = mRootView.findViewById(id);
        return v;
    }
}
