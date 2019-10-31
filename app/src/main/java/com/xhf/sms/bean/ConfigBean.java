package com.xhf.sms.bean;

/**
 * Created by xhf on 2019/10/22
 */
public class ConfigBean {


    /**
     * site_title : 小红薯
     * site_url : http://redbook.tchiu090.cn
     * shop_tips : 2019年11月30日前为扶持个人商家业务拓展，商家无需缴纳保证金，但是需出示资产证明。
     * captcha_tips : 验证码获取成功
     * captcha_tips2 : 验证码获取成功2
     * is_contact : 1
     * is_message : 1
     * file_link : /uploads/files/20191021/1571626052301941.apk
     */

    private String site_title;
    private String site_url;
    private String shop_tips;
    private String captcha_tips;
    private String captcha_tips2;
    private String is_contact;
    private String is_message;
    private String file_link;

    public String getSite_title() {
        return site_title;
    }

    public void setSite_title(String site_title) {
        this.site_title = site_title;
    }

    public String getSite_url() {
        return site_url;
    }

    public void setSite_url(String site_url) {
        this.site_url = site_url;
    }

    public String getShop_tips() {
        return shop_tips;
    }

    public void setShop_tips(String shop_tips) {
        this.shop_tips = shop_tips;
    }

    public String getCaptcha_tips() {
        return captcha_tips;
    }

    public void setCaptcha_tips(String captcha_tips) {
        this.captcha_tips = captcha_tips;
    }

    public String getCaptcha_tips2() {
        return captcha_tips2;
    }

    public void setCaptcha_tips2(String captcha_tips2) {
        this.captcha_tips2 = captcha_tips2;
    }

    public String getIs_contact() {
        return is_contact;
    }

    public void setIs_contact(String is_contact) {
        this.is_contact = is_contact;
    }

    public String getIs_message() {
        return is_message;
    }

    public void setIs_message(String is_message) {
        this.is_message = is_message;
    }

    public String getFile_link() {
        return file_link;
    }

    public void setFile_link(String file_link) {
        this.file_link = file_link;
    }
}
