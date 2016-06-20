package com.zachaxy.safedefender.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.zachaxy.safedefender.R;
import com.zachaxy.safedefender.bean.UpdateInfo;
import com.zachaxy.safedefender.utils.StringUtils;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends Activity {

    private TextView mAppVersion;
    private UpdateInfo mUpdateInfo;
    private Handler mHandler;

    protected static final int CODE_UPDATE_DIALOG = 0;
    protected static final int CODE_URL_ERROR = 1;
    protected static final int CODE_NET_ERROR = 2;
    protected static final int CODE_JSON_ERROR = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        mAppVersion = (TextView) findViewById(R.id.tv_verdsion);
        mAppVersion.setText("版本号:" + getLocalVersionName());
        checkVersion();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CODE_UPDATE_DIALOG:
                        showUpdateDialog();
                        break;
                    case CODE_URL_ERROR:
                        Toast.makeText(SplashActivity.this, "下载链接错误", Toast.LENGTH_LONG).show();
                        enterHome();
                        break;
                    case CODE_NET_ERROR:
                        Toast.makeText(SplashActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
                        enterHome();
                        break;
                    case CODE_JSON_ERROR:
                        Toast.makeText(SplashActivity.this, "数据解析失败", Toast.LENGTH_LONG).show();
                        enterHome();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private String getLocalVersionName() {
        PackageManager packageManager = getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            //没有找到相关的报名.因此这里建议使用getPackageName()方法,而不是手动写入包名字符串
            e.printStackTrace();
        }
        String versionName = packageInfo.versionName;
        return versionName;
    }

    private int getLocalVersionCode() {
        PackageManager packageManager = getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            //没有找到相关的报名.因此这里建议使用getPackageName()方法,而不是手动写入包名字符串
            e.printStackTrace();
        }
        int versionCode = packageInfo.versionCode;
        return versionCode;
    }


    private void checkVersion() {
        //注意启动子线程进行异步加载
        new Thread() {
            @Override
            public void run() {
                InputStream in = null;
                HttpURLConnection conn = null;
                try {
                    URL url = new URL("http://10.0.2.2/update.json");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");  //设置请求方法
                    conn.setConnectTimeout(5000);  //设置连接超时
                    conn.setReadTimeout(5000);     //设置读取超时,读文件不得超过3s,因为update.json仅有五条数据
                    //conn.connect();

                    int responseCode = conn.getResponseCode();
                    Log.d("###", "run: here");
                    if (responseCode == 200) {
                        in = conn.getInputStream();
                        String result = StringUtils.readFromStream(in);
                        Log.d("###", "run: " + result);
                        mUpdateInfo = StringUtils.parseJsonWithJSONObject(result);
                        //判断是否有新版本出现.
                        if (mUpdateInfo.getVersionCode() > getLocalVersionCode()) {
                            mHandler.sendEmptyMessage(CODE_UPDATE_DIALOG);
                        }
                    }
                } catch (MalformedURLException e) {
                    mHandler.sendEmptyMessage(CODE_JSON_ERROR);
                    e.printStackTrace();
                } catch (IOException e) {
                    mHandler.sendEmptyMessage(CODE_NET_ERROR);
                    e.printStackTrace();
                } catch (JSONException e) {
                    mHandler.sendEmptyMessage(CODE_JSON_ERROR);
                    e.printStackTrace();

                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                        if (conn != null) {
                            conn.disconnect();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /***
     * 弹出升级对话框
     */
    private void showUpdateDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("正在下载最新版本" + mUpdateInfo.getVersionName());
        dialog.setMessage(mUpdateInfo.getDescription());
        dialog.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.setNegativeButton("稍后提醒", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });
        dialog.show();
    }

    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        //进入主页面,把欢迎页面finish掉,防止用户点击返回键时,在回到该页面
        finish();
    }
}
