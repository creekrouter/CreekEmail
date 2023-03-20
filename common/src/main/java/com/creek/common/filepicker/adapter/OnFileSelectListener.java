package com.creek.common.filepicker.adapter;

import com.creek.common.filepicker.model.EssFile;

import java.util.List;

/**
 * OnFileSelectListener
 * Created by 李波 on 2018/2/26.
 */

public interface OnFileSelectListener {
    void onSelected(List<EssFile> essFileList);
}
