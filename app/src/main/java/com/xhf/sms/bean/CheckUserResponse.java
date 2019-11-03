package com.xhf.sms.bean;

/**
 * Created by xhf on 2019/10/26.
 */
public class CheckUserResponse {

    /**
     * code : 0
     * message : 成功
     * result : {"name":"张三","idcard":"362123196409134532","res":"2","description":"不一致","sex":"男","birthday":"19640913","address":"江西省信丰县"}
     */

    private String code;
    private String message;
    private ResultBean result;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * name : 张三
         * idcard : 362123196409134532
         * res : 2
         * description : 不一致
         * sex : 男
         * birthday : 19640913
         * address : 江西省信丰县
         */

        private String name;
        private String idcard;
        private String res;
        private String description;
        private String sex;
        private String birthday;
        private String address;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIdcard() {
            return idcard;
        }

        public void setIdcard(String idcard) {
            this.idcard = idcard;
        }

        public String getRes() {
            return res;
        }

        public void setRes(String res) {
            this.res = res;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
