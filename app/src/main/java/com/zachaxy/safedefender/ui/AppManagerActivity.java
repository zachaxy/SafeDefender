package com.zachaxy.safedefender.ui;

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
        new Thread() {
            @Override
            public void run() {
                mAppInfoList = AppInfoUtils.getAllApps(AppManagerActivity.this);
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
            return mAppInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AppInfo appInfo = mAppInfoList.get(position);

            View view;
            AppViewHolder viewHolder;

            if (convertView == null) {
                view = View.inflate(AppManagerActivity.this, R.layout.app_item, null);
                viewHolder = new AppViewHolder();
                viewHolder.icon = (ImageView) view.findViewById(R.id.img_app_icon);
                viewHolder.name = (TextView) view.findViewById(R.id.tv_app_name);
                viewHolder.location = (TextView) view.findViewById(R.id.tv_app_location);
                viewHolder.size = (TextView) view.findViewById(R.id.tv_app_size);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (AppViewHolder) view.getTag();
            }


            viewHolder.icon.setImageDrawable(appInfo.getIcon());
            viewHolder.name.setText(appInfo.getName());
            viewHolder.location.setText(appInfo.isRom() ? "手机内存" : "SD卡");
            viewHolder.size.setText(appInfo.getSize());
            return view;
        }

        class AppViewHolder {
            ImageView icon;
            TextView name;
            TextView location;
            TextView size;
        }
    }
}
