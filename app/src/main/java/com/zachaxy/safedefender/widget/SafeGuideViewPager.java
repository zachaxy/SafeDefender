package com.zachaxy.safedefender.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * Created by zhangxin on 2016/6/24.
 * 自定义ViewPager,实现为设置当前页,禁止滑动到下一页的操作.
 * 所用到的知识点是:Android touch事件分发机制
 */
public class SafeGuideViewPager extends ViewPager {
    private float beforeX;

    private Context context;

    private int index;

    private String[] tipStr = {"0","请先绑定SIM卡","请先设置安全号码","3"};

    //如果这是为flase,那么不可以向右翻页
    private boolean isScrollable = true;

    private boolean once = true;

    public SafeGuideViewPager(Context context) {
        super(context);
        this.context = context;
    }

    public SafeGuideViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
      /*  if (getCurrentItem()==0){
            isScrollable = true;
        }*/
        if (!isScrollable) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //beforeX = ev.getX();
                    beforeX = ev.getRawX();
                    System.out.println("down---"+beforeX);
                    //System.out.println("beforeX:" + beforeX);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float currentX = ev.getRawX();
                    //float motionValue = ev.getX() - beforeX;
                    float motionValue = currentX - beforeX;
                    //System.out.println(beforeX+ "<---->" + ev.getX() );
                    System.out.println(beforeX + "<---->" + currentX);
                    if (currentX < 0) {
                        //System.out.println("发送终止触摸事件");
                        //这是系统的一个bug.
                        beforeX = 0;
                        //这里手动发送一个up的事件,供onInterceptTouchEvent(ev)方法来终止动作.
                        ev.setAction(MotionEvent.ACTION_UP);
                        //该函数会判断.如果此时动作是up,那么就结束此次动作!!!内部调用resetTouch()来重置触摸
                        //这样就停止响应后续的操作,直到下一次ACTION_DOWN
                        onInterceptTouchEvent(ev);
                    } else {
                        beforeX = currentX;
                    }
                    if (motionValue < 0) {
                        //表明想看下一张page.
                        //System.out.println("左滑");
                        if(once){
                            Toast.makeText(context,tipStr[index],Toast.LENGTH_SHORT).show();
                            once = false;
                        }
                        System.out.println(tipStr[index]);
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        onInterceptTouchEvent(ev);
                        //肯定要返回true,因为如果返回false,那么其他的MotionEvent_MOVE,UP等都不会被执行.
                        //这里直接返回了true,是不处理本次的动作move到up之间的逻辑.
                        //向左滑动到边缘时,得到的valueX可能是负数,应该是系统的问题.
                        return false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    System.out.println("up: " + beforeX);
                    beforeX = 0.0f;
                    once = true;
                    break;
                default:
                    break;
            }
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

    public void setScrollable(boolean scrollable){
        isScrollable = scrollable;
    }

    public boolean isScrollable() {
        return isScrollable;
    }

    public void setIndex(int i){
        index = i;
    }
}
