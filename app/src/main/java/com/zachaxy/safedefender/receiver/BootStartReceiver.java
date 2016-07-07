package com.zachaxy.safedefender.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
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
        mPref = context.getSharedPreferences("config", context.MODE_PRIVATE);
        boolean isProtect = mPref.getBoolean("protect", false);

        //只有在防盗保护开启的情况下,才进行sim卡判断.
        if (isProtect) {
            String sim = mPref.getString("SimSerial", null);
            if (!TextUtils.isEmpty(sim)) {
                String currentSim = ((TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE)).getSimSerialNumber();
                if (!sim.equals(currentSim)) {
                    //一旦发现手机的序列号改变,就向安全号码发送报警短信.
                    String safeNumber = mPref.getString("safe_number", "");
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(safeNumber, null, "sim card changed", null, null);
                }
            }
        }
    }
}
