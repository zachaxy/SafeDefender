package com.zachaxy.safedefender.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zachaxy.safedefender.bean.BlackItemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxin on 2016/7/13.
 * <p/>
 * Description :
 * 黑名单数据库工具类
 */
public class BlackListDao {

    private final BlackListOpenHelper helper;

    public BlackListDao(Context context) {
        helper = new BlackListOpenHelper(context);
    }

    public boolean add(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("number", number);
        value.put("mode", mode);
        long rawID = db.insert("balckList", null, value);
        db.close();
        if (rawID == -1) {
            return false;
        } else {
            return true;
        }

    }


    public boolean delete(String number) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rowNumber = db.delete("balckList", "number=?", new String[]{number});
        db.close();
        if (rowNumber == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 通过电话号码改变拦截模式
     *
     * @param number 由number搜索响应黑名单
     * @param mode   将其拦截模式修改
     * @return true:修改成功;false:修改失败;
     */
    public boolean update(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("mode", mode);
        int rowNUmber = db.update("balckList", value, "number=?", new String[]{number});
        db.close();
        if (rowNUmber == 0) {
            return false;
        } else {
            return true;
        }
    }

    /***
     * 查询所有黑名单
     */
    public List<BlackItemInfo> findAll() {
        ArrayList<BlackItemInfo> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("balckList", new String[]{"number", "mode"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            BlackItemInfo blackItemInfo = new BlackItemInfo(cursor.getString(0), cursor.getString(1));
            list.add(blackItemInfo);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 分页查询黑名单,减轻服务器压力.
     *
     * @param currentPage 表示当前查询第几页,从0开始计数
     * @param pageSize    表示每一页保存多少条目
     * @return 返回一个page
     */
    public List<BlackItemInfo> findPart(int currentPage, int pageSize) {
        ArrayList<BlackItemInfo> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        //limit表示查询多少条数据;offset表示从多少条后开始查询
        Cursor cursor = db.rawQuery("select number,mode from balckList limit ? offset ?",
                new String[]{String.valueOf(pageSize), String.valueOf(pageSize * currentPage)});
        while (cursor.moveToNext()) {
            BlackItemInfo blackItemInfo = new BlackItemInfo(cursor.getString(0), cursor.getString(1));
            list.add(blackItemInfo);
        }
        cursor.close();
        db.close();
        return list;
    }


    /**
     * 分批查询黑名单,减轻服务器压力.
     *
     * @param startIndex 表示接下来要查询的开始的位置
     * @param maxCount    表示每一批量最多要查询的条目
     * @return 返回一个page
     */
    public List<BlackItemInfo> findBatch(int startIndex, int maxCount) {
        ArrayList<BlackItemInfo> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        //limit表示查询多少条数据;offset表示从多少条后开始查询
        Cursor cursor = db.rawQuery("select number,mode from balckList limit ? offset ?",
                new String[]{String.valueOf(maxCount), String.valueOf(startIndex)});
        while (cursor.moveToNext()) {
            BlackItemInfo blackItemInfo = new BlackItemInfo(cursor.getString(0), cursor.getString(1));
            list.add(blackItemInfo);
        }
        cursor.close();
        db.close();
        return list;
    }


    public int getTotalItem() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from balckList", null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

}
