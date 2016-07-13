package com.zachaxy.safedefender.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.zachaxy.safedefender.R;
import com.zachaxy.safedefender.ui.RocketBackgroundActivity;

/**
 * Created by zhangxin on 2016/7/12.
 */
public class RocketService extends Service {
    private WindowManager manager;
    private View vRocket;
    private int startX, startY;
    private SharedPreferences mPref;
    private int windowWidth, windowHeight;
    private WindowManager.LayoutParams params;
    private ImageView ivRocket;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        show();
        System.out.println("screen height:" + windowHeight);
        System.out.println("screen width:" + windowWidth);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (manager != null && vRocket != null) {
            manager.removeView(vRocket);
            vRocket = null;
        }
    }

    private void init() {
        manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowWidth = manager.getDefaultDisplay().getWidth();
        windowHeight = manager.getDefaultDisplay().getHeight();
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.gravity = Gravity.LEFT + Gravity.TOP;
        params.setTitle("Rocket");

        mPref = getSharedPreferences("config", MODE_PRIVATE);
        vRocket = View.inflate(this, R.layout.rocket, null);
        ivRocket = (ImageView) vRocket.findViewById(R.id.iv_rocket);
        ivRocket.setBackgroundResource(R.drawable.anim_rocket);
        //为其设置一个逐帧动画...
        AnimationDrawable anim = (AnimationDrawable) ivRocket.getBackground();
        anim.start();
        manager.addView(vRocket, params);
    }

    public void show() {
        //TODO:为火箭配置一个坐标 x, y

        vRocket.setOnTouchListener(new View.OnTouchListener() {
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
                        if (params.x > windowWidth - vRocket.getWidth()) {
                            params.x = windowWidth - vRocket.getWidth();
                        }
                        if (params.y > windowHeight - vRocket.getHeight()) {
                            params.y = windowHeight - vRocket.getHeight();
                        }

                        manager.updateViewLayout(vRocket, params);
                        startX = endX;
                        startY = endY;
                        break;
                    case MotionEvent.ACTION_UP:
                       /* SharedPreferences.Editor edit = mPref.edit();
                        edit.putInt("last_x", params.x);
                        edit.putInt("last_y", params.y);
                        edit.commit();*/
                        System.out.println(params.x + "---" + params.y);
                        if (params.y > windowHeight - 250 && params.x > windowWidth / 2 - 100 && params.x < windowWidth / 2 + 100) {
                            //触发发射动画
                            System.out.println("发射动画");
                            sendRocket();

                            // 启动烟雾效果
                            Intent intent = new Intent(RocketService.this,
                                    RocketBackgroundActivity.class);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//从service启动一个Activity,需要启动一个栈来存放activity
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//目前效果最好的一种,缺点:自己也会被kill.
                            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);//无效,也许需要设置不同的taskaffinity
                            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_FROM_BACKGROUND);//从service启动一个Activity,需要启动一个栈来存放activity
                            startActivity(intent);
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

    }

    protected void sendRocket() {
        // 设置火箭居中
        params.x = windowWidth / 2 - vRocket.getWidth() / 2;
        manager.updateViewLayout(vRocket, params);
        int pos = windowHeight;// 移动总距离
        for (int i = 0; i <= 10; i++) {
            int y = pos - windowHeight / 10 * i;
            Message msg = Message.obtain();
            msg.arg1 = y;
            mHandler.sendMessageDelayed(msg, i * 50);
        }

        /*new Thread() {
            @Override
            public void run() {
                int pos = windowHeight;// 移动总距离
                for (int i = 0; i <= 10; i++) {

                    // 等待一段时间再更新位置,用于控制火箭速度
                    //TODO:不使用线程等待而是使用handler的delay机制
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    int y = pos - windowHeight/10 * i;

                    Message msg = Message.obtain();
                    msg.arg1 = y;

                    //mHandler.sendMessage(msg);
                    mHandler.sendMessageDelayed(msg,i*50);
                }
            }
        }.start();*/

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            // 更新火箭位置
            int y = msg.arg1;
            params.y = y;
            manager.updateViewLayout(vRocket, params);
        }
    };

}
