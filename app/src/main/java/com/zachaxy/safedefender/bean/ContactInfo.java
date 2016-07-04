package com.zachaxy.safedefender.bean;

/**
 * Created by zhangxin on 2016/7/4.
 */
public class ContactInfo {
    private String name;
    private String phoneNumber;

    public ContactInfo(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
