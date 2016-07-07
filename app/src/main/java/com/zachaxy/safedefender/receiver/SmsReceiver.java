package com.zachaxy.safedefender.receiver;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.zachaxy.safedefender.R;
import com.zachaxy.safedefender.service.LocationService;

/**
 * Created by zhangxin on 2016/7/6.
 */
public class SmsReceiver extends BroadcastReceiver {

    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdminSample;


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Object[] pdus = (Object[]) bundle.get("pdus");//提取短信消息
        SmsMessage[] messages = new SmsMessage[pdus.length];

        for (int i = 0; i < pdus.length; i++) {
            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
        }

        String from = messages[0].getOriginatingAddress();//获取对方手机号码
        String content = "";                                //获取短信内容
        for (SmsMessage msg : messages) {
            content += msg.getMessageBody();
        }

        /*if ("#*location*#".equals(content)) {

        }
        if ("#*alarm*#".equals(content)) {

        }
        if ("#*wipedata*#".equals(content)) {

        }
        if ("#*lockscreen*#".equals(content)) {

        }*/


        switch (content) {
            case "#*location*#":
                //开启定位的服务
                context.startService(new Intent(context, LocationService.class));
                abortBroadcast();
                break;
            case "#*alarm*#":
                MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                player.setVolume(1f, 1f);  //左右声道最大声
                player.setLooping(true);  //单曲循环
                player.start();           //开始播放
                abortBroadcast();
                break;
            case "#*wipedata*#":
                mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                mDeviceAdminSample = new ComponentName(context, AdminReceiver.class);
                //首先判断设别管理器是否已经激活,如果未激活,那么程序会直接崩溃
                if (!mDPM.isAdminActive(mDeviceAdminSample)) {
                    //进入激活设备的向导
                    activeDevice(context);
                }
                mDPM.wipeData(0);//清除数据,恢复出厂设置
                abortBroadcast();
                break;
            case "#*lockscreen*#":
                mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                mDeviceAdminSample = new ComponentName(context, AdminReceiver.class);
                //首先判断设别管理器是否已经激活,如果未激活,那么程序会直接崩溃
                if (!mDPM.isAdminActive(mDeviceAdminSample)) {
                    //进入激活设备的向导
                    activeDevice(context);
                }
                mDPM.lockNow();
                mDPM.resetPassword("123456", 0);  //重置锁屏密码.并且不让其他设备重复设置密码,进一步加强设备安全性
                abortBroadcast();
                break;
            default:
                break;
        }
    }

    private void activeDevice(Context context) {
        //进入激活设备的向导
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        mDeviceAdminSample = new ComponentName(context, AdminReceiver.class);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                context.getString(R.string.add_admin_extra_app_text));
        context.startActivity(intent);
    }

}


