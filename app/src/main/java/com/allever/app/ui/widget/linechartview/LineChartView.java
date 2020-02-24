package com.allever.app.ui.widget.linechartview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by allever on 17-7-27.
 */

public class LineChartView extends View {
    private int mDefaultColor;

    private static final String TAG = "LineChartView";

    //横线,坐标画笔
    private Paint mLineTextPaint;
    //贝塞尔曲线画笔
    private Paint mBezierPaint;
    //竖直线画笔
    private Paint mVerticalPaint;
    //标注矩形
    private Paint mRectPaint;
    //标注文字画笔
    private Paint mTextPaint;

    private float mHeight;
    private float mWidth;

    //按下时x坐标
    private float mDownX = -1f;

    private Context mContext;

    //左右边界值,
    private final float mMarginLeftRight;
    //上下边界值
    private final float mMarginTopBottom;

    //数据源.存放曲线参数
    private List<LineDataSet> mLineDataSetList = new ArrayList<>();
    //横坐标时间(总数据)格式01:00,由外部设置
    private List<String> mXNameList = new ArrayList<>();
    //显示的横坐标 时间
    private List<String> mXNameListShow = new ArrayList<>();
    //外层List表示曲线类型，速度，距离，卡路里....
    //内层List表示该条曲线点数据
    private List<List<BezierLineData>> mBezierLineDataList = new ArrayList<>();//主要用来获取曲线上坐标
    private List<List<PointF>> mOriginPointList = new ArrayList<>();//
    //private List<List<PointF>> mSelectedOriginPointList = new ArrayList<>();//选取其中几个点用来绘制曲线
    //private List<List<PointF>> mAndroidPointList = new ArrayList<>();

    //选择N个点绘制曲线~~ N-1段曲线
    private final int mBezierPointCount = 3;

    public LineChartView(Context context) {
        this(context, null);
    }

    public LineChartView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public LineChartView(Context context, AttributeSet attributeSet, int styleff) {
        super(context, attributeSet, styleff);
        mContext = context;
        mMarginLeftRight = DensityUtil.dip2px(mContext, 30f);
        mMarginTopBottom = DensityUtil.dip2px(mContext, 30f);
        mBezierPaint = new Paint();
        mLineTextPaint = new Paint();
        mVerticalPaint = new Paint();
        mRectPaint = new Paint();
        mTextPaint = new Paint();
        mDefaultColor = Color.RED;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制5条分割线
        drawHorizonLine(canvas);
        //绘制横坐标
        drawXLabel(canvas);
        if (mLineDataSetList.size() == 0) return;
        if (mLineDataSetList.get(0).getOldPointFsList().size() == 0) return;
        //绘制贝塞尔曲线
        drawBezier2(canvas);
        //绘制标注
        drawMark2(canvas);
        //绘制标线
        drawMarkLine2(canvas);
    }

    /**
     * 画5条分割线
     */
    private void drawHorizonLine(Canvas canvas) {
        mLineTextPaint.setColor(0x66cccccc);
        mLineTextPaint.setTextSize(DensityUtil.dip2px(mContext, 12f));
        float intervalY = (getMeasuredHeight() - mMarginTopBottom * 2) / 4;//每条线段的间隔
        for (int i = 0; i < 5; i++) {
            canvas.drawLine(
                    mMarginLeftRight,
                    getMeasuredHeight() - mMarginTopBottom - intervalY * i,
                    getMeasuredWidth() - mMarginLeftRight,
                    getMeasuredHeight() - mMarginTopBottom - intervalY * i,
                    mLineTextPaint);
        }
    }

    /**
     * 画横坐标
     */
    private void drawXLabel(Canvas canvas) {
        float xNameintervalX = (mWidth - 2f * mMarginLeftRight) / (mXNameListShow.size() - 1);//横坐标的间隔
        mLineTextPaint.setTextSize(DensityUtil.dip2px(mContext, 12f));
        for (int i = 0; i < mXNameListShow.size(); i++) {
            canvas.drawText(mXNameListShow.get(i),
                    mMarginLeftRight + xNameintervalX * (i) - DensityUtil.dip2px(mContext, 12f),
                    getMeasuredHeight() - DensityUtil.dip2px(mContext, 10f),
                    mLineTextPaint);
            canvas.save();
        }
    }

