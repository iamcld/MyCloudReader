package com.chenld.mycloudreader.view.viewbigimage;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by chenld on 2017/3/9.
 * 继承于viewpager 可以和photoView一起使用，实现相册图片的左右滑动，放大缩小，等
 */

public class HackyViewPager extends ViewPager{
//    public HackyViewPager(Context context) {
//        super(context);
//    }
    public HackyViewPager(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        try {
            return super.onInterceptTouchEvent(event);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }
}
