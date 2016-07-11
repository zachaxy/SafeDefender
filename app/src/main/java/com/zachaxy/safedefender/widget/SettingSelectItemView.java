package com.zachaxy.safedefender.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zachaxy.safedefender.R;

/**
 * Created by zhangxin on 2016/7/11.
 */
public class SettingSelectItemView extends RelativeLayout {

    private TextView mTVTitle;
    private TextView mTVDescribe;
    private ImageView mIVSelect;


    private String title;


    private static final String NAME_SPACE = "http://schemas.android.com/apk/res-auto";

    public SettingSelectItemView(Context context) {
        this(context, null);
    }

    public SettingSelectItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingSelectItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //暂时不能使用引用的字符串
        title = attrs.getAttributeValue(NAME_SPACE, "set_item_select_title");

        initView();
        if (title.startsWith("@")) {
            title = title.substring(1);
            title = context.getString(Integer.valueOf(title));
        }
        setTitle(title);
    }

    /***
     * 初始化布局
     */
    private void initView() {
        //以往在使用View.inflate的时候,第三个参数传入的都是null
        //第三个参数是这个view的ParentView,这里我们需要一个parent,所以传入this.
        //下面这句话的意思是将setting_item对应的布局文件渲染后,塞给this,是其成为this的viewgroup中的子view
        View.inflate(getContext(), R.layout.setting_select_item, this);
        mTVTitle = (TextView) findViewById(R.id.tv_select_title);
        mTVDescribe = (TextView) findViewById(R.id.tv_select_desc);
        mIVSelect = (ImageView) findViewById(R.id.iv_select);
    }

    public void setTitle(String s) {
        mTVTitle.setText(s);
    }

    public void setDesc(String s) {
        mTVDescribe.setText(s);
    }

    public void setIVSelect(boolean flag){
        if (flag){
            mIVSelect.setImageResource(R.drawable.jiantou1);
        }else {
            mIVSelect.setImageResource(R.drawable.jiantou1_disable);
        }
    }

}
