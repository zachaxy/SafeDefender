package com.zachaxy.safedefender.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.zachaxy.safedefender.R;

/**
 * Created by zhangxin on 2016/7/6.
 */
public class SmsReceiver extends BroadcastReceiver {

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
                break;
            case "#*alarm*#":
                MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                player.setVolume(1f, 1f);  //左右声道最大声
                player.setLooping(true);  //单曲循环
                player.start();           //开始播放
                abortBroadcast();
                break;
            case "#*wipedata*#":
                break;
            case "#*lockscreen*#":
                break;
            default:
                break;
        }
    }
}
