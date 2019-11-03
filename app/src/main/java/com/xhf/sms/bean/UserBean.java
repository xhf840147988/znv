package com.xhf.sms.bean;

/**
 * Created by xhf on 2019/10/28
 */
public class UserBean {
    private String phone;
    private String imei;
    private String name;
    private String ip;
    private String location;

    public UserBean(String phone, String imei, String name, String ip, String location) {
        this.phone = phone;
        this.imei = imei;
        this.name = name;
        this.ip = ip;
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
