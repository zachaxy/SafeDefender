package com.zachaxy.safedefender.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.format.Formatter;

import com.zachaxy.safedefender.bean.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxin on 2016/7/18.
 * <p/>
 * Description :
 */
public class AppInfoUtils {

    public static List<AppInfo> getAllApps(Context context) {
        //获取包管理者
        PackageManager pm = context.getPackageManager();

        //获取所有安装的包名
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);

        List<AppInfo> list = new ArrayList<>();
        List<AppInfo> usrList = new ArrayList<>();
        List<AppInfo> sysList = new ArrayList<>();
        //usrList.add(new AppInfo(0));
        sysList.add(new AppInfo(0));


        for (PackageInfo packageInfo : installedPackages) {
            //packageInfo.applicationInfo,得到的是manifest文件中application的节点
            Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
            String name = packageInfo.applicationInfo.loadLabel(pm).toString();
            String packageName = packageInfo.packageName;
            //获取到apk资源的路径,然后统计该路径的大小,然后将其格式化为MB单位的字符串
            String size = Formatter.formatFileSize(context, new File(packageInfo.applicationInfo.sourceDir).length());
            //boolean isUser = false;
            int appType = 1;//默认普通用户
            boolean isRom = false;

            int appFlag = packageInfo.applicationInfo.flags;


            if ((appFlag & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
                //表明在rom中
                isRom = true;
            }

            if ((appFlag & ApplicationInfo.FLAG_SYSTEM) != 0) {
                //表明是系统应用程序
                appType = 2;
                sysList.add(new AppInfo(icon, name, packageName, size, appType, isRom));
            } else {
                usrList.add(new AppInfo(icon, name, packageName, size, appType, isRom));
            }
        }
        SharedPreferences mPref = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        mPref.edit().putInt("user_app_count",usrList.size()).commit();
        mPref.edit().putInt("sys_app_count",sysList.size()-1).commit();
        list.addAll(usrList);
        list.addAll(sysList);
        return list;


    }
}
