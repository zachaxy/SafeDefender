package com.zachaxy.safedefender.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zachaxy.safedefender.R;

public class DragAddrPositionActivity extends Activity {

    private TextView mTop, mBottom;
    private ImageView mDrag;
    private int startX, startY;
    private SharedPreferences mPref;

    private int centerX, centerY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_addr_position);


        mTop = (TextView) findViewById(R.id.tv_position_top);
        mBottom = (TextView) findViewById(R.id.tv_position_bottom);
        mDrag = (ImageView) findViewById(R.id.iv_drag);
        mPref = getSharedPreferences("config", MODE_PRIVATE);

        if (!mPref.contains("center_x")) {
            centerX = (((RelativeLayout) mDrag.getParent()).getWidth() - mDrag.getWidth()) / 2;
            mPref.edit().putInt("center_x", centerX).commit();
            System.out.println("c_x: " + centerX);
        }

        if (!mPref.contains("center_y")) {
            centerY = (((RelativeLayout) mDrag.getParent()).getHeight() - mDrag.getWidth()) / 2;
            mPref.edit().putInt("center_y", centerY).commit();
            System.out.println("c_y: " + centerY);
        }

        int last_x = mPref.getInt("last_x", centerX);
        int last_y = mPref.getInt("last_y", centerY);

        //注意布局绘制的步骤:onMeasure(测量)->onLayout(安放位置)->onDraw(绘制)
        //因为还没有测量完毕,所以就不能安放位置
        //mDrag.layout(last_x, last_y, last_x + mDrag.getWidth(), last_y + mDrag.getHeight());
        //因此使用另一种方法,注意LayoutParams所在的包!!!view所在的父标签是什么就转为什么
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mDrag.getLayoutParams();
        //params.setMargins(last_x, last_y, last_x + mDrag.getWidth(), last_y + mDrag.getHeight());
        params.leftMargin = last_x;
        params.topMargin = last_y;
        mDrag.setLayoutParams(params);

        mDrag.setOnTouchListener(new View.OnTouchListener() {
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

                        int left = mDrag.getLeft() + dx;
                        int right = mDrag.getRight() + dx;
                        int top = mDrag.getTop() + dy;
                        int bottom = mDrag.getBottom() + dy;
                        //注:是手势先移动到所需的位置,然后图片才跟着移动过去,在还为移动前,mDrag.getLeft()获得的是手势移动前的位置.
                        mDrag.layout(left, top, right, bottom);

                        startX = endX;
                        startY = endY;
                        break;
                    case MotionEvent.ACTION_UP:
                       /* mPref.edit().putInt("last_x", mDrag.getLeft()).commit();
                        mPref.edit().putInt("last_y", mDrag.getTop()).commit();
*/
                        SharedPreferences.Editor edit = mPref.edit();
                        edit.putInt("last_x", mDrag.getLeft());
                        edit.putInt("last_y", mDrag.getTop());
                        edit.commit();
                        break;
                    default:
                        break;
                }
                //表示消费了该触摸事件
                return true;
            }
        });
    }

  /*  @Override
    public void onBackPressed() {
        super.onBackPressed();
        //finish();
    }*/
}
