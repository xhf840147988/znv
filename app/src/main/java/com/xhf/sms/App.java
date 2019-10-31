package com.xhf.sms;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.meiqia.core.callback.OnInitCallback;
import com.meiqia.meiqiasdk.util.MQConfig;
import com.xhf.sms.api.AppContext;


public class App extends Application {

    private static final String APPKEY = "5fdbaf458c0e2ad109213fee8062ca58";


    @Override
    public void onCreate() {
        super.onCreate();
        AppContext.attachApp(this);
        initMQ();

    }

    private void initMQ() {

        MQConfig.init(this, APPKEY, new OnInitCallback() {
            @Override
            public void onSuccess(String clientId) {
                Log.e("Application", "onSuccess: " + clientId);
            }

            @Override
            public void onFailure(int code, String message) {
                Log.e("Application", "onFailure: " + message);
            }
        });
    }
}
