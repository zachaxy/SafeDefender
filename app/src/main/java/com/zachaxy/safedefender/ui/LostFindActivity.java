package com.zachaxy.safedefender.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zachaxy.safedefender.R;

public class LostFindActivity extends Activity {


    TextView mSafeNumber;
    ImageView mIsProtect;
    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_find);

        mPref = getSharedPreferences("config", MODE_PRIVATE);

        mSafeNumber = (TextView) findViewById(R.id.tv_lf_safeNumber);
        mSafeNumber.setText(mPref.getString("safe_number", ""));
        mIsProtect = (ImageView) findViewById(R.id.ibtn_lf_isOpenProtect);
        if (mPref.getBoolean("protect",false)){
            mIsProtect.setImageResource(R.drawable.lock);
        }else {
            mIsProtect.setImageResource(R.drawable.unlock);
        }
    }


    /***
     * 为textview设置的监听,重新进入设置向导
     *
     * @param v
     */
    public void reEnterSafeGuide(View v) {
        startActivity(new Intent(this, SafeGuideActivity.class));
        finish();

        //两个界面的切换动画,后者进入动画,前者退出动画
        //进入的动画是从右向左,退出的动画是从左向右
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }
}
