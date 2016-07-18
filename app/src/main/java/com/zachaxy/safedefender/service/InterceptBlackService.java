package com.zachaxy.safedefender.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.zachaxy.safedefender.dao.BlackListDao;

/**
 * 拦截黑名单中设置的电话和短信的后台服务
 */
public class InterceptBlackService extends Service {

    BlackListDao dao;

    @Override
    public IBinder onBind(Intent intent) {
        throw null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        dao = new BlackListDao(this);
        //初始化接收短信的广播
        InnerReicerver innerReicerver = new InnerReicerver();
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(Integer.MAX_VALUE - 1);
        registerReceiver(innerReicerver, intentFilter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class InnerReicerver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            Object[] pdus = (Object[]) bundle.get("pdus");//提取短信消息
            SmsMessage[] messages = new SmsMessage[pdus.length];

            if (pdus.length > 0) {
                messages[0] = SmsMessage.createFromPdu((byte[]) pdus[0]);
                String from = messages[0].getOriginatingAddress();//获取对方手机号码
                String mode = dao.findModeByNumber(from);
                switch (mode){
                    case "1":
                    case "2":
                        System.out.println("黑名单中的号码,短信拦截...");
                        abortBroadcast();
                        break;
                    default:
                        break;
                }
            }

        }
    }

}
