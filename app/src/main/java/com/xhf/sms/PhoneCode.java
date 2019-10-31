package com.xhf.sms;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;


public class PhoneCode extends ContentObserver {

    public static final String SMS_URI_INBOX = "content://sms/inbox";
    public static final String SMS_URI_RAW = "content://sms/raw";
//    public static final int EXPEND_TIME = 30 * 24 * 60 * 60 * 1000;
    private Context mContext;
    SmsListener mListener;
    private StringBuilder mStringBuilder = new StringBuilder();


    public PhoneCode(Context context, Handler handler, SmsListener listener) {
        super(handler);
        this.mContext = context;
        this.mListener = listener;
    }


    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        Log.e("onChange", uri.toString());
        long curTime = System.currentTimeMillis();
        mStringBuilder.setLength(0);

        if (SMS_URI_RAW.equals(uri.toString()))
            return;


        // 按时间顺序排序短信数据库
        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
        Cursor cursor = mContext.getContentResolver().query(Uri.parse(SMS_URI_INBOX), projection, null,
                null, "date desc");
        if (cursor != null) {
            while (cursor.moveToFirst()) {
                // 获取手机号
                String address = cursor.getString(cursor.getColumnIndex("address"));
                // 获取短信内容
                String body = cursor.getString(cursor.getColumnIndex("body"));
                long startTime = cursor.getLong(cursor.getColumnIndex("date"));
//                Log.e("ADDRESS", (startTime + EXPEND_TIME > curTime) + "--" + body);
//                if (startTime + EXPEND_TIME > curTime) {
//                    Log.e("body", "--" + body);
//                    if (body.contains("验证码")) {
//                        if (mListener != null) {
//                            mStringBuilder.append(body + "\n");
//                        }
//                    }
//
//                }
                mListener.onResult(address,body);


            }
        }

    }

    /**
     * 短信回调接口
     */
    public interface SmsListener {

        void onResult(String phone,String body);
    }
}
