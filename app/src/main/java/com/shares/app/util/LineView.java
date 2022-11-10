package com.shares.app.util;

import android.content.Context;
import android.util.AttributeSet;

import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.renderer.LineChartRenderer;
import lecho.lib.hellocharts.view.LineChartView;

public class LineView extends LineChartView implements MyLineChartDataProvider {
    private  MyLineChartRenderer lineChartRenderer;
    public LineView(Context context) {
        this(context, null, 0);
    }

    public LineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        lineChartRenderer=new MyLineChartRenderer(context, this, this);
        setChartRenderer(lineChartRenderer);
        setLineChartData(LineChartData.generateDummyData());
    }

    @Override
    public void notifyView() {
        invalidate();
    }

    public void setDelayHide(){
        lineChartRenderer.setDelayHide(true);
    }

}
