package com.github.mikephil.charting.stockChart.markerView;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.stockChart.model.KLineDataModel;
import com.github.mikephil.charting.utils.DataTimeUtil;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.NumberUtils;

import java.text.DecimalFormat;

public class LeftMarkerView extends MarkerView {
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    private TextView markerTv;
    private TextView markerTime;
    private TextView markerMax;
    private TextView markerMin;
    private float num;
    private KLineDataModel data;
    private int precision;

    public LeftMarkerView(Context context, int layoutResource, int precision) {
        super(context, layoutResource);
        this.precision = precision;
        markerTv = (TextView) findViewById(R.id.marker_tv);
        markerTime = (TextView) findViewById(R.id.marker_time);
        markerMax = (TextView) findViewById(R.id.marker_max);
        markerMin = (TextView) findViewById(R.id.marker_min);

    }

    public void setNumber(float num) {
        this.num = num;
    }

    public void setData(KLineDataModel data){
        this.data=data;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        markerTime.setText(DataTimeUtil.secToDateTime(data.getDateMills()));
        markerTv.setText(NumberUtils.keepPrecisionR(num, precision));
        markerMin.setText(NumberUtils.keepPrecisionR(data.getLow(), precision));
        markerMax.setText(NumberUtils.keepPrecisionR(data.getHigh(), precision));
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
