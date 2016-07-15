package com.zachaxy.safedefender;

import android.content.Context;
import android.test.AndroidTestCase;

import com.zachaxy.safedefender.dao.BlackListDao;

import java.util.Random;

/**
 * Created by zhangxin on 2016/7/14.
 * <p>
 * Description :
 *      用来测试数据库功能的读写功能,注意:所有的测试函数必须以test为前缀!!!
 */
public class BlackListDaoTest extends AndroidTestCase {

    private Context context;
    private BlackListDao dao;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context = getContext();
        dao  = new BlackListDao(context);
    }

    public void testAdd(){
        Random rand = new Random();
        for (int i = 0; i < 123; i++) {
            dao.add("182"+i,String.valueOf(rand.nextInt(3)));
        }
    }

    public void testGetTotalItem(){
        int i = dao.getTotalItem();
        assertEquals(i,123);
    }
}
