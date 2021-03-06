package com.zachaxy.safedefender.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.zachaxy.safedefender.dao.AddressDao;
import com.zachaxy.safedefender.receiver.OutCallReceiver;
import com.zachaxy.safedefender.utils.AddrToastUtils;

/**
 * Created by zhangxin on 2016/7/11.
 * 来电/去电显示服务
 * 去电使用outCallReceiver
 * 来电使用IncomingCallAddrService的listener监听来电状态
 * 来电使用的是service,去电使用的是receiver.
 */
public class IncomingCallAddrService extends Service {

    private TelephonyManager tm;
    private SafePhoneStateListner listner;
    private OutCallReceiver outCallReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //监听来电(只是打过来的,注意和下面的outCallReceiver功能的区别)显示
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listner = new SafePhoneStateListner();
        tm.listen(listner, PhoneStateListener.LISTEN_CALL_STATE);

        //动态注册receiver,执行动作是用来监听去电.目的是显示去电归属地
        outCallReceiver = new OutCallReceiver();
        registerReceiver(outCallReceiver, new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //改变状态,停止监听
        tm.listen(listner, PhoneStateListener.LISTEN_NONE);
        unregisterReceiver(outCallReceiver);
    }


    class SafePhoneStateListner extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {  //该状态有三个:闲置;响铃;接通;这里只监听响铃,表示有电话来.
                case TelephonyManager.CALL_STATE_RINGING:
                    //新的来电,电话铃响
                    String incomingAddress = AddressDao.getAddress(incomingNumber);
                    //Toast.makeText(IncomingCallAddrService.this,incomingAddress,Toast.LENGTH_LONG).show();
                    AddrToastUtils.show(IncomingCallAddrService.this, incomingAddress);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    //对方电话挂断,取消归属地显示
                    AddrToastUtils.hide();
                    break;
                default:
                    break;
            }
        }
    }
}
