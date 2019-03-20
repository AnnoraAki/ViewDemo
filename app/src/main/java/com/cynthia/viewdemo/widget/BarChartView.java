package com.cynthia.viewdemo.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;
import com.cynthia.viewdemo.R;

/**
 * Created by Cchanges on 2019/3/16
 * 初版见 {@link RectProcessView}
 * 添加：双缓存机制
 * 修复：view绘制的卡顿
 */
public class BarChartView extends View {

    private Paint backgroundPaint, rectPaint;
    private TextPaint textPaint;
    private Rect textRect;
    private RectF chartRectF;
    private Path mPath;

    //  缓存用bitmap和canvas
    private Bitmap backgroundBitmap;
    private Canvas backgroundCanvas;
    private ValueAnimator animator;

    private int coordinateMax;
    private int time;
    private boolean isDrawCoordinate;

    private String[] colors;
    private String[] subjectTexts;
    private int[] finalNums;
    private float[] current;
    private float[] acrossLocation;
    private float[] verticalLocation;

    public BarChartView(Context context) {
        this(context, null);
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initOthers();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.BarChartView);
        colors = getStringArrays(ta.getTextArray(R.styleable.BarChartView_colors));
        subjectTexts = getStringArrays(ta.getTextArray(R.styleable.BarChartView_subjectNames));
        finalNums = getIntArrays(ta.getTextArray(R.styleable.BarChartView_finalNums));
        coordinateMax = ta.getInt(R.styleable.BarChartView_max, 120);
        time = ta.getInt(R.styleable.BarChartView_time, 3);
        ta.recycle();
    }

    private void initOthers() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.parseColor("#5954ACFF"));
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(dp2px(0.5f));

        rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(dp2px(14));
        textPaint.setColor(Color.parseColor("#0083FF"));

        mPath = new Path();
        textRect = new Rect();
        chartRectF = new RectF();

        acrossLocation = new float[7];
        verticalLocation = new float[9];
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            heightSize = (int) (widthSize / 6f * 5 * 1.25f);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = (int) Math.max(widthSize / 1.5f, dp2px(300));
            heightSize = (int) (widthSize / 6f * 5 * 1.25f);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }
        setData(widthSize, heightSize);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 根据传入的宽高计算相对应的点
     *
     * @param width  view的宽
     * @param height view的高
     */
    private void setData(int width, int height) {
        float everyWidthSize = (width - dp2px(15)) / 8f;
        float everyHeight = height / 8f;

        for (int i = 0; i < 7; i++) {
            acrossLocation[i] = everyHeight * (i + 0.25f);
        }
        for (int i = 0; i < 9; i++) {
            if (i == 0)
                verticalLocation[i] = dp2px(15);
            verticalLocation[i] = everyWidthSize * i + dp2px(15);
        }

//      设置虚线
        backgroundPaint.setPathEffect(new DashPathEffect(new float[]{dp2px(16), dp2px(4)}, 0));

//      bitmap初始化
        if (backgroundBitmap == null) {
            backgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            backgroundCanvas = new Canvas(backgroundBitmap);
            isDrawCoordinate = false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//      双缓存
        if (!isDrawCoordinate) {
            drawCoordinate(backgroundCanvas);
            isDrawCoordinate = true;
        }
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        drawRects(canvas);
    }

    /**
     * 绘制坐标线和坐标轴数据
     *
     * @param canvas 缓存bitmap绑定的画布
     */
    private void drawCoordinate(Canvas canvas) {
//      绘制坐标轴
        for (int i = 0; i < 7; i++) {
            mPath.moveTo(verticalLocation[1], acrossLocation[i]);
            mPath.lineTo(verticalLocation[7] + verticalLocation[1] / 2, acrossLocation[i]);
            canvas.drawPath(mPath, backgroundPaint);
            mPath.reset();
        }
//      计算间隔值并进行绘制
        int maxNum = coordinateMax;
        int margin = coordinateMax / 6;
        textPaint.setAlpha(165);
        for (int i = 0; i < 7; i++) {
            String t = String.valueOf(maxNum);
            textPaint.getTextBounds(t, 0, t.length(), textRect);
            float height = textRect.height();
            float width = textRect.width();
            canvas.drawText(t, verticalLocation[0] * 1.5f - width / 2, acrossLocation[i] + height / 2, textPaint);
            maxNum = maxNum - margin;
        }

//      绘制底部坐标文字
        textPaint.setAlpha(220);
        for (int i = 0; i < 3; i++) {
            textPaint.getTextBounds(subjectTexts[i], 0, subjectTexts[i].length(), textRect);
            float height = textRect.height();
//          使用该Layout来适配文字一行会重叠的问题
            StaticLayout layout = new StaticLayout(subjectTexts[i], textPaint, (int) verticalLocation[1], Layout.Alignment.ALIGN_CENTER, 1.0f, 0f, true);
//          移动画布进行文字绘制
            canvas.translate(verticalLocation[2 * (i + 1)] - verticalLocation[0] / 2, acrossLocation[6] + height / 2);
            layout.draw(canvas);
            canvas.translate(-(verticalLocation[2 * (i + 1)] - verticalLocation[0] / 2), -(acrossLocation[6] + height / 2));
        }
        for (int i = 0; i < 3; i++) {
            chartRectF.left = verticalLocation[2 * (i + 1)];
            chartRectF.right = verticalLocation[2 * (i + 1) + 1];
            chartRectF.bottom = acrossLocation[6];
            chartRectF.top = acrossLocation[6] - dp2px(5);
            rectPaint.setColor(Color.parseColor(colors[2 * i]));
            canvas.drawRoundRect(chartRectF, 0, 0, rectPaint);
        }
    }

    /**
     * 绘制柱形以及提示文字
     *
     * @param canvas 绘制画布
     */

    private void drawRects(Canvas canvas) {
        for (int i = 0; i < 3; i++) {
            float pro = current[i] / coordinateMax * acrossLocation[6];
            chartRectF.left = verticalLocation[2 * (i + 1)];
            chartRectF.right = verticalLocation[2 * (i + 1) + 1];
            chartRectF.bottom = acrossLocation[6];
            chartRectF.top = acrossLocation[6] - pro;
            int[] color = {Color.parseColor(colors[2 * i + 1]), Color.parseColor(colors[2 * i])};
            float[] pos = {0f, 1f};
            LinearGradient shader = new LinearGradient(0, 0, 0, pro,
                    color, pos, Shader.TileMode.CLAMP);
            rectPaint.setColor(Color.parseColor(colors[2 * i]));
            rectPaint.setShader(shader);
            canvas.drawRoundRect(chartRectF, 5, 5, rectPaint);

            String pe = String.valueOf((int) current[i]) + "人";
            textPaint.getTextBounds(pe, 0, pe.length(), textRect);
            float h = textRect.height();
            textPaint.setColor(Color.parseColor("#ccFF5A5A"));
//          保证绘制的文字居中
            if (pe.length() < "100人".length())
                canvas.drawText(pe, verticalLocation[2 * (i + 1)] + verticalLocation[0] / 4, acrossLocation[6] - pro - h, textPaint);
            else
                canvas.drawText(pe, verticalLocation[2 * (i + 1)], acrossLocation[6] - pro - h, textPaint);
        }
    }

    /**
     * 动画初始化
     */
    private void initAnim() {
        current = new float[3];
        for (int i = 0; i < 3; i++) {
            animator = ValueAnimator.ofFloat(0, finalNums[i]);
            animator.setDuration((long) (time * 1000));
            animator.setRepeatCount(0);
            animator.setInterpolator(new LinearInterpolator());
            final int finalI = i;
            animator.addUpdateListener(animation -> {
                current[finalI] = (float) animation.getAnimatedValue();
                postInvalidate();
            });
            animator.start();
        }
    }

    /**
     * 工具用方法
     */
    private String[] getStringArrays(CharSequence[] array) {
        String[] res = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            res[i] = String.valueOf(array[i]);
        }
        return res;
    }

    private int[] getIntArrays(CharSequence[] array) {
        int[] res = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            res[i] = Integer.valueOf((String) array[i]);
        }
        return res;
    }

    private int dp2px(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    /**
     * 动画相关
     */
    public void startAnim() {
        initAnim();
        animator.start();
    }

    public void stopAnim() {
        if (animator.isRunning()) {
            animator.cancel();
        }
    }

    /**
     * 属性设置对外暴露的接口
     */
    public void setSubjectTexts(String[] subjects) {
        subjectTexts = subjects;
        postInvalidate();
    }

    public void setFinalNums(int[] nums) {
        finalNums = nums;
        postInvalidate();
    }

    public void setCoordinateMax(int max) {
        coordinateMax = max;
        postInvalidate();
    }

}
