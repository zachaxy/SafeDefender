package com.zachaxy.safedefender.widget;

import android.content.Context;
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

    public SettingItemView(Context context) {
        // super(context);
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int attributeCount = attrs.getAttributeCount();
        for (int i = 0;i<attributeCount;i++){
            System.out.println(attrs.getAttributeName(i)+"--"+attrs.getAttributeValue(i));
        }
        initView();
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
        mCheck= (CheckBox) findViewById(R.id.cb_check);
    }

    public void setTitle(String s){
        mTVTitle.setText(s);
    }

    public void setDesc(String s){
        mTVDescribe.setText(s);
    }

    public void setCheck(boolean isCheck){
        mCheck.setChecked(isCheck);
    }
}
