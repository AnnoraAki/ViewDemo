package com.cynthia.viewdemo;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.CardView;
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
    private float x1 = 0f;

    private CardView top;
    private CardView bottom;
    private CardView translation;

    private ObjectAnimator.AnimatorListener animatorEndListener;

    public SlideCardViews(Context context) {
        this(context, null);
    }

    public SlideCardViews(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideCardViews(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
        animatorEndListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                returnBack();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        };

    }

    private void returnBack() {
        removeAllViews();
        CardView temp = top;
        top = bottom;
        bottom = translation;
        translation = temp;
        addView(translation);
        addView(bottom);
        addView(top);
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.setTranslationY(0);
            view.setTranslationX(0);
        }
        requestLayout();
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.SlideCardViews);
        actionTime = ta.getFloat(R.styleable.SlideCardViews_actionTime, 1);
        ta.recycle();
        FrameLayout.LayoutParams layoutParams = new LayoutParams(300, 150);
        top = new CardView(getContext());
        bottom = new CardView(getContext());
        translation = new CardView(getContext());
        addView(translation, layoutParams);
        addView(bottom, layoutParams);
        addView(top, layoutParams);

        top.setCardBackgroundColor(Color.LTGRAY);
        bottom.setCardBackgroundColor(Color.BLUE);
        translation.setCardBackgroundColor(Color.CYAN);
    }

    // 还是默认的march_parent，不知道为什么获取的子view的宽高不是预期值.jpg
    // todo 解决测量的bug
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


//        int width = getChildAt(0).getMeasuredWidth();
//        int height = getChildAt(0).getMeasuredHeight();
//        int offset = getChildCount() * 20;

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int vertical = 0;
        int across = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.layout(l + across, t + vertical, view.getMeasuredWidth() + across, view.getMeasuredHeight() + vertical);
            if (i == 1) {
                vertical -= 20;
                across -= 20;
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isInCard(ev.getX(), ev.getY())) {
                x1 = ev.getX();
//              事件分发与拦截
                return true;
            }
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (x1 != 0) {
                float x2 = ev.getX();
                if (Math.abs(x1 - x2) > top.getWidth() / 5) {
                    boolean isLeft = x1 > x2;
                    startAnim(isLeft);
                    x1 = 0;
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isInCard(float x, float y) {
        return top.getLeft() < x && x < top.getRight() && top.getTop() < y && y < top.getBottom();
    }

    private void startAnim(boolean isLeft) {
        if (isLeft) {
            ObjectAnimator animatorLeft = ObjectAnimator.ofFloat(top, "translationX", 0, -getContext().getResources().getDisplayMetrics().widthPixels);
            animatorLeft.setDuration((long) (actionTime * 1000));
            animatorLeft.start();
        } else {
            ObjectAnimator animatorRight = ObjectAnimator.ofFloat(top, "translationX", 0, getContext().getResources().getDisplayMetrics().widthPixels);
            animatorRight.setDuration((long) (actionTime * 1000));
            animatorRight.start();
        }
        PropertyValuesHolder upX = PropertyValuesHolder.ofFloat("translationX", 0, -20);
        PropertyValuesHolder upY = PropertyValuesHolder.ofFloat("translationY", 0, -20);

        ObjectAnimator animatorUp = ObjectAnimator.ofPropertyValuesHolder(bottom, upX, upY)
                .setDuration((long) (actionTime * 1000));
        animatorUp.setStartDelay((long) (actionTime * 100));
        animatorUp.addListener(animatorEndListener);
        animatorUp.start();

        ObjectAnimator animatorShow = ObjectAnimator.ofFloat(translation, "alpha", 0f, 1f)
                .setDuration((long) (actionTime * 1000));
        animatorShow.setStartDelay((long) (actionTime * 100));
        animatorShow.start();
    }
}
