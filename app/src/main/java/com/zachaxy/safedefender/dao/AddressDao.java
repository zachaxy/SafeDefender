package com.zachaxy.safedefender.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

/**
 * Created by zhangxin on 2016/7/7.
 * 归属地查询
 */
public class AddressDao {

    //注意改路径必须是data/data下的路径.否则数据库无法访问
    private static final String PATH = "data/data/com.zachaxy.safedefender/files/address.db";

    public static String getAddress(String number) {
        String address = "未知号码";
        SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
        if (number.matches("^1[3-8]\\d{9}$")) {
            Cursor cursor = database.rawQuery("select location from data2 where id = (select outkey from data1 where id = ?)", new String[]{number.substring(0, 7)});
            if (cursor.moveToFirst()) {
                address = cursor.getString(0);
            }
            cursor.close();
        } else {
            switch (number.length()) {
                case 3:
                    address = "报警电话";
                    break;
                case 4:
                    address = "模拟器";
                    break;
                case 5:
                    address = "客服电话";
                    break;
                case 7:
                case 8:
                    address = "座机号码";
                    break;
                default:
                    if (number.startsWith("0") && number.length() > 10) {
                        Cursor cursor = database.rawQuery("select location from data2 where area = ?", new String[]{number.substring(1, 4)});
                        if (cursor.moveToFirst()) {
                            address = cursor.getString(0);
                        } else {
                            cursor.close();
                            cursor = database.rawQuery("select location from data2 where area = ?", new String[]{number.substring(1, 3)});
                            if (cursor.moveToFirst()) {
                                address = cursor.getString(0);
                            }
                            cursor.close();
                        }
                    }
                    break;
            }
        }
        database.close();
        return address;
    }
}
