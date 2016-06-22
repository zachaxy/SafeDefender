package com.zachaxy.safedefender.ui;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zachaxy.safedefender.R;
import com.zachaxy.safedefender.widget.SettingItemView;

public class SettingActivity extends Activity {

    private SettingItemView mSettingItemView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mSettingItemView = (SettingItemView) findViewById(R.id.set_autoupdate);

    }
}
