package com.zachaxy.safedefender.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by zhangxin on 2016/6/22.
 */
public class MarqueeTextView extends TextView {

    //直接在代码中新建一个TextView的对象时,调用该方法.
    public MarqueeTextView(Context context) {
        super(context);
    }

    //有属性时调用该方法
    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //有style样式的话会调用该方法.
    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
