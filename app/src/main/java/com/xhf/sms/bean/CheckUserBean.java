package com.xhf.sms.bean;

/**
 * Created by xhf on 2019/11/2.
 */
public class CheckUserBean {
    private String name;
    private String idcard;
    private String imei;

    public CheckUserBean(String name, String idcard, String imei) {
        this.name = name;
        this.idcard = idcard;
        this.imei = imei;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
