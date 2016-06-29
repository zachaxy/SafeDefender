package com.zachaxy.safedefender.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zachaxy.safedefender.R;
import com.zachaxy.safedefender.widget.SafeGuideViewPager;

import java.util.ArrayList;
import java.util.List;


/***

 */
public class SafeGuideActivity extends Activity {

    private TextView mSafeGuideTop;
    private SafeGuideViewPager mSafeGuidePages;
    private LinearLayout mSafeGuideBottom;

    private PagerAdapter mSafeGuideAdapter;
    private List<View> mGuideViews;//view数组
    private View mGuideView1, mGuideView2, mGuideView3, mGuideView4;

    private ImageView mGuideTip0, mGuideTip1, mGuideTip2, mGuideTip3;

    private boolean isScrollable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_guide);
        initViews();
        initEvents();
    }

    private void initViews() {
        mSafeGuideTop = (TextView) findViewById(R.id.tv_safe_guide_top);
        mSafeGuidePages = (SafeGuideViewPager) findViewById(R.id.vp_safe_guides);
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
        initGuide2();

        mGuideView3 = inflater.inflate(R.layout.guideview3, null);
        initGuide3();

        mGuideView4 = inflater.inflate(R.layout.guideview4, null);
        initGuide4();

        mGuideViews = new ArrayList<>();
        mGuideViews.add(mGuideView1);
        mGuideViews.add(mGuideView2);
        mGuideViews.add(mGuideView3);
        mGuideViews.add(mGuideView4);

        //连接ViewPager与内部各个view的适配器
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

        //设置了adapter才能滑动换页.
        mSafeGuidePages.setAdapter(mSafeGuideAdapter);


        //添加一个page切换的监听器,以实时同步底部的dock栏
        mSafeGuidePages.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //System.out.println("未知?"+position+"<->"+positionOffset+"<->"+positionOffsetPixels);

            }

            /***
             * 当一个新的page滑动进来,传入其position,
             * 先判断是否设置过,然后在第二个索引处第三个索引处决定事都要置其为false;
             * 防止用户设置了第二页进入第三页后再次进入第三页,导致无法滑动.
             * @param position
             */
            @Override
            public void onPageSelected(int position) {
                //int index = mSafeGuidePages.getCurrentItem();
                // System.out.println("是不是一个索引:"+index+"<------>"+position);
                //经过测试这是一个索引,所以不再另外多生成一个index了
                resetGuideImg();
                switch (position) {
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


    /***
     * 点击bottom栏的tips,跳转到相应的page.在布局文件中已绑定.
     *
     * @param v
     */
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




    private void initGuide2() {

    }

    private void initGuide3() {

    }

    private void initGuide4() {
        Button finishSet;
        finishSet = (Button) mGuideView4.findViewById(R.id.btn_safe_set_finish);
        finishSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SafeGuideActivity.this,LostFindActivity.class));
                finish();
                //TODO:标记设置完成,下次就不再显示引导页了,此功能暂时不开放
            }
        });
    }
}
