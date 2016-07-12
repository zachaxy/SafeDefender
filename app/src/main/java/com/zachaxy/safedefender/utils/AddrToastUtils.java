package com.zachaxy.safedefender.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zachaxy.safedefender.R;

/**
 * Created by zhangxin on 2016/7/11.
 */
public class AddrToastUtils {

    private static WindowManager manager;
    private static View view;
    private static int startX, startY;
    private static SharedPreferences mPref;
    private static int windowWidth, windowHeight;
    private static int[] styles = {
            R.drawable.call_locate_white,
            R.drawable.call_locate_orange,
            R.drawable.call_locate_blue,
            R.drawable.call_locate_gray,
            R.drawable.call_locate_green
    };

    public static void show(Context context, String text) {
        manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowWidth = manager.getDefaultDisplay().getWidth();
        windowHeight = manager.getDefaultDisplay().getHeight();

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");

        mPref = context.getSharedPreferences("config", context.MODE_PRIVATE);
        view = View.inflate(context, R.layout.toast_address, null);
        int styleIndex = mPref.getInt("addr_style", 0);
        view.setBackgroundResource(styles[styleIndex]);

        if (mPref.contains("last_x") && mPref.contains("last_y")) {
            int last_x = mPref.getInt("last_x", 0);
            int last_y = mPref.getInt("last_y", 0);
            params.gravity = Gravity.LEFT + Gravity.TOP; //默认是居中的,现将其设置为左上角.
            params.x = last_x;
            params.y = last_y;
        }

        TextView address = (TextView) view.findViewById(R.id.tv_addr_view);
        address.setText(text);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();

                        int dx = endX - startX;
                        int dy = endY - startY;

                        //更新浮窗位置
                        params.x += dx;
                        params.y += dy;

                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.x > windowWidth - view.getWidth()) {
                            params.x = windowWidth - view.getWidth();
                        }
                        if (params.y > windowHeight - view.getHeight()) {
                            params.y = windowHeight - view.getHeight();
                        }

                        manager.updateViewLayout(view, params);
                        startX = endX;
                        startY = endY;
                        break;
                    case MotionEvent.ACTION_UP:
                        SharedPreferences.Editor edit = mPref.edit();
                        edit.putInt("last_x", params.x);
                        edit.putInt("last_y", params.y);
                        edit.commit();
                        break;
                }
                return false;
            }
        });
        manager.addView(view, params);
    }

    //电话活动结束后删除view布局
    public static void hide() {
        if (manager != null && view != null) {
            manager.removeView(view);
            view = null;
        }
    }
}
