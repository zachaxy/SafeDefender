package com.zachaxy.safedefender.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by zhangxin on 2016/7/11.
 */
public class ServiceStatusUtils {


    /***
     * @param context 上下文,以获取SystemService
     * @param serviceName,需要判别服务的名字
     * @return 检测服务是否运行
     */
    public static boolean isServiceRunning(Context context,String serviceName){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //返回系统后台服务的,指定返回100个,如果超出100,那么也最多返回100个
        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(100);
        for(ActivityManager.RunningServiceInfo runningServiceInfo: runningServices){
            if (serviceName.equals(runningServiceInfo.service.getClassName())){
                return true;
            }
        }
        return false;
    }
}
