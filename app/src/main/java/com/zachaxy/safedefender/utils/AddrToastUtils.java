package com.zachaxy.safedefender.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by zhangxin on 2016/7/11.
 */
public class AddrToastUtils {

    private static WindowManager manager;
    private static TextView view;

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

        view = new TextView(context);
        view.setText(text);
        manager.addView(view, params);
    }

    public static void hide() {
        if (manager != null && view != null) {
            manager.removeView(view);
            view = null;
        }
    }
}
