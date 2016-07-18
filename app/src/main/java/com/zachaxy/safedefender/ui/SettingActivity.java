package com.zachaxy.safedefender.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.zachaxy.safedefender.R;
import com.zachaxy.safedefender.service.IncomingCallAddrService;
import com.zachaxy.safedefender.service.InterceptBlackService;
import com.zachaxy.safedefender.service.RocketService;
import com.zachaxy.safedefender.utils.ServiceStatusUtils;
import com.zachaxy.safedefender.widget.SettingItemView;
import com.zachaxy.safedefender.widget.SettingSelectItemView;


/***
 * 程序设置界面setting
 */
public class SettingActivity extends Activity {

    private SharedPreferences mPref;
    private SettingItemView mSettingAutoUpdate, mSettingRocket, mSettingShowAddr, mInterceptBlack;
    private SettingSelectItemView mSettingAddrStyle, mSettingAddrPosition;
    private boolean autoUpdate, showRocket, showAddr, interceptBlack;
    private int styleIndex;

    private String[] addrStyles = {"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mPref = getSharedPreferences("config", MODE_PRIVATE);


        initAutoUpdateItem();
        initRocketItem();
        initShowAddreItem();
        initAddrStyle();
        initAddrPosition();
        initInterceptBlack();
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
        mSettingShowAddr = (SettingItemView) findViewById(R.id.set_show_phone_addr);

        //这里根据当前服务是否运行来更新勾选框
        //TODO:添加开机自启服务
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

    private void initAddrStyle() {
        mSettingAddrStyle = (SettingSelectItemView) findViewById(R.id.set_show_phone_addr_style);
        styleIndex = mPref.getInt("addr_style", 0);
        mSettingAddrStyle.setDesc(addrStyles[styleIndex]);
        mSettingAddrStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAddrStyleDialog();
            }
        });
    }

    private void selectAddrStyleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("归属地提示框风格");
        builder.setIcon(R.drawable.jinshan_icon);
        builder.setSingleChoiceItems(addrStyles, styleIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSettingAddrStyle.setDesc(addrStyles[which]);
                mPref.edit().putInt("addr_style", which).commit();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("取消", null);
        builder.setCancelable(false);
        builder.show();
    }

    private void initAddrPosition() {
        mSettingAddrPosition = (SettingSelectItemView) findViewById(R.id.set_show_phone_addr_position);
        mSettingAddrPosition.setDesc("设置归属地提示框的显示位置");
        mSettingAddrPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, DragAddrPositionActivity.class));
            }
        });
    }

    private void initRocketItem() {
        mSettingRocket = (SettingItemView) findViewById(R.id.set_show_rocket);
        showRocket = ServiceStatusUtils.isServiceRunning(this, "com.zachaxy.safedefender.service.RocketService");
        mSettingRocket.setCheck(showRocket);

        mSettingRocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSettingRocket.isCheck()) {
                    mSettingRocket.setCheck(false);
                    stopService(new Intent(SettingActivity.this, RocketService.class));
                } else {
                    mSettingRocket.setCheck(true);
                    startService(new Intent(SettingActivity.this, RocketService.class));
                }
            }
        });
    }

    private void initInterceptBlack() {
        mInterceptBlack = (SettingItemView) findViewById(R.id.set_interception_black);
//        interceptBlack = mPref.getBoolean("intercept_black", true);
        //TODO:添加开机自启服务
        interceptBlack = ServiceStatusUtils.isServiceRunning(this, "com.zachaxy.safedefender.service.InterceptBlackService");

        mInterceptBlack.setCheck(interceptBlack);
        mInterceptBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterceptBlack.isCheck()) {
                    mInterceptBlack.setCheck(false);
                    stopService(new Intent(SettingActivity.this, InterceptBlackService.class));
                } else {
                    mInterceptBlack.setCheck(true);
                    startService(new Intent(SettingActivity.this, InterceptBlackService.class));
                }
            }
        });
    }
}
