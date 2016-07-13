package com.zachaxy.safedefender.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhangxin on 2016/7/13.
 * <p/>
 * Description :
 * 创建黑名单数据库的辅助类
 */
public class BlackListOpenHelper extends SQLiteOpenHelper {

    //id:自增长;number:拦截的电话号码;mode:拦截模式(电话拦截/短信拦截)
    public static final String CREATE_BLACKLIST = "create table balckList ( "
            + "id integer primary key autoincrement,"
            + "number varchar(20),"
            + "mode varchar(2) )";


    public BlackListOpenHelper(Context context) {
        super(context, "safe.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BLACKLIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
