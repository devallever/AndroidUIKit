package com.allever.lib.ui.checkview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.allever.lib.common.util.DisplayUtils;
import com.allever.lib.common.util.log.LogUtils;

/**
 * @author allever
 * @参考 Android自定义View：一个精致的打钩小动画
 * @link https://www.jianshu.com/p/1b2cdba03d23
 * @Github https://github.com/ChengangFeng/TickView
 */
public class CheckView extends View {

    private static final int DEFAULT_SIZE = DisplayUtils.INSTANCE.dip2px(48);
    private static final int DEFAULT_WIDTH = DEFAULT_SIZE;
    private static final int DEFAULT_HEIGHT = DEFAULT_SIZE;

    private static final int DEFAULT_PADDING = (DEFAULT_SIZE / 10);

    /***
     * Padding应该是每个方向一致，设置不同时取最大值
     */
    private int mPadding = DEFAULT_PADDING;

//    private int mPaddingLeft = DEFAULT_PADDING;
//    private int mPaddingTop = DEFAULT_PADDING;
//    private int mPaddingRight = DEFAULT_PADDING;
//    private int mPaddingBottom = DEFAULT_PADDING;

    private int mMarginLeft = 0;
    private int mMarginTop = 0;
    private int mMarginRight = 0;
    private int mMarginBottom = 0;

    private boolean mIsAnimationRunning = false;
    private boolean mChecked = false;


    private Paint mPaint;

    private PointF mTickStartPoint;
    private PointF mTickCenterPoint;
    private PointF mTickEndPoint;
    private Path mTickPath;

    private int mCircleX;
    private int mCircleY;
    private int mRadius;
    private RectF mCircleRectF;
    private ObjectAnimator mCircleProgressAnimator;

    private int circleProgress;

    public CheckView(Context context) {
        this(context, null);
    }

    public CheckView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAnimator();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mTickStartPoint = new PointF();
        mTickCenterPoint = new PointF();
        mTickEndPoint = new PointF();
        mTickPath = new Path();

        mCircleRectF = new RectF();
    }

    private void initAnimator() {
        mCircleProgressAnimator = ObjectAnimator.ofInt(this, "circleProgress", 0, 360);
        mCircleProgressAnimator.setDuration(1000);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        log("onMeasure");
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int paddingH = Math.max(paddingLeft, paddingRight);
        int paddingV = Math.max(paddingTop, paddingBottom);
        mPadding = Math.max(paddingH, paddingV);
        if (mPadding < DEFAULT_PADDING) {
            mPadding = DEFAULT_PADDING;
        }

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
        mMarginLeft = layoutParams.leftMargin;
        mMarginTop = layoutParams.topMargin;
        mMarginRight = layoutParams.rightMargin;
        mMarginBottom = layoutParams.bottomMargin;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        log("widthSize = " + widthSize);
        log("heightSize = " + heightSize);
        log("default padding = " + DEFAULT_PADDING);

        int width = widthSize;
        switch (widthMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                width = DEFAULT_WIDTH;
                break;
            default:
                break;
        }

        int height = heightSize;
        switch (heightMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                height = DEFAULT_HEIGHT;
                break;
            default:
                break;
        }


        int size = Math.min(width, height);

        mCircleX = size / 2;
        mCircleY = size / 2;
        mRadius = size / 2 - mPadding;

        mCircleRectF.left = mPadding;
        mCircleRectF.top = mPadding;
        mCircleRectF.right = mPadding + 2 * mRadius;
        mCircleRectF.bottom = mPadding + 2 * mRadius;

        mTickStartPoint.x = mCircleX - mRadius / 4f - mRadius / 8f;
        mTickStartPoint.y = mCircleY + mRadius / 4f - mRadius / 8f;

        mTickCenterPoint.x = mCircleX - mRadius / 8f;
        mTickCenterPoint.y = mCircleY + mRadius / 2f - mRadius / 8f;

        mTickEndPoint.x = mCircleX + mRadius / 2f - mRadius / 8f;
        mTickEndPoint.y = mCircleY - mRadius / 4f - mRadius / 8f;

        mTickPath.moveTo(mTickStartPoint.x, mTickStartPoint.y);
        mTickPath.lineTo(mTickCenterPoint.x, mTickCenterPoint.y);
        mTickPath.lineTo(mTickEndPoint.x, mTickEndPoint.y);

        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        log("onDraw");

        if (!mChecked) {

            //画圆圈
            drawCircle(canvas);
            //画沟
            drawTick(canvas);
            return;

        }

        //画圆圈
        drawCircle(canvas);
        //画沟
        drawTick(canvas);
        //画圆形进度
        drawCircleProgress(canvas);

        if (!mIsAnimationRunning) {
            mIsAnimationRunning = true;
            mCircleProgressAnimator.start();
        }
    }

    /**
     * 改变状态
     *
     * @param checked 选中还是未选中
     */
    public void setChecked(boolean checked) {
        if (this.mChecked != checked) {
            mChecked = checked;
            reset();
        }
    }

    public boolean isChecked() {
        return mChecked;
    }


    /**
     * 重置
     */
    private void reset() {
        mCircleProgressAnimator.cancel();
        circleProgress = 0;
        mIsAnimationRunning = false;
        invalidate();
    }

    private void drawCircle(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(DisplayUtils.INSTANCE.dip2px(2));
        canvas.drawCircle(mCircleX, mCircleY, mRadius, mPaint);
    }

    private void drawTick(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(DisplayUtils.INSTANCE.dip2px(2));
        canvas.drawPath(mTickPath, mPaint);
    }

    private void drawCircleProgress(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.YELLOW);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(DisplayUtils.INSTANCE.dip2px(2));
        canvas.drawArc(mCircleRectF, 90, circleProgress, false, mPaint);
    }

    public int getCircleProgress() {
        return circleProgress;
    }

    public void setCircleProgress(int circleProgress) {
        this.circleProgress = circleProgress;
        invalidate();
    }

    private void log(String msg) {
        LogUtils.INSTANCE.d(msg);
    }
}