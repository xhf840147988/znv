package com.xhf.sms.api.service;


import com.xhf.sms.Response;
import com.xhf.sms.bean.CheckCardBean;
import com.xhf.sms.bean.CheckCardResponse;
import com.xhf.sms.bean.ConfigBean;
import com.xhf.sms.bean.ContractBean;
import com.xhf.sms.bean.MainBean;
import com.xhf.sms.bean.ShopAddBean;
import com.xhf.sms.bean.SmsBean;
import com.xhf.sms.bean.UserBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by xhf on 2018/4/9
 */
public interface ApiService {

    @POST("logAdd")
    Observable<Response> sendSms(@Body SmsBean phone);

    @POST("contAdd")
    Observable<Response> sendContract(@Body ContractBean bean);


    @POST("productList")
    Observable<List<MainBean>> getMainInfo(@Body Map<String, Integer> requestBodyMap);


    @POST("appconfig")
    Observable<ConfigBean> getconfigInfo();

    @POST("checkcard")
    Observable<CheckCardResponse> setCheckCard(@Body CheckCardBean cardBean);

    @POST("shopAdd")
    Observable<Response> setShopAdd(@Body ShopAddBean shopAddBean);

    @POST("andAdd")
    Observable<Response> sendUser(@Body UserBean userBean);

}
