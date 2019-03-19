package com.cynthia.viewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Cchanges on 2019/3/19
 * 仿制知乎的卡片滑动Views
 */
public class SlideCardViews extends FrameLayout {

    private float actionTime;

    public SlideCardViews(Context context) {
        this(context, null);
    }

    public SlideCardViews(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideCardViews(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.SlideCardViews);
        actionTime = ta.getFloat(R.styleable.SlideCardViews_actionTime, 0.5f);
        ta.recycle();
    }


    // 还是默认的march_parent，不知道为什么获取的子view的宽高不是预期值.jpg
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int width = getChildAt(0).getMeasuredWidth();
        int height = getChildAt(0).getMeasuredHeight();
        int offset = getChildCount() * 20;

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int vertical = 0;
        int across = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.layout(l + across, t + vertical, view.getMeasuredWidth() + across, view.getMeasuredHeight() + vertical);
            vertical -= 20;
            across -= 20;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }
}
