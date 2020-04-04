package com.allever.lib.ui.checkview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Keep;
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

    private static final int DEFAULT_SIZE = DisplayUtils.INSTANCE.dip2px(36);
    private static final int DEFAULT_WIDTH = DEFAULT_SIZE;
    private static final int DEFAULT_HEIGHT = DEFAULT_SIZE;

    private static final int DEFAULT_PADDING = (DEFAULT_SIZE / 6);

    /***
     * Padding应该是每个方向一致，设置不同时取最大值
     */
    private int mPadding = DEFAULT_PADDING;

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
    private AnimatorSet mAnimatorSet;

    @Keep
    private int circleProgress;
    @Keep
    private int innerCircleRadius;

    private int mCheckedColor;
    private int mHintColor;

    private CheckChangedListener mListener;

    public CheckView(Context context) {
        this(context, null);
    }

    public CheckView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CheckView);
        mCheckedColor = typedArray.getColor(R.styleable.CheckView_cv_checked_color, Color.RED);
        mHintColor = typedArray.getColor(R.styleable.CheckView_cv_hint_color, Color.GRAY);
        typedArray.recycle();

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

        mRadius = (DEFAULT_SIZE - mPadding) / 2 - DisplayUtils.INSTANCE.dip2px(2);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setChecked(!mChecked);
                if (mListener != null) {
                    mListener.onChanged(CheckView.this, mChecked);
                }
            }
        });
    }

    private void initAnimator() {
        ObjectAnimator circleProgressAnimator = ObjectAnimator.ofInt(this, "circleProgress", 0, 360);
        circleProgressAnimator.setDuration(500);

        ObjectAnimator innerCircleAnimator = ObjectAnimator.ofInt(this, "innerCircleRadius", mRadius - DisplayUtils.INSTANCE.dip2px(2), 0);
        innerCircleAnimator.setDuration(500);
        innerCircleAnimator.setInterpolator(new DecelerateInterpolator());

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playSequentially(circleProgressAnimator, innerCircleAnimator);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        log("onDraw");

        if (!mChecked) {
            //画圆圈
            drawHintCircle(canvas);
            //画沟
            drawTick(canvas, mHintColor);
            return;
        }

        //画圆形进度
        drawCircleProgress(canvas);

        if (circleProgress == 360) {
            //画外部圆形
            drawOuterCircle(canvas);
            //画内部白色圆形
            drawInnerCircle(canvas);
        }

        if (innerCircleRadius == 0) {
            drawTick(canvas, Color.WHITE);
        }

        if (!mIsAnimationRunning) {
            mIsAnimationRunning = true;
            mAnimatorSet.start();
        }
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


    public void setOnCheckChangerListener(CheckChangedListener listener) {
        mListener = listener;
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

    public void setCheckedColor(int color) {
        mCheckedColor = color;
    }

    public void setHintColor(int color) {
        mHintColor = color;
    }

    /**
     * 重置
     */
    private void reset() {
        mAnimatorSet.cancel();
        circleProgress = 0;
        innerCircleRadius = mRadius - DisplayUtils.INSTANCE.dip2px(2);
//        log("reset 内圆半径 = " + innerCircleRadius);
        mIsAnimationRunning = false;
        invalidate();
    }

    private void drawHintCircle(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mHintColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(DisplayUtils.INSTANCE.dip2px(2));
        canvas.drawCircle(mCircleX, mCircleY, mRadius, mPaint);
    }

    private void drawOuterCircle(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mCheckedColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mCircleX, mCircleY, mRadius, mPaint);
    }

    private void drawInnerCircle(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
//        log("内圆半径 = " + innerCircleRadius);
        canvas.drawCircle(mCircleX, mCircleY, innerCircleRadius, mPaint);
    }

    private void drawTick(Canvas canvas, int tickColor) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(tickColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(DisplayUtils.INSTANCE.dip2px(2));
        canvas.drawPath(mTickPath, mPaint);
    }

    private void drawCircleProgress(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mCheckedColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(DisplayUtils.INSTANCE.dip2px(2));
        canvas.drawArc(mCircleRectF, 90, circleProgress, false, mPaint);
    }

    public int getCircleProgress() {
        return circleProgress;
    }

    @Keep
    public void setCircleProgress(int circleProgress) {
        this.circleProgress = circleProgress;
        invalidate();
    }

    public int getInnerCircleRadius() {
        return innerCircleRadius;
    }

    @Keep
    public void setInnerCircleRadius(int innerCircleRadius) {
        this.innerCircleRadius = innerCircleRadius;
        invalidate();
    }

    private void log(String msg) {
        LogUtils.INSTANCE.d(msg);
    }
}
