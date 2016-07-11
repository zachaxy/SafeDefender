package com.zachaxy.safedefender.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.zachaxy.safedefender.dao.AddressDao;
import com.zachaxy.safedefender.receiver.OutCallReceiver;
import com.zachaxy.safedefender.utils.AddrToastUtils;

/**
 * Created by zhangxin on 2016/7/11.
 * 来电显示服务
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
        //监听来电显示
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listner = new SafePhoneStateListner();
        tm.listen(listner,PhoneStateListener.LISTEN_CALL_STATE);

        outCallReceiver = new OutCallReceiver();
        registerReceiver(outCallReceiver,new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //改变状态,停止监听
        tm.listen(listner,PhoneStateListener.LISTEN_NONE);
        unregisterReceiver(outCallReceiver);
    }





    class SafePhoneStateListner extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING:
                    //电话铃响
                    String incomingAddress = AddressDao.getAddress(incomingNumber);
                    //Toast.makeText(IncomingCallAddrService.this,incomingAddress,Toast.LENGTH_LONG).show();
                    AddrToastUtils.show(IncomingCallAddrService.this,incomingAddress);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    //TODO:取消归属地显示
                    AddrToastUtils.hide();
                    break;
                default:
                    break;
            }
        }
    }
}
