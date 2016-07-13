package com.zachaxy.safedefender.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.zachaxy.safedefender.R;

public class RocketBackgroundActivity extends Activity {

    private ImageView ivTop, ivMiddle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rocket_background);

        ivTop = (ImageView) findViewById(R.id.iv_rocket_bg_bottom);
        ivMiddle = (ImageView) findViewById(R.id.iv_rocket_bg_middle);

        AlphaAnimation animation1 = new AlphaAnimation(0f, 1f);
        animation1.setDuration(1000);
        animation1.setFillAfter(true);
        ivTop.startAnimation(animation1);

        AlphaAnimation animation2 = new AlphaAnimation(0f, 1f);
        animation2.setDuration(1000);
        animation2.setFillAfter(true);
        ivMiddle.startAnimation(animation2);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1100);
    }
}
