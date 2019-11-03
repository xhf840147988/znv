package com.xhf.sms;


import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xhf.sms.api.ApiManager;
import com.xhf.sms.base.BaseActivity;
import com.xhf.sms.bean.CheckUserBean;
import com.xhf.sms.bean.CheckUserResponse;
import com.xhf.sms.dialog.CenterDialog;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class VerifiedActivity extends BaseActivity implements TextWatcher {


    @BindView(R.id.edit1)
    EditText mEdit1;
    @BindView(R.id.edit2)
    EditText mEdit2;
    @BindView(R.id.edit3)
    EditText mEdit3;
    @BindView(R.id.tvWarn)
    TextView mTvWarn;
    @BindView(R.id.confirmView)
    TextView mConfirmView;

    private CenterDialog mTipDialog;
    private ImageView mIvBank, mIvWechat;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_verified;
    }

    @Override
    protected void initView() {
        mEdit1.addTextChangedListener(this);
        mEdit2.addTextChangedListener(this);
        mEdit3.addTextChangedListener(this);

    }

    @Override
    protected void initData() {
    }


    @OnClick(R.id.confirmView)
    public void onClick() {
        String edit1 = mEdit1.getText().toString().trim();
        String edit2 = mEdit2.getText().toString().trim();
        String edit3 = mEdit3.getText().toString().trim();
        if (TextUtils.isEmpty(edit1)) {
            mTvWarn.setText("*请输入姓名");
            mTvWarn.setVisibility(View.VISIBLE);
            return;
        }

        if (TextUtils.isEmpty(edit2)) {
            mTvWarn.setText("*请输入身份证号码");
            mTvWarn.setVisibility(View.VISIBLE);
            return;
        }

        if (TextUtils.isEmpty(edit3)) {
            mTvWarn.setText("*请输入联系手机");
            mTvWarn.setVisibility(View.VISIBLE);
            return;
        }

        CheckUserBean userBean = new CheckUserBean(edit1, edit2, AppUtils.getDeviceID(this));
        ApiManager.getInstance().getApiService().checkUser(userBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CheckUserResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CheckUserResponse response) {
                        if (response.getResult() == null)
                            return;
                        if (TextUtils.equals("1", response.getResult().getRes())) {
                            showCenterDialog();

                        } else {
                            mTvWarn.setVisibility(View.VISIBLE);
                            mTvWarn.setText("信息不匹配");
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    boolean isWechat = false;
    boolean isBank = true;

    private void showCenterDialog() {
        CenterDialog dialog = CenterDialog.create(getSupportFragmentManager());
        dialog.setLayoutRes(R.layout.dialog_verfied)
                .setViewListener(v -> {
                    mIvBank = v.findViewById(R.id.ivBank);
                    LinearLayout llBank = v.findViewById(R.id.llBank);
                    mIvWechat = v.findViewById(R.id.ivWechat);
                    LinearLayout llWechat = v.findViewById(R.id.llWechat);
                    llBank.setOnClickListener(v1 -> {
                        mIvBank.setImageResource(R.mipmap.pic41);
                        mIvWechat.setImageResource(R.mipmap.pic42);

                        if (isBank) {
                            startActivity(new Intent(VerifiedActivity.this, BuySafeActivity.class));
                        }
                        isWechat = false;
                        isBank = true;
                    });
                    llWechat.setOnClickListener(v12 -> {

                        mIvBank.setImageResource(R.mipmap.pic42);
                        mIvWechat.setImageResource(R.mipmap.pic41);
                        if (isWechat) {
                            showTipDialog();
                        }
                        isWechat = true;
                        isBank = false;
                    });

                }).show();

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mTvWarn.setVisibility(View.INVISIBLE);

    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    @OnClick({R.id.ivBack})
    public void onDialogClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                finish();
                break;
        }
    }

    private void showTipDialog() {
        mTipDialog = CenterDialog.create(getSupportFragmentManager());
        mTipDialog.setLayoutRes(R.layout.dialog_verfied_tip)
                .setViewListener(v -> {
                    ImageView imageView = v.findViewById(R.id.ivClose);
                    TextView textView = v.findViewById(R.id.confirmView);
                    imageView.setOnClickListener(v1 -> mTipDialog.dismiss());
                    textView.setOnClickListener(v12 -> mTipDialog.dismiss());
                }).show();
    }

}
