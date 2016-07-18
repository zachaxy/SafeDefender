package com.zachaxy.safedefender.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by zhangxin on 2016/7/18.
 * <p/>
 * Description :
 * 描述app的bean,分别有app的图标;名称;大小;是否是用户应用/系统应用;是否放置在rom/sd卡;app包名
 */
public class AppInfo {

    private Drawable icon;
    private String name;
    private String size;
    private int appType; //0:占位符 1:用户应用 2:系统应用
    private boolean isRom;

    public AppInfo(int appType) {
        this.appType = appType;
    }

    public AppInfo(Drawable icon, String name, String packageName, String size, int appType, boolean isRom) {
        this.icon = icon;
        this.name = name;
        this.packageName = packageName;
        this.size = size;
        this.appType = appType;
        this.isRom = isRom;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    private String packageName;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getAppType() {
        return appType;
    }

    public void setAppType(int appType) {
        this.appType = appType;
    }

    public boolean isRom() {
        return isRom;
    }

    public void setRom(boolean rom) {
        isRom = rom;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "name='" + name + '\'' +
                ", size=" + size +
                ", isRom=" + isRom +
                ", appType=" + appType +
                '}';
    }
}
