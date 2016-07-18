package com.zachaxy.safedefender.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zachaxy.safedefender.R;
import com.zachaxy.safedefender.bean.AppInfo;
import com.zachaxy.safedefender.utils.AppInfoUtils;

import java.util.List;

public class AppManagerActivity extends AppCompatActivity {

    TextView tvRom, tvSD;
    ListView lv_app;
    LinearLayout mWaitAppListProgress;
    List<AppInfo> mAppInfoList;
    AppManagerAdapter adapter;

    int usrAppCount, sysAppCount;

    SharedPreferences mPref;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    adapter = new AppManagerAdapter();
                    lv_app.setAdapter(adapter);
                    mWaitAppListProgress.setVisibility(View.INVISIBLE);
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
        lv_app = (ListView) findViewById(R.id.lv_am_apps);
        mWaitAppListProgress = (LinearLayout) findViewById(R.id.ll_wait_applist);


        long romAvailable = Environment.getDataDirectory().getFreeSpace();
        long sdAvailable = Environment.getExternalStorageDirectory().getFreeSpace();

        //格式化可用空间
        tvRom.setText(Formatter.formatFileSize(this, romAvailable));
        tvSD.setText(Formatter.formatFileSize(this, sdAvailable));
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
}
