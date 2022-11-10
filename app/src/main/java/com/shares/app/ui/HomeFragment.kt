package com.shares.app.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jaeger.library.StatusBarUtil
import com.shares.app.R
import com.shares.app.base.BaseModel
import com.shares.app.data.LineData
import com.shares.app.databinding.FragmentHomeBinding
import com.shares.app.databinding.FragmentLoginBinding
import com.shares.app.extension.toDateStr
import com.shares.app.util.LineView
import com.shares.app.util.MyLineChartRenderer
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.view.ColumnChartView
import lecho.lib.hellocharts.view.LineChartView
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : BaseFragment() {
    private lateinit var mBinding: FragmentHomeBinding
    private val mVm: HomeViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentHomeBinding.inflate(inflater)
        mBinding.lifecycleOwner = this
        mBinding.vm = mVm
        mVm.loadData()
        StatusBarUtil.setTranslucentForImageViewInFragment(requireActivity(),0,mBinding.top)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mVm.event.observe(viewLifecycleOwner, { event ->
            when (event) {


            }
        })
        mVm.listData.observe(viewLifecycleOwner, { listData ->
            initLine(
                R.color.bottom_check,
                listData,
                0,
                mBinding.chart1
            )

            initLine(
                R.color.bottom_check,
                listData,
                1,
                mBinding.chart2
            )
        })
    }

    fun initLine(
        color: Int,
        listData: List<LineData>,
        mIndex:Int,
        chartView: LineView
    ) {
        val xList = arrayListOf<String>()
        for (value in listData) {
            xList.add(value.getTimeDayStr())
        }
        val mAxisXValues = mutableListOf<AxisValue>()
        mAxisXValues.add(AxisValue(0f).setLabel(requireContext().resources.getString(R.string.home_today_lable)))
        for ((index, value) in xList.withIndex()) {
            mAxisXValues.add(AxisValue(mAxisXValues.size.toFloat()).setLabel(value));
        }
        val values: MutableList<PointValue> = ArrayList()
        val yValues = mutableListOf<AxisValue>()
        var maxY = 0f;
        for ((index, value) in listData.withIndex()) {
            if (maxY < value.getDataFromIndex(mIndex)) {
                maxY = value.getDataFromIndex(mIndex)
            }
            values.add(
                PointValue(
                    (index + 1).toFloat(),
                    value.getDataFromIndex(mIndex)
                ).setLabel(String.format(requireContext().resources.getString(R.string.home_format1),value.getTimeStr(),value.wOption.toString()))
            )
        }
        val df = DecimalFormat("######0")
        for(index in 1..7){
            val temp=df.format(index*maxY/7)
            val value1 = AxisValue(temp.toFloat());
            value1.setLabel(temp)
            yValues.add(value1);
        }
        val line = Line(values)
        val lines: MutableList<Line> = mutableListOf()
        line.setShape(ValueShape.CIRCLE)
        line.setColor(requireContext().resources.getColor(color))
        line.setPointColor(requireContext().resources.getColor(color))
        line.setHasLabelsOnlyForSelected(true)

        line.setPointRadius(4)
        line.setCubic(true)
        line.setFilled(true)
        line.setHasLines(true)
        line.setHasPoints(true)
        val data = LineChartData()
        data.lines = lines
        data.setValueLabelsTextColor(requireContext().resources.getColor(color))
        data.valueLabelBackgroundColor = requireContext().resources.getColor(R.color.white)
        data.isValueLabelBackgroundAuto = false

        lines.add(line)
        val axisX = Axis(); //X轴
        axisX.setTextColor(requireContext().resources.getColor(R.color.f878e91));  //设置字体颜色
        axisX.setTextSize(12);//设置字体大小
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
        val axisY = Axis(); //Y轴
        axisY.setTextSize(12);//设置字体大小
        axisY.setHasLines(true); //x 轴分割线
        axisY.setValues(yValues);
        axisY.setTextColor(requireContext().resources.getColor(R.color.f878e91));  //设置字体颜色
        data.setAxisYLeft(axisY);  //Y轴设置在左边
        chartView.lineChartData = data
        val viewPort = chartView.maximumViewport
        viewPort.left = -0f
        viewPort.top =10+ chartView.currentViewport.top
        viewPort.bottom = 0f
        chartView.currentViewport = viewPort
        chartView.setDelayHide()
    }

    override fun getBaseModel(): BaseModel {
        return mVm
    }
}