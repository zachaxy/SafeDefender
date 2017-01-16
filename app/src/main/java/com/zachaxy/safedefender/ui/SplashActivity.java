package com.zachaxy.safedefender.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zachaxy.safedefender.R;
import com.zachaxy.safedefender.bean.UpdateInfo;
import com.zachaxy.safedefender.utils.StringUtils;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends Activity {

    private RelativeLayout mActySplash;
    private TextView mAppVersion;
    private TextView mCountDownTV;

    private TextView mTVDownload;
    private ProgressBar mPBDownload;

    private UpdateInfo mUpdateInfo;

    private static final int CODE_UPDATE_DIALOG = 0;
    private static final int CODE_URL_ERROR = 1;
    private static final int CODE_NET_ERROR = 2;
    private static final int CODE_JSON_ERROR = 3;
    private static final int CODE_ENTER_HOME = 4;
    private static final int CODE_CHANGE_COUNTDOWN = 5;

    private boolean againFlag = true;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_UPDATE_DIALOG:
                    showUpdateDialog();
                    break;
                case CODE_URL_ERROR:
                    Toast.makeText(SplashActivity.this, "下载链接错误", Toast.LENGTH_LONG).show();
                    //enterHome();
                    break;
                case CODE_NET_ERROR:
                    Toast.makeText(SplashActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
                    // enterHome();
                    break;
                case CODE_JSON_ERROR:
                    Toast.makeText(SplashActivity.this, "数据解析失败", Toast.LENGTH_LONG).show();
                    // enterHome();
                    break;
                case CODE_CHANGE_COUNTDOWN:
                    String text = (String) msg.obj;
                    mCountDownTV.setText(text);
                    break;
                default:
                    enterHome();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash);

        mActySplash = (RelativeLayout) findViewById(R.id.acty_splash);
        mAppVersion = (TextView) findViewById(R.id.tv_verdsion);
        mAppVersion.setText("版本号:" + getLocalVersionName());

        mCountDownTV = (TextView) findViewById(R.id.tv_countDown);
        mTVDownload = (TextView) findViewById(R.id.tv_progress);
        mPBDownload = (ProgressBar) findViewById(R.id.pb_download);

        //拷贝归属地数据库
        copyDB("address.db");

        if (getSharedPreferences("config", MODE_PRIVATE).getBoolean("auto_update", true)) {
            checkVersion();
        } else {
            delayToCountDown(5);
        }

        //设置渐变的动画,范围是0.0~1.0,
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(5000);
        mActySplash.startAnimation(animation);
    }


    /***
     * 获取本地build.gradle中的版本名字
     *
     * @return
     */
    private String getLocalVersionName() {
        PackageManager packageManager = getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            //没有找到相关的包名.因此这里建议使用getPackageName()方法,而不是手动写入包名字符串
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 获取本地build.gradle中的版本号
     *
     * @return
     */
    private int getLocalVersionCode() {
        PackageManager packageManager = getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            //没有找到相关的报名.因此这里建议使用getPackageName()方法,而不是手动写入包名字符串
            e.printStackTrace();
        }
        return -1;
    }


    /***
     * 检测服务器端的版本号
     */
    private void checkVersion() {
        //注意启动子线程进行异步加载
        new Thread() {
            @Override
            public void run() {
                InputStream in = null;
                HttpURLConnection conn = null;
                try {
                    delayToCountDown(5);
                    URL url = new URL("http://10.0.2.2/update.json");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");  //设置请求方法
                    conn.setConnectTimeout(3000);  //设置连接超时
                    conn.setReadTimeout(3000);     //设置读取超时,读文件不得超过3s,因为update.json仅有五条数据

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
                    //delayToCountDown(5);
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
        //dialog.setCancelable(false); //尽量不用强制用户
        dialog.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO:立即更新需要下载,该功能还未实现.考虑后期添加bmob后台云服务?
            }
        });
        dialog.setNegativeButton("稍后提醒", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });

        //在弹出对话框后,点击back键盘进入这个方法,证明不感兴趣
        //优化体验:直接跳转到主页面
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });
        dialog.show();
    }

    private void enterHome() {
        mHandler.removeMessages(CODE_CHANGE_COUNTDOWN);
        mHandler.removeMessages(CODE_ENTER_HOME);
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        //进入主页面,把欢迎页面finish掉,防止用户点击返回键时,在回到该页面
        //但是之前发给Handler的消息是在另一个线程里面,还在处理消息(如:倒计时结束后进入主页面)
        finish();
    }


    /***
     * 该函数是点击右上角跳过广告后,直接进入主页面
     * 存在的问题是如果直接跳转进入主界面,那么之前子线程的计数器还在进行,倒计时0s后还是会在执行一次跳转到主界面
     * 解决方法:添加一个标志位?(x)方法不可取,因为在sendMessage的时候是一块发出去的,并不是间隔一秒再发的,只是处理的时候按照间隔一秒处理
     * 正确的方法是使用handler.removeMessage(int what)方法
     *
     * @param view
     */
    public void enterHome(View view) {
        enterHome();
    }


    private void delayToCountDown(int delayTime) {
        for (int i = delayTime; i > 0; i--) {
            Message msg = new Message();
            msg.what = CODE_CHANGE_COUNTDOWN;
            msg.obj = "" + i + "s";
            mHandler.sendMessageDelayed(msg, (delayTime - i) * 1000);
        }

        mHandler.sendEmptyMessageDelayed(CODE_ENTER_HOME, delayTime * 1000);
    }

    /***
     * 暂时不用此功能
     * 下载结束后跳转到系统的安装程序,由系统安装程序进行新版本程序的安装
     */
    private void onDownloadFinshed() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        //intent.setDataAndType(Uri.fromFile("下载的路径的file对象"),"application/vnd.android.package-archive")
        //如果用户取消安装,会返回结果,并调用回调方法onActivityResult()
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
    }

    //拷贝数据库,之前的数据是存放在asset目录下的,程序中无法直接访问,因此需要拷贝到用户目录下.
    //补充下:Android中还有另外一个文件夹raw,和assets差不多,也不会被R文件编译,但是raw下不能在建文件夹,assets文件下是可以在建文件夹的
    private void copyDB(String dbName) {
        //getFilesDir()--->data/data/com.zachaxy.safedefender/files/
        File destFile = new File(getFilesDir(), dbName);
        if (destFile.exists()) {
            return;
        }
        InputStream in = null;
        FileOutputStream out = null;
        try {
            in = getAssets().open("address.db");
            out = new FileOutputStream(destFile);
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
