package com.zachaxy.safedefender.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

/**
 * Created by zhangxin on 2016/7/6.
 * 获取经纬杜坐标的service
 */
public class LocationService extends Service {

    private SharedPreferences mPref;
    private LocationManager lm;
    private MyLocationListener myLocationListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPref = getSharedPreferences("config", MODE_PRIVATE);
        myLocationListener = new MyLocationListener();
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //设置标准
        Criteria criteria = new Criteria();
        //允许付费
        criteria.setCostAllowed(true);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String bestProvider = lm.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //位置提供者,最短更新时间,最短更新距离,监听器
        lm.requestLocationUpdates(bestProvider, 60, 50, myLocationListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.removeUpdates(myLocationListener);
    }

    class MyLocationListener implements LocationListener {

        //位置发生变化
        @Override
        public void onLocationChanged(Location location) {
            double j = location.getLongitude();
            double w = location.getLatitude();

            mPref.edit().putString("location","j:"+j+" ; w:"+w).commit();
            stopSelf();  //停掉service
        }

        //provider 能/不能 接收位置信息.
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        //当提供者可用,打开gps时回调该方法.
        @Override
        public void onProviderEnabled(String provider) {

        }

        //当提供者不可用,关闭gps时回调该方法.
        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
