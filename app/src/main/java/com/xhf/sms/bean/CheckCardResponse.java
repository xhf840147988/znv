package com.xhf.sms.bean;

/**
 * Created by xhf on 2019/10/26.
 */
public class CheckCardResponse {
    private String msg;
    private boolean success;
    private int code;
    private DataBean mDataBean;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getDataBean() {
        return mDataBean;
    }

    public void setDataBean(DataBean dataBean) {
        mDataBean = dataBean;
    }

    public static class DataBean {
        private int result;
        private String order_no;
        private String desc;
        private String msg;

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }

        public String getOrder_no() {
            return order_no;
        }

        public void setOrder_no(String order_no) {
            this.order_no = order_no;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
