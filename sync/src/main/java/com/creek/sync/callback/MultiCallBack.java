package com.creek.sync.callback;

import com.creek.common.interfaces.CommonCallBack;

import java.util.ArrayList;
import java.util.List;

public class MultiCallBack<T> implements CommonCallBack<T,String> {

    private final int TOTAL_PART;
    private volatile int mCount = 0;
    private CommonCallBack<List<T>, String> mCallBack;
    private List<T> partSuccess = new ArrayList<>();


    public MultiCallBack(int totalPart, CommonCallBack<List<T>, String> callBack) {
        TOTAL_PART = totalPart;
        mCallBack = callBack;
    }

    private void addPartNumber(boolean success, T t) {
        synchronized (MultiCallBack.class) {
            mCount++;
            if (success) {
                partSuccess.add(t);
            }
        }
    }

    private boolean isOver() {
        synchronized (MultiCallBack.class) {
            return mCount == TOTAL_PART;
        }
    }

    @Override
    public void success(T t) {
        addPartNumber(true, t);
        callBack();
    }

    @Override
    public void fail(String message) {
        addPartNumber(false, null);
        callBack();
    }

    private void callBack() {
        if (isOver()) {
            if (partSuccess.size() == TOTAL_PART) {
                mCallBack.success(partSuccess);
            } else if (partSuccess.size() == 0) {
                mCallBack.fail("操作失败！");
            } else {
                mCallBack.success(partSuccess);
//                mCallBack.fail("操作部分成功，部分失败！");
            }
        }
    }
}
