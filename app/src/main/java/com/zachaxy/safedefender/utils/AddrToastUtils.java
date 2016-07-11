package com.zachaxy.safedefender.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.View;
import android.view.WindowManager;
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

        /*view = new TextView(context);
        view.setText(text);*/
        view = View.inflate(context, R.layout.toast_address, null);
        int styleIndex = context.getSharedPreferences("config", context.MODE_PRIVATE).getInt("addr_style", 0);
        view.setBackgroundResource(styles[styleIndex]);
        TextView address = (TextView) view.findViewById(R.id.tv_addr_view);
        address.setText(text);
        manager.addView(view, params);
    }

    public static void hide() {
        if (manager != null && view != null) {
            manager.removeView(view);
            view = null;
        }
    }
}
