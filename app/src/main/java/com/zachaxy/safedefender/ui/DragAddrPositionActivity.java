package com.zachaxy.safedefender.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.SystemClock;
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

    private RelativeLayout mLayout;
    private TextView mTop, mBottom;
    private ImageView mDrag;
    private int startX, startY;
    private SharedPreferences mPref;
    private int windowWidth, windowHeight;
    private int centerX, centerY;
    private long[] mHint = new long[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_addr_position);


        mLayout = (RelativeLayout) findViewById(R.id.rl_at_drag);
        mTop = (TextView) findViewById(R.id.tv_position_top);
        mBottom = (TextView) findViewById(R.id.tv_position_bottom);
        mDrag = (ImageView) findViewById(R.id.iv_drag);
        mPref = getSharedPreferences("config", MODE_PRIVATE);

        windowWidth = getWindowManager().getDefaultDisplay().getWidth();
        windowHeight = getWindowManager().getDefaultDisplay().getHeight();

        System.out.println(windowHeight + "  " + windowWidth);
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

        if (last_y > windowHeight / 2) {
            mTop.setVisibility(View.VISIBLE);
            mBottom.setVisibility(View.INVISIBLE);
        } else {
            mTop.setVisibility(View.INVISIBLE);
            mBottom.setVisibility(View.VISIBLE);
        }

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

                        if (left < 0 | right > windowWidth) {
                            break;
                        }
                        if (top < 0 | bottom > windowHeight - 30) {
                            break;
                        }

                        if (top > windowHeight / 2) {
                            mTop.setVisibility(View.VISIBLE);
                            mBottom.setVisibility(View.INVISIBLE);
                        } else {
                            mTop.setVisibility(View.INVISIBLE);
                            mBottom.setVisibility(View.VISIBLE);
                        }
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
                //要返回false,以便让其响应点击事件
                return false;
            }
        });

        //双击将图片居中
        mDrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHint, 1, mHint, 0, mHint.length - 1);
                mHint[mHint.length - 1] = SystemClock.uptimeMillis();
                if (mHint[0] >= (SystemClock.uptimeMillis() - 500)) {
                    mDrag.layout(windowWidth / 2 - mDrag.getWidth() / 2, mDrag.getTop(), windowWidth / 2 + mDrag.getWidth() / 2, mDrag.getBottom());
                    mPref.edit().putInt("last_x", windowWidth / 2 - mDrag.getWidth() / 2).commit();
                }
            }
        });
    }
}
