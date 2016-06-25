package com.zachaxy.safedefender.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by zhangxin on 2016/6/24.
 */
public class SafeGuideViewPager extends ViewPager {
    private float beforeX;
    private boolean isScrollable = false;

    public SafeGuideViewPager(Context context) {
        super(context);
    }

    public SafeGuideViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
     /*   if (isScrollable) {
            return super.dispatchTouchEvent(ev);
        } else {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    beforeX = ev.getX();
                    System.out.println("beforeX"+beforeX);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float motionValue = ev.getX() - beforeX;
                    System.out.println(ev.getX()+"<---->"+beforeX);
                    beforeX = ev.getX();
                    if (motionValue <= 0) {
                        //表明向左滑动,准备切换到下一个page,那么此时dispach掉touch事件
                        return true;
                    }
                    break;
                default:
                    break;
            }
            return super.dispatchTouchEvent(ev);
        }*/


        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //beforeX = ev.getX();
                beforeX = ev.getRawX();
                System.out.println("beforeX:" + beforeX);
                break;
            case MotionEvent.ACTION_MOVE:
                float currentX = ev.getRawX();
                //float motionValue = ev.getX() - beforeX;
                float motionValue = currentX - beforeX;
                //System.out.println(beforeX+ "<---->" + ev.getX() );
                System.out.println(beforeX + "<---->" + currentX);
                if (currentX < 0) {
                    System.out.println("发送终止触摸事件");
                    beforeX = 0;
                    ev.setAction(MotionEvent.ACTION_UP);
                    onInterceptTouchEvent(ev);
                } else {
                    beforeX = currentX;
                }
                if (motionValue <= 0) {
                    //表明向右滑动
                    System.out.println("左滑");
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("up: " + beforeX);
                beforeX = 0.0f;
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


   /* @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        System.out.println("终止触摸事件");
        return flag;

    }*/

 /*   @Override
    public boolean onTouchEvent(MotionEvent ev) {
        System.out.println(isScrollable);
        //return super.onTouchEvent(ev);
        switch (ev.getAction()) {
           *//* case MotionEvent.ACTION_DOWN:
                beforeX = ev.getX();
                System.out.println("beforeX"+beforeX);
                break;*//*
            case MotionEvent.ACTION_MOVE:
                float motionValue = ev.getX() - beforeX;
                System.out.println(ev.getX()+"<---->"+beforeX);
                beforeX = ev.getX();
                if (motionValue >= 0) {
                    //表明向右滑动
                    isScrollable = false;
                }else{
                    isScrollable = true;
                }
                break;
            default:
                break;
        }
        return isScrollable;


    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isScrollable == false) {
            return false;
        } else {
            return super.onInterceptTouchEvent(ev);
        }

    }*/

    public boolean isScrollable() {
        return isScrollable;
    }
}
