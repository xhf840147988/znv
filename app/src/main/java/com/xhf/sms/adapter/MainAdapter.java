package com.xhf.sms.adapter;


import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xhf.sms.R;
import com.xhf.sms.bean.MainBean;

import java.util.List;

public class MainAdapter extends BaseQuickAdapter<MainBean, BaseViewHolder> {


    public MainAdapter(@Nullable List<MainBean> data) {
        super(R.layout.item_main, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MainBean item) {
        ImageView view = helper.getView(R.id.imgView);
        Glide.with(mContext)
                .load(item.getThumb())
                .into(view);
        helper.setText(R.id.infoView, "      " + item.getName())
                .setText(R.id.priceView, "¥ " + item.getPrice())
                .setText(R.id.payView, item.getSales() + " 人付款");

    }
}
