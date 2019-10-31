package com.xhf.sms.api;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by xhf on 2018/4/9
 */
public class AppContext extends ContextWrapper {

    public Handler applicationHandler = new Handler(Looper.getMainLooper());

    public static void attachApp(Application application) {
        Holder.INSTANCE = new AppContext(application);
    }

    public AppContext(Context base) {
        super(base);
    }

    @Override
    public Context getApplicationContext() {
        return getBaseContext();
    }

    @Override
    public void sendBroadcast(Intent intent) {
        try {
            super.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Intent registerReceiver(
            BroadcastReceiver receiver, IntentFilter filter) {
        try {
            return super.registerReceiver(receiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        try {
            super.unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ComponentName startService(Intent service) {
        try {
            return super.startService(service);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean stopService(Intent name) {
        try {
            return super.stopService(name);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn,
                               int flags) {
        try {
            return super.bindService(service, conn, flags);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        try {
            super.unbindService(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class Holder {
        private static AppContext INSTANCE;
    }

    public static AppContext get() {
        if (Holder.INSTANCE == null) {
            throw new IllegalStateException("AppContext not init");
        }
        return Holder.INSTANCE;
    }

    public static Handler getHandler() {
        return AppContext.get().applicationHandler;
    }

    public static Application getApplication() {
        return (Application) AppContext.get().getBaseContext();
    }

}