    private void drawBezier2(Canvas canvas) {
        //绘制前先获取，保存一些数据，原始点(y值)，每条曲线每一段的数据点集,绘制标注时用到.
        initData();

        mBezierPaint.setStyle(Paint.Style.STROKE);
        mBezierPaint.setStrokeWidth(DensityUtil.dip2px(mContext, 3f));//设置线宽
        mBezierPaint.setAntiAlias(true);//去除锯齿
        mBezierPaint.setStrokeJoin(Paint.Join.ROUND);
        mBezierPaint.setStrokeCap(Paint.Cap.ROUND);

        for (int i = 0; i < mLineDataSetList.size(); i++) {
            Path bezierPath = new Path();//曲线路径
            bezierPath.moveTo(mBezierLineDataList.get(i).get(0).getStartP().x, mBezierLineDataList.get(i).get(0).getStartP().y);
            for (int j = 0; j < mBezierLineDataList.get(i).size(); j++) {
                bezierPath.cubicTo(
                        mBezierLineDataList.get(i).get(j).getCp1().x, mBezierLineDataList.get(i).get(j).getCp1().y,
                        mBezierLineDataList.get(i).get(j).getCp2().x, mBezierLineDataList.get(i).get(j).getCp2().y,
                        mBezierLineDataList.get(i).get(j).getEndP().x, mBezierLineDataList.get(i).get(j).getEndP().y);
            }
            //设置颜色和渐变
            int lineColor = mLineDataSetList.get(i).getColor();
            mBezierPaint.setColor(lineColor);
            LinearGradient mLinearGradient;
            int[] colorArr;
            if (mLineDataSetList.get(i).getGradientColors() != null) {
                colorArr = mLineDataSetList.get(i).getGradientColors();
            } else {
                colorArr = new int[]{lineColor, lineColor, lineColor, lineColor, lineColor};
            }
            mLinearGradient = new LinearGradient(
                    0,
                    mMarginTopBottom,
                    0,
                    getMeasuredHeight(),
                    colorArr,
                    null,
                    Shader.TileMode.CLAMP
            );
            mBezierPaint.setShader(mLinearGradient);
            canvas.drawPath(bezierPath, mBezierPaint);
            canvas.save();
        }
    }

