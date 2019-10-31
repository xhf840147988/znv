package com.xhf.sms.api;

import android.content.Context;
import android.util.Log;

import com.xhf.sms.api.service.ApiService;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiManager {

    private ApiService mApiService;
    private Context mContext;

    public static final int READ_TIME_OUT = 10000;
    public static final int CONNECT_TIME_OUT = 10000;
    public static final int WRITE_TIME_OUT = 10000;
    public static final String BASE_URL = "http://redbook.tchiu090.cn/index/";

    private ApiManager() {
        mContext = AppContext.get().getBaseContext();
        mApiService = getRetrofit(BASE_URL).create(ApiService.class);
    }

    private Retrofit getRetrofit(String url) {
        OkHttpClient client = newClient();
        return new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public OkHttpClient newClient() {
        File httpCacheDirectory = new File(mContext.getExternalCacheDir(), "HttpCache");
        Cache cache = new Cache(httpCacheDirectory, 50 * 1024 * 1024);
        return new OkHttpClient.Builder()
                .connectTimeout(WRITE_TIME_OUT, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIME_OUT, TimeUnit.MILLISECONDS)
                .writeTimeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(new LogInterceptor())
                .cache(cache)
                .build();

    }

    public class LogInterceptor implements Interceptor {

        public  String TAG = "LogInterceptor";

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            long startTime = System.currentTimeMillis();
            okhttp3.Response response = chain.proceed(chain.request());
            long endTime = System.currentTimeMillis();
            long duration=endTime-startTime;
            okhttp3.MediaType mediaType = response.body().contentType();
            String content = response.body().string();
            Log.d(TAG,"\n");
            Log.d(TAG,"----------Start----------------");
            Log.d(TAG, "| "+request.toString());
            String method=request.method();
            if("POST".equals(method)){
                StringBuilder sb = new StringBuilder();
                if (request.body() instanceof FormBody) {
                    FormBody body = (FormBody) request.body();
                    for (int i = 0; i < body.size(); i++) {
                        sb.append(body.encodedName(i) + "=" + body.encodedValue(i) + ",");
                    }
                    sb.delete(sb.length() - 1, sb.length());
                    Log.d(TAG, "| RequestParams:{"+sb.toString()+"}");
                }
            }
            Log.d(TAG, "| body:" + content);
            Log.d(TAG,"----------End:"+duration+"毫秒----------");
            return response.newBuilder()
                    .body(okhttp3.ResponseBody.create(mediaType, content))
                    .build();
        }
    }




    public ApiService getApiService() {
        return mApiService;
    }

    public static ApiManager getInstance() {
        return ApiManager.Holder.INSTANCE;
    }

    private static class Holder {
        private static final ApiManager INSTANCE = new ApiManager();
    }


}
