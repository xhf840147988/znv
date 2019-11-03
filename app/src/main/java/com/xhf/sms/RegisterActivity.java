package com.xhf.sms;

import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xhf.sms.api.ApiManager;
import com.xhf.sms.base.BaseActivity;
import com.xhf.sms.bean.InviteCodeBean;
import com.xhf.sms.dialog.CenterDialog;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.llMan)
    LinearLayout mLlMan;
    @BindView(R.id.llWomen)
    LinearLayout mLlWomen;
    @BindView(R.id.confirmView)
    ImageView mConfirmView;
    @BindView(R.id.ivMan)
    ImageView mIvMan;
    @BindView(R.id.ivWomen)
    ImageView mIvWomen;
    private boolean isMan;
    private CenterDialog mManDialog;
    private CenterDialog mWomenDialog;
    private CenterDialog mTipDialog;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    protected void initView() {
    }

    @Override
    protected void initData() {

    }


    private void showDialog() {
        if (isMan) {
            mManDialog = CenterDialog.create(getSupportFragmentManager());
            mManDialog.setLayoutRes(R.layout.dialog_register_man)
                    .setViewGravity(Gravity.CENTER)
                    .setViewListener(v -> initDialogView(v, 1)).show();
        } else {

            mTipDialog = CenterDialog.create(getSupportFragmentManager());
            mTipDialog.setLayoutRes(R.layout.dialog_register_tip_women)
                    .setViewGravity(Gravity.CENTER)
                    .setViewListener(v -> {
                        TextView view = v.findViewById(R.id.textView);
                        ImageSpan imgSpan = new ImageSpan(RegisterActivity.this, R.mipmap.pic33);
                        SpannableString spannableString = new SpannableString(getResources().getString(R.string.crate_account_tip));
                        spannableString.setSpan(imgSpan, 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        view.setText(spannableString);
                        v.findViewById(R.id.tvRegister).setOnClickListener(v1 -> {
                            mWomenDialog.show();
                            mTipDialog.dismiss();
                        });

                    }).show();

            mWomenDialog = CenterDialog.create(getSupportFragmentManager());
            mWomenDialog.setLayoutRes(R.layout.dialog_register_women)
                    .setViewGravity(Gravity.CENTER)
                    .setViewListener(v -> initDialogView(v, 0));
        }

    }

    private void initDialogView(View v, final int i) {
        final EditText ed1 = v.findViewById(R.id.edit1);
        final EditText ed2 = v.findViewById(R.id.edit2);
        final EditText ed3 = v.findViewById(R.id.edit3);
        final EditText ed4 = v.findViewById(R.id.edit4);
        TextView btn = v.findViewById(R.id.tvRegister);
        btn.setOnClickListener(v1 -> {
            if (TextUtils.isEmpty(ed1.getText().toString().trim())) {
                Toast.makeText(RegisterActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(ed2.getText().toString().trim())) {
                Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(ed3.getText().toString().trim())) {
                Toast.makeText(RegisterActivity.this, "重复密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(ed4.getText().toString().trim())) {
                Toast.makeText(RegisterActivity.this, "邀请码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!TextUtils.equals(ed2.getText().toString().trim(), ed3.getText().toString().trim())) {
                Toast.makeText(RegisterActivity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }
            requestData(ed4.getText().toString().trim(), i);

        });
    }

    private void requestData(String code, final int i) {
        ApiManager.getInstance().getApiService().inviteCode(new InviteCodeBean(code))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response response) {
                        Log.e("znv", "inviteCode: " + response.getCode());
                        if (response.getCode() == 1) {
                            if (i == 1) {
                                mManDialog.dismiss();
                            } else {
                                mWomenDialog.dismiss();
                            }
                            registerSuccess();
                        } else {
                            Toast.makeText(RegisterActivity.this, "邀请码错误", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("znv", "inviteCode: " + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }


    private void registerSuccess() {
        final CenterDialog dialog = CenterDialog.create(getSupportFragmentManager());
        dialog.setLayoutRes(R.layout.dialog_register_success)
                .setViewGravity(Gravity.CENTER)
                .setViewListener(v -> v.findViewById(R.id.tvRegister).setOnClickListener(v1 -> {
                    startActivity(new Intent(RegisterActivity.this, VerifiedActivity.class));
                    dialog.dismiss();
                }))
                .show();
    }


    @OnClick({R.id.llMan, R.id.llWomen, R.id.confirmView})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llMan:
                isMan = true;
                mIvMan.setImageResource(R.mipmap.pic41);
                mIvWomen.setImageResource(R.mipmap.pic42);
                break;
            case R.id.llWomen:
                isMan = false;
                mIvMan.setImageResource(R.mipmap.pic42);
                mIvWomen.setImageResource(R.mipmap.pic41);
                break;
            case R.id.confirmView:
                showDialog();
                break;

        }
    }

}