    /**
     * 绘制标注
     */
    private void drawMark2(Canvas canvas) {
        if (mDownX == -1) return;
        if (mDownX < mMarginLeftRight || mDownX > mWidth - mMarginLeftRight) return;
        List<BezierLineData> lineDataList;
        BezierLineData lineData;
        float t;//点在曲线上的长度比例
        float intevalBezierX = (mWidth - 2 * mMarginLeftRight) / ((float) (mBezierPointCount - 1));
        PointF linePoint;//曲线上的坐标点
        for (int i = 0; i < mBezierLineDataList.size(); i++) {//曲线数量
            //设置该条曲线的颜色和渐变,画笔
            initLineStyle(i);

            //获取该条曲线每段曲线的数据集合
            lineDataList = mBezierLineDataList.get(i);
            //判断触控点在哪一段曲线上
            int bezierLinePosition = -1;//曲线段数索引
            for (int n = 0; n < lineDataList.size(); n++) {
                if ((mDownX > intevalBezierX * n + mMarginLeftRight) && ((mDownX < intevalBezierX * (n + 1) + mMarginLeftRight))) {
                    bezierLinePosition = n;
                    break;
                }
            }
            if (bezierLinePosition == -1) return;
            //根据段数获取该段曲线的数据点集合(起点，终点，控制点)
            lineData = lineDataList.get(bezierLinePosition);
            //求触控点在该段曲线上的长度比例
            t = (mDownX - lineData.getStartP().x) / intevalBezierX;
            //根据比例，控制点，起点，终点，求该曲线上的坐标点
            linePoint = BezierUtil.calculateBezierPointForCubic(t, lineData.getStartP(), lineData.getCp1(), lineData.getCp2(), lineData.getEndP());

            //求Marker上显示的数值
            float value = 0;
            //根据触控点所在区间求y值(真实数据)
            int position = -1;//触摸点所在区间
            float intervalDataX = (mWidth - 2 * mMarginLeftRight) / ((float) (mOriginPointList.get(i).size()));
            for (int m = 0; m < mOriginPointList.get(i).size(); m++) {
                if ((mDownX > (intervalDataX * m + mMarginLeftRight)) && (mDownX < (intervalDataX * (m + 1) + mMarginLeftRight))) {
                    position = m;
                    break;
                }
            }
            if (position != -1) value = mOriginPointList.get(i).get(position).y;
            value = (float) (Math.round(value * 1000)) / 1000;
            String drawText = value + "" /* + mLineDataSetList.get(i).getSportAnalysisType().getUnit()*/;

            //Marker的宽高
            float dataBoxWidth = DensityUtil.dip2px(mContext, drawText.length() * 7.7f);//标注边框宽度//根据文字长度动态变化
            float dataBoxHeight = DensityUtil.dip2px(mContext, 30);

            if (i % 2 == 0) {
                if (mWidth - mMarginLeftRight - mDownX < dataBoxWidth / 2) {
                    //左边绘制
                    drawLeft(canvas, linePoint, drawText, dataBoxWidth, dataBoxHeight);
                } else {
                    //右边绘制
                    drawRight(canvas, linePoint, drawText, dataBoxWidth, dataBoxHeight);
                }
            } else {
                if (mDownX - mMarginLeftRight < dataBoxWidth / 2) {
                    //右边绘制
                    drawRight(canvas, linePoint, drawText, dataBoxWidth, dataBoxHeight);
                } else {
                    //左边绘制
                    drawLeft(canvas, linePoint, drawText, dataBoxWidth, dataBoxHeight);
                }
            }
        }
    }

    private void initLineStyle(int linePosition) {
        int lineColor = mLineDataSetList.get(linePosition).getColor();
        LinearGradient mLinearGradient;
        int[] colorArr;
        if (mLineDataSetList.get(linePosition).getGradientColors() != null) {
            colorArr = mLineDataSetList.get(linePosition).getGradientColors();
        } else {
            colorArr = new int[]{lineColor, lineColor, lineColor, lineColor, lineColor};
        }
        mLinearGradient = new LinearGradient(
                0,
                mMarginTopBottom,
                0,
                getMeasuredHeight(),
                colorArr,
                null,
                Shader.TileMode.CLAMP
        );

        mRectPaint.setShader(mLinearGradient);
        mRectPaint.setColor(lineColor);
        mRectPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(DensityUtil.sp2px(mContext, 12f));
        mTextPaint.setAntiAlias(true);//去除锯齿
        mRectPaint.setAntiAlias(true);//去除锯齿
    }

    private void drawRight(Canvas canvas, PointF linePoint, String drawText, float dataBoxWidth, float dataBoxHeight) {
        canvas.drawRoundRect(
                new RectF(
                        mDownX + DensityUtil.dip2px(mContext, 3f),
                        linePoint.y + DensityUtil.dip2px(mContext, 5f),
                        mDownX + dataBoxWidth + DensityUtil.dip2px(mContext, 3f),
                        linePoint.y + dataBoxHeight),
                DensityUtil.dip2px(mContext, 8f),
                DensityUtil.dip2px(mContext, 8f),
                mRectPaint);
        canvas.drawText(drawText, 0,
                drawText.length(),
                mDownX + DensityUtil.dip2px(mContext, 8f),
                linePoint.y + DensityUtil.dip2px(mContext, 21f),
                mTextPaint);
    }

