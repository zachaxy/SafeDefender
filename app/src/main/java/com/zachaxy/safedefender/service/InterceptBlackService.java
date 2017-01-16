package com.zachaxy.safedefender.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.zachaxy.safedefender.dao.BlackListDao;

import java.lang.reflect.Method;

/**
 * 拦截黑名单中设置的电话和短信的后台服务
 */
public class InterceptBlackService extends Service {

    BlackListDao dao;
    TelephonyManager tm;
    BlackNumberListenr listner;
    InnerReicerver innerReicerver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        dao = new BlackListDao(this);
        //初始化接收短信的广播
        innerReicerver = new InnerReicerver();
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(Integer.MAX_VALUE - 1);
        registerReceiver(innerReicerver, intentFilter);

        //获取到系统电话服务
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listner = new BlackNumberListenr();
        tm.listen(listner, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(innerReicerver);
        tm.listen(listner, PhoneStateListener.LISTEN_NONE);
    }

    private class InnerReicerver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //可以判断一下intent的action是否正确,不过使用的是动态注册,已经将action与receiver绑定了,所以进来肯定是短信来了
            Bundle bundle = intent.getExtras();
            Object[] pdus = (Object[]) bundle.get("pdus");//提取短信消息
            SmsMessage[] messages = new SmsMessage[pdus.length];

            if (pdus.length > 0) {
                messages[0] = SmsMessage.createFromPdu((byte[]) pdus[0]);
                String from = messages[0].getOriginatingAddress();//获取对方手机号码
                String mode = dao.findModeByNumber(from);
                switch (mode) {
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

    private class BlackNumberListenr extends PhoneStateListener {
        //电话状态改变的回调函数
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    //新的来电,电话铃响
                    String mode = dao.findModeByNumber(incomingNumber);
                    switch (mode) {
                        case "0":
                        case "2":
                            System.out.println("黑名单中的号码,电话拦截...");

                            /*try {
                                //获取android.os.ServiceManager类的对象的getService()方法
                                Method method=Class.forName("android.os.ServiceManager").
                                        getMethod("getService",String.class);
                                // 获取远程TELEPHONY_SERVICE的IBinder对象的代理
                                IBinder binder =(IBinder)method.invoke(null, new Object[] {TELEPHONY_SERVICE});
                                // 将IBinder对象的代理转换为ITelephony对象
                                ITelephony telephony=ITelephony.Stub.asInterface(binder);
                                //挂断电话
                                telephony.endCall();
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }*/

                            break;
                        default:
                            break;
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    //对方电话挂断,取消归属地显示
                    break;
                default:
                    break;
            }
        }
    }

    private void endCall(){
        try {
            Class<?> aClass = getClassLoader().loadClass("android.os.ServiceManager");
            Method method = aClass.getDeclaredMethod("getService",String.class);
            IBinder iBinder = (IBinder) method.invoke(null,TELEPHONY_SERVICE);
            //iBinder
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
