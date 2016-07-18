package com.zachaxy.safedefender.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zachaxy.safedefender.R;
import com.zachaxy.safedefender.bean.AppInfo;
import com.zachaxy.safedefender.utils.AppInfoUtils;

import java.util.List;

public class AppManagerActivity extends Activity {

    TextView tvRom, tvSD, tvAppType;
    ListView lv_app;
    LinearLayout mWaitAppListProgress;
    List<AppInfo> mAppInfoList;
    AppManagerAdapter adapter;

    int usrAppCount, sysAppCount;

    //---------
    boolean appTpyeChangeFlag = false; //滑动过程中状态转换的标志,只有状态改变了才允许修改文字;
    int currentType = 1, oldType = 1;

    PopupWindow popupWindow;
    AppInfo clickAppItem;

    SharedPreferences mPref;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    adapter = new AppManagerAdapter();
                    lv_app.setAdapter(adapter);
                    mWaitAppListProgress.setVisibility(View.INVISIBLE);
                    tvAppType.setVisibility(View.VISIBLE);
                    tvAppType.setText("用户程序:" + usrAppCount);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        initView();
        initData();
    }

    private void initView() {
        tvRom = (TextView) findViewById(R.id.tv_am_rom_available);
        tvSD = (TextView) findViewById(R.id.tv_am_sd_available);
        tvAppType = (TextView) findViewById(R.id.tv_app_type);
        lv_app = (ListView) findViewById(R.id.lv_am_apps);
        mWaitAppListProgress = (LinearLayout) findViewById(R.id.ll_wait_applist);


        long romAvailable = Environment.getDataDirectory().getFreeSpace();
        long sdAvailable = Environment.getExternalStorageDirectory().getFreeSpace();

        //格式化可用空间
        tvRom.setText(Formatter.formatFileSize(this, romAvailable));
        tvSD.setText(Formatter.formatFileSize(this, sdAvailable));

        lv_app.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                dismissPopupWindow();

                oldType = currentType;

                if (firstVisibleItem < usrAppCount) {
                    //用户app
                    currentType = 1;
                } else {
                    //系统app
                    currentType = 2;
                }

                if (oldType != currentType) {
                    appTpyeChangeFlag = true;
                } else {
                    appTpyeChangeFlag = false;
                }


                if (appTpyeChangeFlag) {
                    if (currentType == 1) {
                        //用户app
                        tvAppType.setText("用户程序:" + usrAppCount);
                    } else {
                        //系统app
                        tvAppType.setText("系统程序:" + sysAppCount);
                    }
                }
            }
        });

        lv_app.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = lv_app.getItemAtPosition(position);
                if (obj != null && obj instanceof AppInfo && ((AppInfo) obj).getAppType() == 1) {
                    dismissPopupWindow();

                    clickAppItem = (AppInfo) obj;
                    View contentView = View.inflate(AppManagerActivity.this, R.layout.app_item_popup_window, null);
                    LinearLayout appUninstall = (LinearLayout) contentView.findViewById(R.id.popup_app_uninstall);
                    LinearLayout appRun = (LinearLayout) contentView.findViewById(R.id.popup_app_run);
                    LinearLayout appShare = (LinearLayout) contentView.findViewById(R.id.popup_app_share);

                    appUninstall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent localIntent = new Intent("android.intent.action.DELETE", Uri.parse("pacKage"+clickAppItem.getPackageName()));
                            AppManagerActivity.this.startActivityForResult(localIntent,0);
                            //popupWindow.dismiss();
                        }
                    });

                    appRun.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent localIntent = AppManagerActivity.this.getPackageManager().getLaunchIntentForPackage(clickAppItem.getPackageName());
                            AppManagerActivity.this.startActivity(localIntent);
                            popupWindow.dismiss();
                        }
                    });

                    appShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent localIntent = new Intent("android.intent.action.SEND");
                            localIntent.setType("text/plain");
                            localIntent.putExtra("android.intent.extra.SUBJECT", "f分享");
                            localIntent.putExtra("android.intent.extra.TEXT", "Hi,推荐你使用安全卫士,保护你的手机");
                            AppManagerActivity.this.startActivity(Intent.createChooser(localIntent, "分享"));
                            popupWindow.dismiss();
                        }
                    });


                    popupWindow = new PopupWindow(contentView, -2, -2);

                    //需要注意的是:使用popupwindow,必须设置背景,不然动画效果不能展示
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    //传入一个长度为2的数组,将返回该view距离屏幕x,y的距离
                    int[] location = new int[2];
                    view.getLocationInWindow(location);
                    popupWindow.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, 70, location[1] - 5);

                    ScaleAnimation animation = new ScaleAnimation(0.5f, 1f, 0.5f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setDuration(300);
                    contentView.startAnimation(animation);
                }
            }
        });
    }

    private void initData() {
        mPref = getSharedPreferences("config", MODE_PRIVATE);
        new Thread() {
            @Override
            public void run() {
                mAppInfoList = AppInfoUtils.getAllApps(AppManagerActivity.this);
                usrAppCount = mPref.getInt("user_app_count", 0);
                sysAppCount = mPref.getInt("sys_app_count", 0);
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    class AppManagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mAppInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            if (getItemViewType(position) == 0) {
                return null;
            }
            return mAppInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (getItemViewType(position) == 0) {
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);

                if (position == 0) {
                    textView.setText("用户程序:" + usrAppCount);
                } else {
                    textView.setText("系统程序:" + sysAppCount);
                }

                return textView;
            }

            //-----------------------------------
            AppInfo appInfo = mAppInfoList.get(position);
            View view;
            AppViewHolder viewHolder;

            if (convertView != null && convertView instanceof LinearLayout) {
                view = convertView;
                viewHolder = (AppViewHolder) view.getTag();
            } else {
                //convertView不为null,或者有缓存,但是缓存的是TextView并不是LinearLayout,那么都需要重新绘制
                view = View.inflate(AppManagerActivity.this, R.layout.app_item, null);
                viewHolder = new AppViewHolder();
                viewHolder.icon = (ImageView) view.findViewById(R.id.img_app_icon);
                viewHolder.name = (TextView) view.findViewById(R.id.tv_app_name);
                viewHolder.location = (TextView) view.findViewById(R.id.tv_app_location);
                viewHolder.size = (TextView) view.findViewById(R.id.tv_app_size);
                view.setTag(viewHolder);
            }

            viewHolder.icon.setImageDrawable(appInfo.getIcon());
            viewHolder.name.setText(appInfo.getName());
            viewHolder.size.setText(appInfo.getSize());

            if (getItemViewType(position) == 1) {
                viewHolder.location.setText(appInfo.isRom() ? "手机内存" : "SD卡");
            } else if (getItemViewType(position) == 2) {
                viewHolder.location.setText("");
            }
            return view;
        }

        @Override
        public int getItemViewType(int position) {
            return mAppInfoList.get(position).getAppType();
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }
    }

    class AppViewHolder {
        ImageView icon;
        TextView name;
        TextView location;
        TextView size;
    }

    private void dismissPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }


    @Override
    protected void onDestroy() {
        dismissPopupWindow();
        super.onDestroy();
    }
}
