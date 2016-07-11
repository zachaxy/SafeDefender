package com.zachaxy.safedefender.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.zachaxy.safedefender.dao.AddressDao;

/**
 * Created by zhangxin on 2016/7/11.
 * 来电显示服务
 */
public class IncomingCallAddrService extends Service {

    private TelephonyManager tm;
    private SafePhoneStateListner listner;

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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //改变状态,停止监听
        tm.listen(listner,PhoneStateListener.LISTEN_NONE);
    }

    class SafePhoneStateListner extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING:
                    //电话铃响
                    String incomingAddress = AddressDao.getAddress(incomingNumber);
                    Toast.makeText(IncomingCallAddrService.this,incomingAddress,Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    }
}
