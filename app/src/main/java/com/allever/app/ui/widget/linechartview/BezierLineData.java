package com.allever.app.ui.widget.linechartview;

import android.graphics.PointF;

/**
 * Created by allever on 17-9-28.
 *
 * 每一段曲线用到的数据点集
 */

public class BezierLineData {
    private PointF startP;
    private PointF endP;
    private PointF cp1;
    private PointF cp2;

    public BezierLineData(PointF startP, PointF endP, PointF cp1, PointF cp2){
        this.startP = startP;
        this.endP = endP;
        this.cp1 = cp1;
        this.cp2 = cp2;
    }

    public PointF getStartP() {
        return startP;
    }

    public void setStartP(PointF startP) {
        this.startP = startP;
    }

    public PointF getEndP() {
        return endP;
    }

    public void setEndP(PointF endP) {
        this.endP = endP;
    }

    public PointF getCp1() {
        return cp1;
    }

    public void setCp1(PointF cp1) {
        this.cp1 = cp1;
    }

    public PointF getCp2() {
        return cp2;
    }

    public void setCp2(PointF cp2) {
        this.cp2 = cp2;
    }
}
