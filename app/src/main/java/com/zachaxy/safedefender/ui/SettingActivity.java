package com.zachaxy.safedefender.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.zachaxy.safedefender.R;
import com.zachaxy.safedefender.widget.SettingItemView;

public class SettingActivity extends Activity {

    private SharedPreferences mPref;
    private SettingItemView mSettingItemView;
    private boolean autoUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mPref = getSharedPreferences("config", MODE_PRIVATE);

        mSettingItemView = (SettingItemView) findViewById(R.id.set_autoupdate);
        autoUpdate = mPref.getBoolean("auto_update", true);
        mSettingItemView.setCheck(autoUpdate);
        mSettingItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSettingItemView.isCheck()){
                    mSettingItemView.setCheck(false);
                    mPref.edit().putBoolean("auto_update",false).commit();
                }else{
                    mSettingItemView.setCheck(true);
                    mPref.edit().putBoolean("auto_update",true).commit();
                }
            }
        });
    }
}
