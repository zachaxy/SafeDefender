package com.zachaxy.safedefender.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.zachaxy.safedefender.dao.AddressDao;
import com.zachaxy.safedefender.utils.AddrToastUtils;

/**
 * Created by zhangxin on 2016/7/11.
 * 监听拨打电话的广播,显示归属地.
 */
public class OutCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String number = getResultData();
        String address = AddressDao.getAddress(number);
        //Toast.makeText(context, address, Toast.LENGTH_LONG).show();
        AddrToastUtils.show(context,address);
    }
}