    private void drawLeft(Canvas canvas, PointF linePoint, String drawText, float dataBoxWidth, float dataBoxHeight) {
        canvas.drawRoundRect(
                new RectF(
                        mDownX - dataBoxWidth - DensityUtil.dip2px(mContext, 3f),
                        linePoint.y + DensityUtil.dip2px(mContext, 5f),
                        mDownX - DensityUtil.dip2px(mContext, 3f),
                        linePoint.y + dataBoxHeight),
                DensityUtil.dip2px(mContext, 8f),
                DensityUtil.dip2px(mContext, 8f),
                mRectPaint);
        canvas.drawText(drawText, 0,
                drawText.length(),
                mDownX - dataBoxWidth + DensityUtil.dip2px(mContext, 3f),
                linePoint.y + DensityUtil.dip2px(mContext, 21f),
                mTextPaint);
    }

    /**
     * 绘制标线及底部三角形
     */
    private void drawMarkLine2(Canvas canvas) {
        if (mDownX == -1) return;
        if (mDownX < mMarginLeftRight || mDownX > mWidth - mMarginLeftRight) return;
        mVerticalPaint.setColor(Color.WHITE);
        mVerticalPaint.setStrokeWidth(2f);
        Path trianglePath = new Path();
        canvas.drawLine(
                mDownX,
                mMarginTopBottom,
                mDownX,
                mHeight - mMarginTopBottom,
                mVerticalPaint);
        trianglePath.moveTo(mDownX, mHeight - mMarginTopBottom - 20f);
        trianglePath.lineTo(mDownX - 20f, mHeight - mMarginTopBottom);
        trianglePath.lineTo(mDownX + 20f, mHeight - mMarginTopBottom);
        trianglePath.close();
        canvas.drawPath(trianglePath, mVerticalPaint);
        mDownX = -1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float downX = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = downX;
                //postInvalidateDelayed(50);
                Log.d(TAG, "onTouchEvent: ACTION_DOWN mDownX = " + mDownX);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onTouchEvent: ACTION_MOVE mDownX = " + mDownX);
                mDownX = event.getX();
                invalidate();
                //postInvalidateDelayed(50);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        //postInvalidateDelayed(50);
        return true;
    }

    /**
     * 把一般坐标转为 Android中的视图坐标
     **/
    private List<PointF> changePoint(List<PointF> oldPointFs) {
        List<PointF> pointFs = new ArrayList<>();
        float maxValueY = 0;
        float yValue;
        for (int i = 0; i < oldPointFs.size(); i++) {
            yValue = oldPointFs.get(i).y;
            if (maxValueY < yValue) maxValueY = yValue + (yValue * 0.1f);//
        }
        Log.d(TAG, "changePoint: maxValueY = " + maxValueY);
        //间隔，减去某个值是为了空出多余空间，为了画线以外，还要写坐标轴的值，除以坐标轴最大值
        //相当于缩小图像
        int blockCount = oldPointFs.size() - 1;
        float intervalX = (getMeasuredWidth() - mMarginLeftRight * 2f) / blockCount;
        float intervalY = (getMeasuredHeight() - mMarginTopBottom * 2f) / maxValueY - 0f;
        int height = getMeasuredHeight();
        PointF p;
        float x;
        float y;
        for (int i = 0; i < oldPointFs.size(); i++) {
            PointF pointF = oldPointFs.get(i);
            //最后的正负值是左移右移
            x = (pointF.x - 1) * intervalX + mMarginLeftRight;
            y = height - mMarginTopBottom - intervalY * pointF.y - DensityUtil.dip2px(mContext, 5f);
            p = new PointF(x, y);
            pointFs.add(p);
        }
        return pointFs;
    }


    /**
     * 添加数据
     *
     * @param lineDataSet 曲线参数集
     **/
    public void addLineData(LineDataSet lineDataSet) {
        if (lineDataSet.getColor() == 0) lineDataSet.setColor(mDefaultColor);
        mLineDataSetList.add(lineDataSet);
        postInvalidateDelayed(50);
        //invalidate();
    }

    public void removeLine(LineDataSet lineDataSet) {
        mLineDataSetList.remove(lineDataSet);
        postInvalidateDelayed(50);
        //invalidate();
    }

