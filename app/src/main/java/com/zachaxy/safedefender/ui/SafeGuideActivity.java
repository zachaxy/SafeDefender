package com.zachaxy.safedefender.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zachaxy.safedefender.R;

import java.util.ArrayList;
import java.util.List;

public class SafeGuideActivity extends Activity {

    private TextView mSafeGuideTop;
    private ViewPager mSafeGuidePages;
    private LinearLayout mSafeGuideBottom;

    private PagerAdapter mSafeGuideAdapter;
    private List<View> mGuideViews;//view数组
    private View mGuideView1, mGuideView2, mGuideView3, mGuideView4;

    private ImageView mGuideTip0, mGuideTip1, mGuideTip2, mGuideTip3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_guide);
        initViews();
        initEvents();
    }

    private void initViews() {
        mSafeGuideTop = (TextView) findViewById(R.id.tv_safe_guide_top);
        mSafeGuidePages = (ViewPager) findViewById(R.id.vp_safe_guides);
        mSafeGuideBottom = (LinearLayout) findViewById(R.id.ll_set_guide_bottom);

        mGuideTip0 = (ImageView) findViewById(R.id.img_safe_guide_tip0);
        mGuideTip1 = (ImageView) findViewById(R.id.img_safe_guide_tip1);
        mGuideTip2 = (ImageView) findViewById(R.id.img_safe_guide_tip2);
        mGuideTip3 = (ImageView) findViewById(R.id.img_safe_guide_tip3);

    }

    private void initEvents() {
        LayoutInflater inflater = getLayoutInflater();
        mGuideView1 = inflater.inflate(R.layout.guideview1, null);
        mGuideView2 = inflater.inflate(R.layout.guideview2, null);
        mGuideView3 = inflater.inflate(R.layout.guideview3, null);
        mGuideView4 = inflater.inflate(R.layout.guideview4, null);

        mGuideViews = new ArrayList<>();
        mGuideViews.add(mGuideView1);
        mGuideViews.add(mGuideView2);
        mGuideViews.add(mGuideView3);
        mGuideViews.add(mGuideView4);

        mSafeGuideAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return mGuideViews.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(mGuideViews.get(position));
                return mGuideViews.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mGuideViews.get(position));
            }
        };

        mSafeGuidePages.setAdapter(mSafeGuideAdapter);


        mSafeGuidePages.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int index = mSafeGuidePages.getCurrentItem();
                resetGuideImg();
                switch (index){
                    case 0:
                        mGuideTip0.setImageResource(R.drawable.tip_focused);
                        break;
                    case 1:
                        mGuideTip1.setImageResource(R.drawable.tip_focused);
                        break;
                    case 2:
                        mGuideTip2.setImageResource(R.drawable.tip_focused);
                        break;
                    case 3:
                        mGuideTip3.setImageResource(R.drawable.tip_focused);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    public void changeGuidePage(View v) {
        resetGuideImg();
        switch (v.getId()) {
            case R.id.img_safe_guide_tip0:
                mSafeGuidePages.setCurrentItem(0);
                mGuideTip0.setImageResource(R.drawable.tip_focused);
                break;
            case R.id.img_safe_guide_tip1:
                mSafeGuidePages.setCurrentItem(1);
                mGuideTip1.setImageResource(R.drawable.tip_focused);
                break;
            case R.id.img_safe_guide_tip2:
                mSafeGuidePages.setCurrentItem(2);
                mGuideTip2.setImageResource(R.drawable.tip_focused);
                break;
            case R.id.img_safe_guide_tip3:
                mSafeGuidePages.setCurrentItem(3);
                mGuideTip3.setImageResource(R.drawable.tip_focused);
                break;

            default:
                break;
        }
    }

    private void resetGuideImg() {
        mGuideTip0.setImageResource(R.drawable.tip_unfocused);
        mGuideTip1.setImageResource(R.drawable.tip_unfocused);
        mGuideTip2.setImageResource(R.drawable.tip_unfocused);
        mGuideTip3.setImageResource(R.drawable.tip_unfocused);
    }
}
