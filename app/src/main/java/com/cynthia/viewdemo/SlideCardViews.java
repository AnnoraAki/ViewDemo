package com.cynthia.viewdemo;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
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
    private float x1 = 0f;
    private int[] baseBottom = {-1, -1, -1, -1};
    private View top;

    private ObjectAnimator animatorLeft;
    private ObjectAnimator animatorRight;
    private ObjectAnimator animatorUp;
    private ObjectAnimator.AnimatorListener animatorEndListener;
    private ObjectAnimator.AnimatorListener animatorUpEndListener;

    public SlideCardViews(Context context) {
        this(context, null);
    }

    public SlideCardViews(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideCardViews(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        animatorEndListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                View bottom = getChildAt(0);
                top.setVisibility(INVISIBLE);
                bringChildToFront(bottom);
                postInvalidate();
                top.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        };
        animatorUpEndListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animation.cancel();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.SlideCardViews);
        actionTime = ta.getFloat(R.styleable.SlideCardViews_actionTime, 1);
        ta.recycle();
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
        top = getChildAt(1);
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
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isInCard(ev.getX(), ev.getY())) {
                x1 = ev.getX();
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
        View bottom = getChildAt(0);
        if (baseBottom[0] != -1) {
            baseBottom[0] = bottom.getLeft();
            baseBottom[1] = bottom.getTop();
            baseBottom[2] = bottom.getRight();
            baseBottom[3] = bottom.getBottom();
        }
        if (isLeft) {
            animatorLeft = ObjectAnimator.ofFloat(top, "translationX", top.getLeft(), -top.getWidth());
            animatorLeft.setDuration((long) (actionTime * 1000));
            animatorLeft.start();
            animatorLeft.addListener(animatorEndListener);
        } else {
            animatorRight = ObjectAnimator.ofFloat(top, "translationX", top.getLeft(), getContext().getResources().getDisplayMetrics().widthPixels);
            animatorRight.setDuration((long) (actionTime * 1000));
            animatorRight.start();
            animatorRight.addListener(animatorEndListener);
        }
        PropertyValuesHolder upX = PropertyValuesHolder.ofFloat("translationX", bottom.getLeft(), bottom.getLeft() - 20);
        PropertyValuesHolder upY = PropertyValuesHolder.ofFloat("translationY", bottom.getTop(), bottom.getTop() - 20);
        animatorUp = ObjectAnimator.ofPropertyValuesHolder(bottom, upX, upY).setDuration((long) (actionTime * 1000));
        animatorUp.start();
        animatorUp.addListener(animatorUpEndListener);
    }
}