    private void initData() {
        List<PointF> aOriginPointList;
        //List<PointF> aAndroidPointList;
        //List<BezierLineData> aLineDataList;
        //List<PointF> aSelectedOriginPointList;
        mBezierLineDataList.clear();
        mOriginPointList.clear();
        //mAndroidPointList.clear();
        for (LineDataSet lineDataSet : mLineDataSetList) {
            //每一次遍历就是一条曲线数据
            aOriginPointList = lineDataSet.getOldPointFsList();
            if (aOriginPointList.size() == 0) continue;
            mOriginPointList.add(aOriginPointList);
            //aSelectedOriginPointList = getSelectedPoint(aOriginPointList);
            //mSelectedOriginPointList.add(aSelectedOriginPointList);
            //aAndroidPointList = changePoint(aSelectedOriginPointList);
            //mAndroidPointList.add(aAndroidPointList);
            //aLineDataList = getLineData(aAndroidPointList);
            //mBezierLineDataList.add(aLineDataList);
            mBezierLineDataList.add(
                    getLineData(
                            changePoint(
                                    getSelectedPoint(
                                            lineDataSet.getOldPointFsList()))));
        }
        Log.d(TAG, "initData: mOriginPointList.size = " + mOriginPointList.size());
    }

    /**
     * 设置底部时间数据
     */
    public void setxNameDataList(List<String> xNameDataList) {
        this.mXNameList = xNameDataList;
        setxNameListShow();
        postInvalidateDelayed(50);
    }

    /**
     * 设置显示的时间
     * 只显示10个时间点
     */
    private void setxNameListShow() {
        int interval = mXNameList.size() / 10 + 1;//只显示10个横坐标,的间隔
        for (int i = 0; i < mXNameList.size(); i = i + interval) {
            mXNameListShow.add(mXNameList.get(i));
        }
    }


    /**
     * 从全部数据中选中其中mBezierPointCount个数据
     */
    private List<PointF> getSelectedPoint(List<PointF> pointFList) {
        PointF pointF;
        PointF selectedPoint;
        float ySum = 0;
        float averageY;
        int interval = pointFList.size() / mBezierPointCount + 1;
        List<PointF> selectedPointList = new ArrayList<>();
        if (pointFList.size() == 0) return selectedPointList;
        int j = 0;
        for (int i = 0; i < pointFList.size(); i++) {
            pointF = pointFList.get(i);
            ySum += pointF.y;
            if (i % interval == 0) {
                j++;
                averageY = ySum / interval;
                //selectedPoint = new PointF(j, averageY);//求平均
                selectedPoint = new PointF(j, pointF.y);//不求平均
                selectedPointList.add(selectedPoint);
                ySum = 0;
            }
        }
        Log.d(TAG, "getSelectedPoint: selected count = " + selectedPointList.size());

        //暂时办法-解决不够n个点
        if (selectedPointList.size() < mBezierPointCount) {
            int curPosition;
            for (curPosition = selectedPointList.size(); curPosition < mBezierPointCount; curPosition++) {
                selectedPointList.add(new PointF(curPosition + 1, selectedPointList.get(selectedPointList.size() - 1).y));
            }
        }
        Log.d(TAG, "getSelectedPoint: after selected count = " + selectedPointList.size());
        return selectedPointList;
    }

    /**
     * 获取每一段曲线所需要的点集
     */
    private List<BezierLineData> getLineData(List<PointF> pointList) {
        float t = 0.5f;
        List<BezierLineData> lineDataList = new ArrayList<>();
        PointF startP;
        PointF endP;
        PointF cp1;
        PointF cp2;
        BezierLineData lineData;
        for (int i = 0; i < pointList.size() - 1; i++) {
            startP = pointList.get(i);
            endP = pointList.get(i + 1);
            cp1 = new PointF();
            cp1.x = startP.x + (endP.x - startP.x) * t;
            cp1.y = startP.y;
            cp2 = new PointF();
            cp2.x = startP.x + (endP.x - startP.x) * (1 - t);
            cp2.y = endP.y;
            lineData = new BezierLineData(startP, endP, cp1, cp2);
            lineDataList.add(lineData);
        }
        return lineDataList;
    }

}
