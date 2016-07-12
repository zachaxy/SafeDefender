package com.zachaxy.safedefender.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
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
    private static int[] styles = {
            R.drawable.call_locate_white,
            R.drawable.call_locate_orange,
            R.drawable.call_locate_blue,
            R.drawable.call_locate_gray,
            R.drawable.call_locate_green
    };

    public static void show(Context context, String text) {
        manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.setTitle("Toast");

        SharedPreferences mPref = context.getSharedPreferences("config", context.MODE_PRIVATE);
        view = View.inflate(context, R.layout.toast_address, null);
        int styleIndex = mPref.getInt("addr_style", 0);
        view.setBackgroundResource(styles[styleIndex]);

        if (mPref.contains("last_x") && mPref.contains("last_y")) {
            int last_x = mPref.getInt("last_x", 0);
            int last_y = mPref.getInt("last_y", 0);
            view.layout(last_x, last_y, last_x + view.getWidth(), last_y + view.getHeight());
        }

        TextView address = (TextView) view.findViewById(R.id.tv_addr_view);
        address.setText(text);
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
