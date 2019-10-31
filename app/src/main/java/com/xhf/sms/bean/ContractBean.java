package com.xhf.sms.bean;

import java.util.List;

/**
 * Created by xhf on 2019/10/26.
 */
public class ContractBean {

    private String name;
    private String userPhoneNo;
    private String imei;
    private String ip;
    private List<ContractList> contactsList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserPhoneNo() {
        return userPhoneNo;
    }

    public void setUserPhoneNo(String userPhoneNo) {
        this.userPhoneNo = userPhoneNo;
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

    public List<ContractList> getPhoneBeans() {
        return contactsList;
    }

    public void setPhoneBeans(List<ContractList> phoneBeans) {
        contactsList = phoneBeans;
    }

    public static class ContractList {
        private String name;
        private String phoneNo;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhoneNo() {
            return phoneNo;
        }

        public void setPhoneNo(String phoneNo) {
            this.phoneNo = phoneNo;
        }
    }
}
