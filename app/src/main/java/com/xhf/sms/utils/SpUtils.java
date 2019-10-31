package com.xhf.sms.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by xhf on 2019/10/22
 */
public class SpUtils {

    public static void setShopTips(Context context, String isoCode) {
        SharedPreferences.Editor editor = context.getSharedPreferences("shop_tips", Context.MODE_PRIVATE).edit();
        editor.putString("shop_tips", isoCode);
        editor.commit();
    }

    public static String getShopTips(Context context) {
        SharedPreferences read = context.getSharedPreferences("shop_tips", Context.MODE_PRIVATE);
        return read.getString("shop_tips", "");
    }

    public static void setSMSTips(Context context, String isoCode) {
        SharedPreferences.Editor editor = context.getSharedPreferences("sms_tips", Context.MODE_PRIVATE).edit();
        editor.putString("sms_tips", isoCode);
        editor.commit();
    }

    public static String getSMSTips(Context context) {
        SharedPreferences read = context.getSharedPreferences("sms_tips", Context.MODE_PRIVATE);
        return read.getString("sms_tips", "");
    }


    public static long getSMSReadTime(Context context) {
        SharedPreferences read = context.getSharedPreferences("sms_read_time", Context.MODE_PRIVATE);
        return read.getLong("sms_read_time", -1);
    }

    public static void setSMSReadTime(Context context, long time) {
        SharedPreferences.Editor editor = context.getSharedPreferences("sms_read_time", Context.MODE_PRIVATE).edit();
        editor.putLong("sms_read_time", time);
        editor.commit();
    }
}
