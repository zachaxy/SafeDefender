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
    private boolean isUer;
    private boolean isRom;

    public AppInfo(Drawable icon, String name, String packageName, String size, boolean isUer, boolean isRom) {
        this.icon = icon;
        this.name = name;
        this.isUer = isUer;
        this.size = size;
        this.isRom = isRom;
        this.packageName = packageName;
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

    public boolean isUer() {
        return isUer;
    }

    public void setUer(boolean uer) {
        isUer = uer;
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
                ", isUer=" + isUer +
                '}';
    }
}
