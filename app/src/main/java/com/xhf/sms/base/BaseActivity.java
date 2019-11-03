package com.xhf.sms.base;

import android.os.Bundle;

import com.xhf.sms.utils.StatusBarUtil;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;

/**
 * Created by xhf on 2019/11/2.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        StatusBarUtil.setLightMode(this);
        initView();
        initData();
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();
}
