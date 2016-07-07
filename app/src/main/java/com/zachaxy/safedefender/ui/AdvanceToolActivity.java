package com.zachaxy.safedefender.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.zachaxy.safedefender.R;

public class AdvanceToolActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_tool);
    }

    public void queryOwnership(View v) {
        startActivity(new Intent(this,AddressActivity.class));
    }

    public void queryCommonpNumber(View v) {

    }

    public void backupMSG(View v) {

    }

    public void lockApp(View v) {

    }
}
