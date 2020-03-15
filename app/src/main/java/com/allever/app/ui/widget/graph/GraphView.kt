package com.allever.app.ui.widget.graph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.allever.lib.common.util.DisplayUtils
import com.allever.lib.common.util.log
import kotlin.math.max

class GraphView : View {
    private val TAG = GraphView::class.java.simpleName

    private val DEFAULT_PADDING = DisplayUtils.dip2px(5)
    private var mPaddingLeft = 0
    private var mPaddingTop = 0
    private var mPaddingRight = 0
    private var mPaddingBottom = 0

    private var mWidth = 0
    private var mHeight = 0

    private var mLineStartX = 0
    private var mLineEndX = 0

    private val screenWidth = DisplayUtils.getScreenWidth().toFloat()
    private val screenHeight = DisplayUtils.getScreenHeight().toFloat()

    private var mLineDataList = mutableListOf<LineData>()
    private var mXValueList = mutableListOf<String>()

    private var mPaint = Paint()

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    constructor(context: Context, attributeSet: AttributeSet?, theme: Int) : super(
        context,
        attributeSet,
        theme
    ) {
        init()
        initTestData()
    }

    private fun init() {

        mWidth = screenWidth.toInt()
        mHeight = (mWidth * (screenWidth / screenHeight)).toInt()
        log("width = $mWidth")
        log("height = $mHeight")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mPaddingLeft = max(paddingLeft, DEFAULT_PADDING)
        mPaddingTop = max(paddingTop, DEFAULT_PADDING)
        mPaddingRight = max(paddingRight, DEFAULT_PADDING)
        mPaddingBottom = max(paddingBottom, DEFAULT_PADDING)

        when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> {
                mWidth = MeasureSpec.getSize(widthMeasureSpec)
            }
            else -> {
            }
        }

        mHeight = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> {
                MeasureSpec.getSize(heightMeasureSpec)
            }
            else -> {
                (mWidth * (screenWidth / screenHeight)).toInt()
            }
        }
        setMeasuredDimension(mWidth, mHeight)

        log("onMeasure width = $mWidth, height = $mHeight")
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(Color.parseColor("#ffffff"))

        drawVLine(canvas)
        drawHLine(canvas)

        drawXLabel(canvas)
    }

    public fun setLineData(data: MutableList<LineData>) {
        mLineDataList.clear()
        mLineDataList.addAll(data)
        invalidate()
    }

    public fun addLine(data: LineData) {
        mLineDataList.add(data)
        invalidate()
    }

    private fun setAxisStyle() {
        mPaint.isAntiAlias = true
        mPaint.color = resources.getColor(android.R.color.darker_gray)
        mPaint.style = Paint.Style.FILL
        mPaint.strokeWidth = DisplayUtils.dip2px(1).toFloat()
    }

    private fun drawVLine(canvas: Canvas?) {
        setAxisStyle()
        val startX = mPaddingLeft + DisplayUtils.dip2px(20).toFloat()
        val startY = mPaddingTop + DisplayUtils.dip2px(30).toFloat()
        val endX = startX
        val endY = mHeight - mPaddingBottom - DisplayUtils.dip2px(20).toFloat()
        canvas?.drawLine(startX, startY, endX, endY, mPaint)
    }

    private fun drawHLine(canvas: Canvas?) {
        setAxisStyle()
        val startX = mPaddingLeft + DisplayUtils.dip2px(20).toFloat() - DisplayUtils.dip2px(1)
        val startY = mHeight - mPaddingBottom - DisplayUtils.dip2px(20).toFloat()
        val endX = mWidth - mPaddingRight - DisplayUtils.dip2px(20).toFloat()
        val endY = startY
        canvas?.drawLine(startX, startY, endX, endY, mPaint)

        mLineStartX = startX.toInt()
        mLineEndX = endY.toInt()
    }

    private fun drawXLabel(canvas: Canvas?) {

    }

    private fun log(msg: String) {
        log(TAG, msg)
    }

    private fun initTestData() {
        val yValue = mutableListOf<Float>()
        yValue.add(3f)
        yValue.add(5f)
        yValue.add(3f)
        yValue.add(5f)
        yValue.add(3f)
        yValue.add(5f)
        yValue.add(3f)

        val xValue = mutableListOf<String>()
        xValue.add("03-08")
        xValue.add("03-09")
        xValue.add("03-10")
        xValue.add("03-11")
        xValue.add("03-12")
        xValue.add("03-13")
        xValue.add("03-14")

        val label = "google"

        val startColor = Color.parseColor("#64b5f6")
        val endColor = Color.parseColor("#2196f3")

        val lineData = LineData()
//        lineData.xValue = xValue
        lineData.yValue = yValue
        lineData.label = label
        lineData.color = endColor

        mLineDataList.add(lineData)
        mXValueList.addAll(xValue)

    }

}