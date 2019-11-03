package com.xhf.sms;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import com.xhf.sms.dialog.CenterDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SplashActivity extends AppCompatActivity {

    private CenterDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        requestPermissions();

    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS,Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS}, 1);
            return;
        }
        showGuide();
    }

    private void showGuide() {
        mDialog = CenterDialog.create(getSupportFragmentManager());
        mDialog.setCancelable(false);
        mDialog.setLayoutRes(R.layout.dialog_guide)
                .setViewGravity(Gravity.CENTER)
                .setViewListener(new CenterDialog.ViewListener() {
                    @Override
                    public void bindView(View v) {
                        v.findViewById(R.id.confirmView).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                GoGuide();
                            }
                        });

                    }
                }).show();
    }

    private void GoGuide() {
        startActivity(new Intent(this, GuideActivity.class));
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showGuide();
                } else {
                    finish();
                }
                break;
            default:
        }
    }
}
