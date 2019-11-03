package com.xhf.sms;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.xhf.sms.base.BaseActivity;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.OnClick;

public class GuideActivity extends BaseActivity {


    @BindView(R.id.progressView)
    SaleProgressView mProgressView;
    @BindView(R.id.flProcess)
    FrameLayout mFlProcess;
    @BindView(R.id.tvPostion)
    TextView mPostionView;
    private MyHandler mMyHandler;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    private String address;


    private class MyHandler extends Handler {
        private final WeakReference<GuideActivity> mActivty;

        public MyHandler(GuideActivity activity) {
            mActivty = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            GuideActivity activity = mActivty.get();
            super.handleMessage(msg);
            if (activity != null) {
                //执行业务逻辑
                mProgressView.setTotalAndCurrentCount(100, msg.arg1);
                mMyHandler.postDelayed(myRunnable, 80);
                Log.e("znv", "handleMessage: " + msg.arg1);
                if (msg.arg1 > 100) {
                    Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                    intent.putExtra("address", address);
                    startActivity(intent);
                    mMyHandler.removeCallbacks(myRunnable);
                } else if (msg.arg1 > 90) {
                    mPostionView.setText(address);
                }

            }
        }

    }

    private final Runnable myRunnable = new Runnable() {
        int i = 0;

        @Override
        public void run() {
            i++;
            Message msg = mMyHandler.obtainMessage();
            msg.arg1 = i;
            mMyHandler.sendMessage(msg);
        }
    };


    @Override
    protected int getLayoutId() {
        return R.layout.activity_guide;
    }

    @Override
    protected void initView() {
        mMyHandler = new MyHandler(this);
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(aMapLocation -> {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    Log.e("znv", "onLocationChanged: " + aMapLocation.getAddress());
                    address = aMapLocation.getAddress();

                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }

        });

        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        /**
         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
         */
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);

        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);


    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMyHandler.removeCallbacks(myRunnable);
    }

    @OnClick({R.id.ivBack, R.id.btnCommit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.btnCommit:
                mFlProcess.setVisibility(View.VISIBLE);
                mMyHandler.post(myRunnable);
                break;
        }
    }


}
