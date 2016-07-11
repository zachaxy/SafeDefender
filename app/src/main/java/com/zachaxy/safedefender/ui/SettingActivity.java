package com.zachaxy.safedefender.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.zachaxy.safedefender.R;
import com.zachaxy.safedefender.service.IncomingCallAddrService;
import com.zachaxy.safedefender.utils.ServiceStatusUtils;
import com.zachaxy.safedefender.widget.SettingItemView;


/***
 * 程序设置界面setting
 */
public class SettingActivity extends Activity {

    private SharedPreferences mPref;
    private SettingItemView mSettingAutoUpdate, mSettingShowAddr;
    private boolean autoUpdate, showAddr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mPref = getSharedPreferences("config", MODE_PRIVATE);


        initAutoUpdateItem();
        initShowAddreItem();

    }


    private void initAutoUpdateItem() {
        mSettingAutoUpdate = (SettingItemView) findViewById(R.id.set_autoupdate);

        autoUpdate = mPref.getBoolean("auto_update", true);
        mSettingAutoUpdate.setCheck(autoUpdate);

        mSettingAutoUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSettingAutoUpdate.isCheck()) {
                    mSettingAutoUpdate.setCheck(false);
                    mPref.edit().putBoolean("auto_update", false).commit();
                } else {
                    mSettingAutoUpdate.setCheck(true);
                    mPref.edit().putBoolean("auto_update", true).commit();
                }
            }
        });
    }

    /***
     * 初始化归属地开关
     */
    private void initShowAddreItem() {
        mSettingShowAddr = (SettingItemView) findViewById(R.id.set_show_coming_call_number);

        //这里根据当前服务是否运行来更新勾选框
        showAddr = ServiceStatusUtils.isServiceRunning(this, "com.zachaxy.safedefender.service.IncomingCallAddrService");
        mSettingShowAddr.setCheck(showAddr);

        mSettingShowAddr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSettingShowAddr.isCheck()) {
                    mSettingShowAddr.setCheck(false);
                    stopService(new Intent(SettingActivity.this, IncomingCallAddrService.class));
                } else {
                    mSettingShowAddr.setCheck(true);
                    startService(new Intent(SettingActivity.this, IncomingCallAddrService.class));
                }
            }
        });
    }
}
