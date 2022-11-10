package com.github.mikephil.charting.stockChart;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.github.mikephil.charting.R;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.VolFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.stockChart.markerView.BarBottomMarkerView;
import com.github.mikephil.charting.stockChart.charts.CandleCombinedChart;
import com.github.mikephil.charting.stockChart.charts.CoupleChartGestureListener;
import com.github.mikephil.charting.stockChart.markerView.KRightMarkerView;
import com.github.mikephil.charting.stockChart.markerView.LeftMarkerView;
import com.github.mikephil.charting.stockChart.charts.MyCombinedChart;
import com.github.mikephil.charting.stockChart.dataManage.KLineDataManage;
import com.github.mikephil.charting.stockChart.enums.TimeType;
import com.github.mikephil.charting.utils.CommonUtil;
import com.github.mikephil.charting.utils.DataTimeUtil;
import com.github.mikephil.charting.utils.NumberUtils;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * K线
 */
public class KLineChart extends BaseChart {

    private Context mContext;
    private CandleCombinedChart candleChart;

    private XAxis  xAxisK;
    private YAxis  axisLeftK;
    private YAxis  axisRightK;

    private KLineDataManage kLineData;

    private int maxVisibleXCount = 100;
    private boolean isFirst = true;//是否是第一次加载数据
    private int zbColor[];

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            candleChart.setAutoScaleMinMaxEnabled(true);
            candleChart.notifyDataSetChanged();
            candleChart.invalidate();
        }
    };

    public KLineChart(Context context) {
        this(context, null);
    }

    public KLineChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_kline, this);
        candleChart = (CandleCombinedChart) findViewById(R.id.candleChart);

        zbColor = new int[]{ContextCompat.getColor(context, R.color.ma5), ContextCompat.getColor(context, R.color.ma10), ContextCompat.getColor(context, R.color.ma20)};
    }

    /**
     * 初始化图表数据
     */
    public void initChart(boolean landscape) {
        this.landscape = landscape;
        //蜡烛图
        candleChart.setDrawBorders(true);
        candleChart.setBorderWidth(0.7f);
        candleChart.setBorderColor(ContextCompat.getColor(mContext, R.color.border_color));
        candleChart.setDragEnabled(true);//是否可拖动
        candleChart.setScaleXEnabled(true);//x轴方向是否可放大缩小
        candleChart.setScaleYEnabled(false);//Y轴方向是否可放大缩小
        candleChart.setHardwareAccelerationEnabled(true);
        Legend mChartKlineLegend = candleChart.getLegend();
        mChartKlineLegend.setEnabled(false);
        //k线滚动系数设置，控制滚动惯性
        candleChart.setDragDecelerationEnabled(true);
        candleChart.setDragDecelerationFrictionCoef(0.6f);//0.92持续滚动时的速度快慢，[0,1) 0代表立即停止。
        candleChart.setDoubleTapToZoomEnabled(false);
        candleChart.setNoDataText(getResources().getString(R.string.loading));



        //蜡烛图X轴
        xAxisK = candleChart.getXAxis();
        xAxisK.setDrawLabels(false);
        xAxisK.setLabelCount(landscape ? 5 : 4, true);
        xAxisK.setDrawGridLines(true);
        xAxisK.setDrawAxisLine(false);
        xAxisK.setGridLineWidth(0.7f);
        xAxisK.setGridColor(ContextCompat.getColor(mContext, R.color.grid_color));
        xAxisK.setTextColor(ContextCompat.getColor(mContext, R.color.label_text));
        xAxisK.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisK.setAvoidFirstLastClipping(true);
        xAxisK.setDrawLimitLinesBehindData(true);

        //蜡烛图左Y轴
        axisLeftK = candleChart.getAxisLeft();
        axisLeftK.setDrawGridLines(true);
        axisLeftK.setDrawAxisLine(false);
        axisLeftK.setDrawLabels(true);
        axisLeftK.setLabelCount(5, true);
        axisLeftK.enableGridDashedLine(CommonUtil.dip2px(mContext, 4), CommonUtil.dip2px(mContext, 3), 0);
        axisLeftK.setTextColor(ContextCompat.getColor(mContext, R.color.axis_text));
        axisLeftK.setGridColor(ContextCompat.getColor(mContext, R.color.grid_color));
        axisLeftK.setGridLineWidth(0.7f);
        axisLeftK.setValueLineInside(true);
        axisLeftK.setDrawTopBottomGridLine(false);
        axisLeftK.setPosition(landscape ? YAxis.YAxisLabelPosition.OUTSIDE_CHART : YAxis.YAxisLabelPosition.INSIDE_CHART);
        axisLeftK.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return NumberUtils.keepPrecisionR(value, precision);
            }
        });

        //蜡烛图右Y轴
        axisRightK = candleChart.getAxisRight();
        axisRightK.setDrawLabels(false);
        axisRightK.setDrawGridLines(false);
        axisRightK.setDrawAxisLine(false);
        axisRightK.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);



        //手势联动监听
        gestureListenerCandle = new CoupleChartGestureListener(candleChart, new Chart[]{});
        candleChart.setOnChartGestureListener(gestureListenerCandle);


        //移动十字标数据监听
        candleChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                candleChart.highlightValue(h);

                updateText((int) e.getX(), true);
            }

            @Override
            public void onNothingSelected() {

                updateText(kLineData.getKLineDatas().size() - 1, false);
            }
        });



    }

    /**
     * 设置K线数据
     */
    public void setDataToChart(KLineDataManage data) {
        kLineData = data;
        if (kLineData.getKLineDatas().size() == 0) {
            candleChart.setNoDataText(getResources().getString(R.string.no_data));

            candleChart.invalidate();

            return;
        }


        if (data.getAssetId().endsWith(".HK") && !data.getAssetId().contains("IDX")) {
            setPrecision(3);
        } else {
            setPrecision(2);
        }

        CombinedData candleChartData;
        CombinedData barChartData;
        CandleDataSet candleDataSet = null;
        /*************************************蜡烛图数据*****************************************************/
        candleDataSet = kLineData.getCandleDataSet();
        candleDataSet.setPrecision(precision);
        candleChartData = new CombinedData();
        candleChartData.setData(new CandleData(candleDataSet));
        candleChartData.setData(new LineData(kLineData.getLineDataMA()));
        candleChart.setData(candleChartData);
        /*************************************成交量数据*****************************************************/
        barChartData = new CombinedData();
        barChartData.setData(new BarData(kLineData.getVolumeDataSet()));
        barChartData.setData(new LineData());
        barChartData.setData(new CandleData());


        if (isFirst) {


            //请注意，修改视口的所有方法需要在为Chart设置数据之后调用。
            //设置当前视图四周的偏移量。 设置这个，将阻止图表自动计算它的偏移量。使用 resetViewPortOffsets()撤消此设置。
            float left_right = 0;
            if (landscape) {
                float volwidth = Utils.calcTextWidth(mPaint, "###0.00");
                float pricewidth = Utils.calcTextWidth(mPaint, NumberUtils.keepPrecision(data.getPreClosePrice() + "", precision) + "#");
                left_right = CommonUtil.dip2px(mContext, pricewidth > volwidth ? pricewidth : volwidth);
                candleChart.setViewPortOffsets(left_right, CommonUtil.dip2px(mContext, 15), CommonUtil.dip2px(mContext, 5), 0);

            } else {
                left_right = CommonUtil.dip2px(mContext, 5);
                candleChart.setViewPortOffsets(left_right, CommonUtil.dip2px(mContext, 15), CommonUtil.dip2px(mContext, 5), 0);

            }

            setMarkerView(kLineData);

            updateText(kLineData.getKLineDatas().size() - 1, false);

            float xScale = calMaxScale(kLineData.getxVals().size());
            ViewPortHandler viewPortHandlerCombin = candleChart.getViewPortHandler();
            viewPortHandlerCombin.setMaximumScaleX(50);
            //根据所给的参数进行放大或缩小。 参数 x 和 y 是变焦中心的坐标（单位：像素）。 记住，1f = 无放缩 。
            candleChart.zoom(xScale, 0, 0, 0);



            candleChart.getXAxis().setAxisMinimum(candleChartData.getXMin()-0.5f);

            candleChart.getXAxis().setAxisMaximum(kLineData.getKLineDatas().size() < 70 ? 70 : candleChartData.getXMax() + 0.5f);
            if (kLineData.getKLineDatas().size() > 70) {
                //moveViewTo(...) 方法会自动调用 invalidate()
                candleChart.moveViewToX(kLineData.getKLineDatas().size() - 1);
            }
            isFirst = false;
        }
        handler.sendEmptyMessageDelayed(0, 100);
    }

    protected int chartType1 = 1;
    protected int chartTypes1 = 5;



    /**
     * 动态增加一个点数据
     *
     * @param kLineData 最新数据集
     */
    public void dynamicsAddOne(KLineDataManage kLineData) {
        int size = kLineData.getKLineDatas().size();
        CombinedData candleChartData = candleChart.getData();
        CandleData candleData = candleChartData.getCandleData();
        ICandleDataSet candleDataSet = candleData.getDataSetByIndex(0);
        int i = size - 1;
        candleDataSet.addEntry(new CandleEntry(i + kLineData.getOffSet(), (float) kLineData.getKLineDatas().get(i).getHigh(), (float) kLineData.getKLineDatas().get(i).getLow(), (float) kLineData.getKLineDatas().get(i).getOpen(), (float) kLineData.getKLineDatas().get(i).getClose()));
        kLineData.getxVals().add(DataTimeUtil.secToDate(kLineData.getKLineDatas().get(i).getDateMills()));
        candleChart.getXAxis().setAxisMaximum(kLineData.getKLineDatas().size() < 70 ? 70 : candleChartData.getXMax() + kLineData.getOffSet());


        candleChart.notifyDataSetChanged();

        if (kLineData.getKLineDatas().size() > 70) {
            //moveViewTo(...) 方法会自动调用 invalidate()
            candleChart.moveViewToX(kLineData.getKLineDatas().size() - 1);
        } else {
            candleChart.invalidate();
        }
    }

    /**
     * 动态更新最后一点数据 最新数据集
     *
     * @param kLineData
     */
    public void dynamicsUpdateOne(KLineDataManage kLineData) {
        int size = kLineData.getKLineDatas().size();
        int i = size - 1;
        CombinedData candleChartData = candleChart.getData();
        CandleData candleData = candleChartData.getCandleData();
        ICandleDataSet candleDataSet = candleData.getDataSetByIndex(0);
        candleDataSet.removeEntry(i);

        candleDataSet.addEntry(new CandleEntry(i + kLineData.getOffSet(), (float) kLineData.getKLineDatas().get(i).getHigh(), (float) kLineData.getKLineDatas().get(i).getLow(), (float) kLineData.getKLineDatas().get(i).getOpen(), (float) kLineData.getKLineDatas().get(i).getClose()));

        candleChart.notifyDataSetChanged();
        candleChart.invalidate();
    }

    public void setMarkerView(KLineDataManage kLineData) {
        LeftMarkerView leftMarkerView = new LeftMarkerView(mContext, R.layout.my_markerview, precision);
        KRightMarkerView rightMarkerView = new KRightMarkerView(mContext, R.layout.my_markerview, precision);
        candleChart.setMarker(leftMarkerView, rightMarkerView, kLineData);
    }



    public float calMaxScale(float count) {
        float xScale = 1;
        if (count >= 800) {
            xScale = 12f;
        } else if (count >= 500) {
            xScale = 8f;
        } else if (count >= 300) {
            xScale = 5.5f;
        } else if (count >= 150) {
            xScale = 2f;
        } else if (count >= 100) {
            xScale = 1.5f;
        } else {
            xScale = 0.1f;
        }
        return xScale;
    }

    //移动十字标更新数据
    public void updateText(int index, boolean isSelect) {
        if (mHighlightValueSelectedListener != null) {
            mHighlightValueSelectedListener.onKHighlightValueListener(kLineData, index, isSelect);
        }
        //更新MA均线数据
        candleChart.setDescriptionCustom(zbColor, new String[]{"MA5:" + NumberUtils.keepPrecision(kLineData.getKLineDatas().get(index).getMa5(), 3), "MA10:" + NumberUtils.keepPrecision(kLineData.getKLineDatas().get(index).getMa10(), 3), "MA20:" + NumberUtils.keepPrecision(kLineData.getKLineDatas().get(index).getMa20(), 3)});
    }


}
