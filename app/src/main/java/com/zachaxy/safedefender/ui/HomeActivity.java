package com.zachaxy.safedefender.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.zachaxy.safedefender.utils.EncryptUtils;


/***
 * 主界面,展示本应用程序的九大功能模块
 * 使用GridView
 * (0)当用户点击了手机防盗页面,如果是第一次点击,需要设置用户密码,确认后会进入到用户导航界面.
 * 如果已经设置过密码,并且进行过用户设置,那么直接跳转到LostAndFound页面
 * 如果未进行过设置,跳转到SafeGuideActivity界面.
 * (8)进入SettingActivity界面,进行程序设置
 */
public class HomeActivity extends Activity {

    private GridView mFuncList;
    private SharedPreferences mPref;

    private final String SECREtKEY = "dashenshouhuweishi";

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
                    case 1:
                        startActivity(new Intent(HomeActivity.this, CallMsgSafeActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(HomeActivity.this, AppManagerActivity.class));
                        break;
                    case 7:
                        startActivity(new Intent(HomeActivity.this, AdvanceToolActivity.class));
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
                    if (EncryptUtils.encrypt(s1, SECREtKEY).equals(mPref.getString("password", null))) {
                        Toast.makeText(HomeActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        //直接登录,那么肯定已经设置过密码了,所以不进入引导页,而是直接进入页面
                        //上述逻辑错误,设置过密码还要看是否设置过向导,向导没有完成,还是要再次进入向导.
                        boolean configed = mPref.getBoolean("configed", false);
                        if (configed) {
                            startActivity(new Intent(HomeActivity.this, LostFindActivity.class));
                        } else {
                            startActivity(new Intent(HomeActivity.this, SafeGuideActivity.class));
                        }
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
                        mPref.edit().putString("password", EncryptUtils.encrypt(s1, SECREtKEY)).commit();
                        dialog.dismiss();
                        //第一次设置完密码,跳转到引导界面
                        startActivity(new Intent(HomeActivity.this, SafeGuideActivity.class));
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

