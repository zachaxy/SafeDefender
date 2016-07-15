package com.zachaxy.safedefender.bean;

/**
 * Created by zhangxin on 2016/7/13.
 * <p>
 * Description :
 * 黑名单bean类
 */
public class BlackItemInfo {

    private String number;
    //TODO:mode的取值范围是0,1,2,后续可以将mode的存储模式改为整数?
    private String mode;

    public BlackItemInfo(String number, String mode) {
        this.number = number;
        this.mode = mode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
