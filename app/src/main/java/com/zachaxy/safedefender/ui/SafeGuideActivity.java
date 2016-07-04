package com.zachaxy.safedefender.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zachaxy.safedefender.R;
import com.zachaxy.safedefender.widget.SafeGuideViewPager;
import com.zachaxy.safedefender.widget.SettingItemView;

import java.util.ArrayList;
import java.util.List;


/***
 * 用于用户设置向导,绑定sim卡等操作
 * 用到的知识点:自定义ViewPager
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

    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_guide);
        mPref = getSharedPreferences("config", MODE_PRIVATE);
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
                System.out.println("未知?" + position + "<->" + positionOffset + "<->" + positionOffsetPixels);
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
                        mSafeGuidePages.setScrollable(true);
                        break;
                    case 1:
                        mGuideTip1.setImageResource(R.drawable.tip_focused);
                        if (((SettingItemView) mGuideView2.findViewById(R.id.guide_bind_sim)).isCheck()) {
                            ableScroll(1);
                        } else {
                            unableScroll(1);
                        }
                        break;
                    case 2:
                        mGuideTip2.setImageResource(R.drawable.tip_focused);
                        if (TextUtils.isEmpty(((EditText) mGuideView3.findViewById(R.id.et_safe_set_safecontact)).getText())) {
                            unableScroll(2);

                        } else {
                            ableScroll(2);
                        }
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
                mSafeGuidePages.setScrollable(true);
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
                mSafeGuidePages.setScrollable(true);
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
        final SettingItemView bindSIM = (SettingItemView) mGuideView2.findViewById(R.id.guide_bind_sim);
        String sim = mPref.getString("SimSerial", null);
        //先初始化布局,判断是否绑定了sim卡,如果未绑定过sim卡,那么设置为未勾选状态,如果设置过,那么设置为勾选状态.
        if (TextUtils.isEmpty(sim)) {
            bindSIM.setCheck(false);
            unableScroll(1);
        } else {
            bindSIM.setCheck(true);
            ableScroll(1);
        }
        bindSIM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bindSIM.isCheck()) {
                    bindSIM.setCheck(false);
                    mPref.edit().remove("SimSerial").commit();
                    unableScroll(1);
                } else {
                    bindSIM.setCheck(true);
                    //这里还要保存sim卡的信息
                    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String simSerial = tm.getSimSerialNumber();
                    System.out.println("sim卡的序列号是:" + simSerial);
                    mPref.edit().putString("SimSerial", simSerial).commit();
                    ableScroll(1);
                }
            }
        });
    }

    private void initGuide3() {
        EditText mSafeNumber = (EditText) mGuideView3.findViewById(R.id.et_safe_set_safecontact);
        mSafeNumber.setText(mPref.getString("safe_number", ""));
        mSafeNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println("afterTextChanged" + s.toString());
                if (TextUtils.isEmpty(s.toString())) {
                    unableScroll(2);
                } else {
                    ableScroll(2);
                }
                mPref.edit().putString("safe_number", s.toString()).commit();
            }
        });
        Button mSelectContact = (Button) mGuideView3.findViewById(R.id.btn_safe_select_safecontact);
        mSelectContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SafeGuideActivity.this, ContactActivity.class), 1);
            }
        });
    }

    private void initGuide4() {
        Button finishSet;
        finishSet = (Button) mGuideView4.findViewById(R.id.btn_safe_set_finish);
        finishSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SafeGuideActivity.this, LostFindActivity.class));
                finish();
                //TODO:标记设置完成,下次就不再显示引导页了,此功能暂时不开放
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String number = data.getStringExtra("number");
                    ((EditText) mGuideView3.findViewById(R.id.et_safe_set_safecontact)).setText(number);
                    if (!TextUtils.isEmpty(number)) {
                        mPref.edit().putString("safe_number", number).commit();
                        ableScroll(2);
                    } else {
                        unableScroll(2);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void unableScroll(int index) {
        if (mSafeGuidePages.getCurrentItem() == index) {
            mSafeGuidePages.setScrollable(false);
            mSafeGuidePages.setIndex(index);
        }

        switch (index) {
            case 1:
                mGuideTip2.setEnabled(false);
            case 2:
                mGuideTip3.setEnabled(false);
                break;
            default:
                break;
        }
    }

    private void ableScroll(int index) {
        if (mSafeGuidePages.getCurrentItem() == index){
            mSafeGuidePages.setScrollable(true);
        }
        switch (index) {
            case 1:
                mGuideTip2.setEnabled(true);
            case 2:
                mGuideTip3.setEnabled(true);
                break;
            default:
                break;
        }
    }
}
