package com.allever.app.ui.widget.linechartview;

import android.graphics.PointF;

import java.util.List;

/**
 * Created by allever on 17-8-10.
 * 每一条线对应一个对象
 */

public class LineDataSet {
    private int color;//颜色，
    private int[] gradientColors;//渐变色数组
    private List<PointF> oldPointFsList;//原始点
//    private SportAnalysisType sportAnalysisType;//参数类型

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int[] getGradientColors() {
        return gradientColors;
    }

    public void setGradientColors(int[] gradientColors) {
        this.gradientColors = gradientColors;
    }

    public List<PointF> getOldPointFsList() {
        return oldPointFsList;
    }

    public void setOldPointFsList(List<PointF> oldPointFsList) {
        this.oldPointFsList = oldPointFsList;
    }

//    public SportAnalysisType getSportAnalysisType() {
//        return sportAnalysisType;
//    }
//
//    public void setSportAnalysisType(SportAnalysisType sportAnalysisType) {
//        this.sportAnalysisType = sportAnalysisType;
//    }
}
