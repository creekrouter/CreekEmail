package com.creek.common.filepicker.adapter;



import androidx.annotation.Nullable;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.creek.common.filepicker.model.BreadModel;
import com.creek.common.R;

import java.util.List;

/**
 * BreadAdapter
 * Created by 李波 on 2018/2/5.
 */

public class BreadAdapter extends BaseQuickAdapter<BreadModel, BaseViewHolder> {

    public BreadAdapter(@Nullable List<BreadModel> data) {
        super(R.layout.bread_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BreadModel item) {
        helper.setText(R.id.btn_bread,item.getCurName());
        helper.addOnClickListener(R.id.btn_bread);
    }
}
