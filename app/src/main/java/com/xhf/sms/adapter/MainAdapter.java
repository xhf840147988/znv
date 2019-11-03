package com.xhf.sms.adapter;


import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xhf.sms.R;
import com.xhf.sms.RatingBarView;
import com.xhf.sms.bean.MainBean;

import androidx.annotation.NonNull;

public class MainAdapter extends BaseQuickAdapter<MainBean, BaseViewHolder> {


    public MainAdapter() {
        super(R.layout.item_main);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MainBean item) {
        ImageView view = helper.getView(R.id.imgView);
        Glide.with(mContext)
                .load(item.getThumb())
                .into(view);
        helper.setText(R.id.distanceView, item.getDistance() + "km")
                .setText(R.id.tvScore, "评分：" + item.getScore())
                .setText(R.id.nameView, item.getName())
                .setText(R.id.ageView, item.getAge() + "岁")
                .setText(R.id.jobView, item.getJob())
                .setText(R.id.priceView, "日租" + item.getPrice() + "元")
                .setText(R.id.rentTimesView, "已租" + item.getRenttimes() + "次");
        RatingBarView barView = helper.getView(R.id.starView);
        double score = Double.parseDouble(item.getScore());
        barView.setStar((int) Math.round(score));

    }
}
