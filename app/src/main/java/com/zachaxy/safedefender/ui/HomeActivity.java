package com.zachaxy.safedefender.ui;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.zachaxy.safedefender.R;

public class HomeActivity extends Activity {

    private GridView mFuncList;

    private String[] mFuncDesc = {
            "手机防盗",
            "通信卫士",
            "软件管理",
            "进程管理",
            "流量统计",
            "手机杀毒",
            "缓存清理",
            "高级工具",
            "设置中心" };

    private int[] mFuncImg = {
            R.drawable.home_safe,
            R.drawable.home_callmsgsafe,
            R.drawable.home_apps,
            R.drawable.home_taskmanager,
            R.drawable.home_netmanager,
            R.drawable.home_trojan,
            R.drawable.home_sysoptimize,
            R.drawable.home_tools,
            R.drawable.home_settings};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mFuncList = (GridView) findViewById(R.id.gv_functions);
        mFuncList.setAdapter(new FuncAdapter());
    }


    class FuncAdapter extends BaseAdapter {

        //需要展示几个item
        @Override
        public int getCount() {
            return mFuncDesc.length;
        }

        //可以返回任意,null也可以
        @Override
        public Object getItem(int position) {
            return mFuncDesc[position];
        }

        //一般是返回position,注:该position和getView()中传入的position不是同一个
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(HomeActivity.this, R.layout.func_list_item, null);
            ImageView imgItem = (ImageView) view.findViewById(R.id.img_item_func);
            TextView tvItem = (TextView) view.findViewById(R.id.tv_item_func);

            imgItem.setImageResource(mFuncImg[position]);
            tvItem.setText(mFuncDesc[position]);
            return view;
        }
    }

}

