package com.zachaxy.safedefender.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zachaxy.safedefender.R;

public class HomeActivity extends Activity {

    private GridView mFuncList;
    private SharedPreferences mPref;

    private String[] mFuncDesc = {
            "手机防盗",
            "通信卫士",
            "软件管理",
            "进程管理",
            "流量统计",
            "手机杀毒",
            "缓存清理",
            "高级工具",
            "设置中心"};

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
        mPref = getSharedPreferences("config", MODE_PRIVATE);
        mFuncList = (GridView) findViewById(R.id.gv_functions);
        mFuncList.setAdapter(new FuncAdapter());
        mFuncList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        showSafePasswordDialog();
                        break;
                    case 8:
                        startActivity(new Intent(HomeActivity.this, SettingActivity.class));
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void showSafePasswordDialog() {
        String savedpwd = mPref.getString("password", null);
        if (TextUtils.isEmpty(savedpwd)) {
            showSafeSetPasswordDialog();
        } else {
            showSafeLoginPasswordDialog();
        }
    }

    private void showSafeLoginPasswordDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_login_password, null);
        final AlertDialog dialog = builder.create();
        final EditText et1 = (EditText) view.findViewById(R.id.et_safe_login_pwd);
        Button btn1 = (Button) view.findViewById(R.id.btn_safe_login_ok);
        Button btn2 = (Button) view.findViewById(R.id.btn_safe_login_cancel);
        dialog.setView(view);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s1 = et1.getText().toString();
                if (!TextUtils.isEmpty(s1)) {
                    if (s1.equals(mPref.getString("password", null))) {
                        Toast.makeText(HomeActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(HomeActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    private void showSafeSetPasswordDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_set_password, null);

        final AlertDialog dialog = builder.create();
        final EditText et1 = (EditText) view.findViewById(R.id.et_safe_set_pwd);
        final EditText et2 = (EditText) view.findViewById(R.id.et_safe_set_repwd);
        Button btn1 = (Button) view.findViewById(R.id.btn_safe_set_ok);
        Button btn2 = (Button) view.findViewById(R.id.btn_safe_set_cancel);
        dialog.setView(view);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s1 = et1.getText().toString();
                String s2 = et2.getText().toString();
                if (!TextUtils.isEmpty(s1) && !TextUtils.isEmpty(s2)) {
                    if (s1.equals(s2)) {
                        mPref.edit().putString("password", s1).commit();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(HomeActivity.this, "两次输入的密码不同,请重新设置密码", Toast.LENGTH_SHORT).show();
                        et1.setText("");
                        et2.setText("");
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }


            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.show();

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

