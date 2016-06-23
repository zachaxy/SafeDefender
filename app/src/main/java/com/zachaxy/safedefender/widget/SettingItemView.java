package com.zachaxy.safedefender.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zachaxy.safedefender.R;


/**
 * Created by zhangxin on 2016/6/22.
 * 自定义控件(ViewGroup)
 * (1)必须要实现三个构造方法,我们可以让一个参数的调用两个的,两个参数的调用三个的,最后在三个参数的构造方法中执行初始化操作.
 */
public class SettingItemView extends RelativeLayout {

    private TextView mTVTitle;
    private TextView mTVDescribe;
    private CheckBox mCheck;
    private SharedPreferences mConfig;

    private String title;
    private String desc_on;
    private String desc_off;

    private static final String NAME_SPACE = "http://schemas.android.com/apk/res-auto";

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //暂时不能使用引用的字符串
        title = attrs.getAttributeValue(NAME_SPACE, "set_item_title");
        desc_on = attrs.getAttributeValue(NAME_SPACE, "set_item_desc_on");
        desc_off = attrs.getAttributeValue(NAME_SPACE, "set_item_desc_off");
        initView();
        title = title.substring(1);
        title = context.getString(Integer.valueOf(title));
        setTitle(title);
    }

    /***
     * 初始化布局
     */
    private void initView() {
        //以往在使用View.inflate的时候,第三个参数传入的都是null
        //第三个参数是这个view的ParentView,这里我们需要一个parent,所以传入this.
        //下面这句话的意思是将setting_item对应的布局文件渲染后,塞给this,是其称为this的viewgroup中的子view
        View.inflate(getContext(), R.layout.setting_item, this);
        mTVTitle = (TextView) findViewById(R.id.tv_set_title);
        mTVDescribe = (TextView) findViewById(R.id.tv_set_desc);
        mCheck = (CheckBox) findViewById(R.id.cb_check);
    }

    public void setTitle(String s) {
        mTVTitle.setText(s);
    }

    public void setDesc(String s) {
        mTVDescribe.setText(s);
    }

    public void setCheck(boolean isCheck) {
        mCheck.setChecked(isCheck);
        if (isCheck) {
            setDesc(desc_on);
        } else {
            setDesc(desc_off);
        }
    }

    public boolean isCheck(){
        return mCheck.isChecked();
    }
}
