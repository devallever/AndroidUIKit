package com.allever.app.ui

import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import com.allever.app.ui.widget.linechartview.LineDataSet
import com.allever.lib.common.app.BaseActivity
import com.allever.lib.common.util.toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = ArrayList<String>()

        list.add("3333")
        list.add("1")
        list.add("22222222222222222222222222222222222222222222")
        list.add("清空万里")
        list.add("两条相交线")
        list.add("短途")
        list.add("希望在明天会更好")

        val autoAdapter = MyAutoAdapter(this)
        autoAdapter.setOnItemClickListener { view, text ->
            toast(text)
        }
        autoAdapter.setData(list)
        autoLayout.setAdapter(autoAdapter)

//        lineCharView.setxNameDataList()

        initData()
    }

    private fun initData() {
        val speedPoint = mutableListOf<PointF>()
        speedPoint.add(PointF(1f, 1f))
        speedPoint.add(PointF(2f, 2f))
        speedPoint.add(PointF(3f, 3f))
        speedPoint.add(PointF(4f, 4f))
        speedPoint.add(PointF(5f, 5f))
        speedPoint.add(PointF(6f, 6f))
        speedPoint.add(PointF(7f, 7f))
        speedPoint.add(PointF(8f, 8f))
        speedPoint.add(PointF(9f, 9f))
        speedPoint.add(PointF(10f, 3f))
        speedPoint.add(PointF(11f, 2f))
        speedPoint.add(PointF(12f, 9f))
        val mSpeedLineDataSet = LineDataSet()
        mSpeedLineDataSet.oldPointFsList = speedPoint
        mSpeedLineDataSet.color = resources.getColor(R.color.colorPrimary)
        mSpeedLineDataSet.gradientColors = intArrayOf(
            0xffffffff.toInt(),
            resources.getColor(R.color.colorAccent),
            resources.getColor(R.color.colorPrimary),
            resources.getColor(R.color.colorPrimary),
            resources.getColor(R.color.colorPrimary)
        )
        lineCharView.addLineData(mSpeedLineDataSet)


        val runSpeedPoint = mutableListOf<PointF>()
        runSpeedPoint.add(PointF(1f, 50f))
        runSpeedPoint.add(PointF(2f, 200f))
        runSpeedPoint.add(PointF(3f, 10f))
        runSpeedPoint.add(PointF(4f, 200f))
        runSpeedPoint.add(PointF(5f, 10f))
        runSpeedPoint.add(PointF(6f, 200f))
        runSpeedPoint.add(PointF(7f, 10f))
        runSpeedPoint.add(PointF(8f, 200f))
        runSpeedPoint.add(PointF(9f, 10f))
        runSpeedPoint.add(PointF(10f, 200f))
        runSpeedPoint.add(PointF(11f, 10f))
        runSpeedPoint.add(PointF(12f, 50f))
        val mRunSpeedLineDataSet = LineDataSet()
        mRunSpeedLineDataSet.oldPointFsList = runSpeedPoint
        mRunSpeedLineDataSet.color = Color.YELLOW
        mRunSpeedLineDataSet.gradientColors =
            intArrayOf(0xffffffff.toInt(), Color.GREEN, Color.YELLOW, Color.YELLOW, Color.YELLOW)
        lineCharView.addLineData(mRunSpeedLineDataSet)


        val oppositionPoint = mutableListOf<PointF>()
        oppositionPoint.add(PointF(1f, 100f))
        oppositionPoint.add(PointF(2f, 150f))
        oppositionPoint.add(PointF(3f, 30f))
        oppositionPoint.add(PointF(4f, 60f))
        oppositionPoint.add(PointF(5f, 80f))
        oppositionPoint.add(PointF(6f, 130f))
        oppositionPoint.add(PointF(7f, 200f))
        oppositionPoint.add(PointF(8f, 160f))
        oppositionPoint.add(PointF(9f, 20f))
        oppositionPoint.add(PointF(10f, 85f))
        oppositionPoint.add(PointF(11f, 200f))
        oppositionPoint.add(PointF(12f, 70f))
        val mOppositionLineDataSet = LineDataSet()
        mOppositionLineDataSet.setOldPointFsList(oppositionPoint)
        mOppositionLineDataSet.setColor(resources.getColor(R.color.colorAccent))
        lineCharView.addLineData(mOppositionLineDataSet)



        val powerPoint = mutableListOf<PointF>()
        powerPoint.add(PointF(1f, 100f))
        powerPoint.add(PointF(2f, 140f))
        powerPoint.add(PointF(3f, 100f))
        powerPoint.add(PointF(4f, 140f))
        powerPoint.add(PointF(5f, 100f))
        powerPoint.add(PointF(6f, 140f))
        powerPoint.add(PointF(7f, 100f))
        powerPoint.add(PointF(8f, 140f))
        powerPoint.add(PointF(9f, 100f))
        powerPoint.add(PointF(10f, 140f))
        powerPoint.add(PointF(11f, 100f))
        powerPoint.add(PointF(12f, 140f))
        val mPowerLineDataSet = LineDataSet()
        mPowerLineDataSet.oldPointFsList = powerPoint
        mPowerLineDataSet.color = Color.RED
        mPowerLineDataSet.gradientColors =
            intArrayOf(0xffffffff.toInt(), Color.RED, Color.RED, Color.RED, Color.RED)
        lineCharView.addLineData(mPowerLineDataSet)


        val heartPoint = mutableListOf<PointF>()
        heartPoint.add(PointF(1f, 1f))
        heartPoint.add(PointF(2f, 10f))
        heartPoint.add(PointF(3f, 2f))
        heartPoint.add(PointF(4f, 1f))
        heartPoint.add(PointF(5f, 3f))
        heartPoint.add(PointF(6f, 1f))
        heartPoint.add(PointF(7f, 4f))
        heartPoint.add(PointF(8f, 6f))
        heartPoint.add(PointF(9f, 5f))
        heartPoint.add(PointF(10f, 6f))
        heartPoint.add(PointF(11f, 3f))
        heartPoint.add(PointF(12f, 2f))
        val mHeartLineDataSet = LineDataSet()
        mHeartLineDataSet.oldPointFsList = heartPoint
        mHeartLineDataSet.color = Color.BLUE
        mHeartLineDataSet.gradientColors =
            intArrayOf(0xffffffff.toInt(), Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE)
        lineCharView.addLineData(mHeartLineDataSet)



        val tagList = listOf("1:00",
            "1:00",
            "1:00",
            "1:00",
            "1:00",
            "1:00",
            "1:00",
            "1:00",
            "1:00",
            "1:00",
            "1:00",
            "1:00"
            )
        lineCharView.setxNameDataList(tagList)
    }
}
