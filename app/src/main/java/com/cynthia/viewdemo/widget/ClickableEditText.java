package com.cynthia.viewdemo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

/**
 * Created by Cchanges on 2019/3/16
 * 简单的点击右侧icon响应点击事件的EditText
 */
public class ClickableEditText extends AppCompatEditText {

    private ClickIconCallback mClickListener;

    public ClickableEditText(Context context) {
        super(context);
    }

    public ClickableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClickableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (isInDrawable(x, y)) {
                if (mClickListener == null) {
                    return super.onTouchEvent(event);
                }
                mClickListener.click(this);
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    private boolean isInDrawable(int x, int y) {
        Drawable drawable = getCompoundDrawables()[2];
        if (drawable == null) return false;
        int right = getWidth() - getPaddingRight();
        int left = right - drawable.getIntrinsicWidth();
        int bottom = getHeight() - getPaddingBottom();
        int top = bottom - drawable.getIntrinsicHeight();
        return left < x && x < right && top < y && y < bottom;
    }

    public void registerListener(ClickIconCallback callback) {
        mClickListener = callback;
    }

    interface ClickIconCallback {
        void click(EditText view);
    }
}
