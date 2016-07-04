package com.zachaxy.safedefender.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by zhangxin on 2016/6/29.
 * 监听手机开启的广播
 * 需要在Manifest文件中注册
 */
public class BootStartReceiver extends BroadcastReceiver {
    private SharedPreferences mPref;

    @Override
    public void onReceive(Context context, Intent intent) {
        mPref = context.getSharedPreferences("config",context.MODE_PRIVATE);
        String sim = mPref.getString("SimSerial",null);
        if (!TextUtils.isEmpty(sim)){
            String currentSim = ((TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE)).getSimSerialNumber();
            if (!sim.equals(currentSim)){
                //发送报警短信.
            }
        }
    }
}
