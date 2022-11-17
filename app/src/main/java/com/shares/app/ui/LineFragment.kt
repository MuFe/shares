package com.shares.app.ui

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.lifecycle.ViewModelProviders
import com.github.wangyiqian.stockchart.DEFAULT_K_CHART_HIGHEST_AND_LOWEST_LABEL_LINE_LENGTH
import com.github.wangyiqian.stockchart.DEFAULT_K_CHART_HIGHEST_AND_LOWEST_LABEL_LINE_STROKE_WIDTH
import com.github.wangyiqian.stockchart.DEFAULT_K_CHART_HIGHEST_AND_LOWEST_LABEL_TEXT_SIZE
import com.github.wangyiqian.stockchart.StockChartConfig
import com.github.wangyiqian.stockchart.childchart.kchart.KChartConfig
import com.github.wangyiqian.stockchart.childchart.kchart.KChartFactory
import com.github.wangyiqian.stockchart.childchart.timebar.TimeBarConfig
import com.github.wangyiqian.stockchart.entities.FLAG_EMPTY
import com.github.wangyiqian.stockchart.entities.Highlight
import com.github.wangyiqian.stockchart.entities.IKEntity
import com.github.wangyiqian.stockchart.entities.containFlag
import com.github.wangyiqian.stockchart.index.Index
import com.github.wangyiqian.stockchart.listener.OnHighlightListener
import com.github.wangyiqian.stockchart.util.NumberFormatUtil
import com.shares.app.R
import com.shares.app.base.BaseModel
import com.shares.app.databinding.FragmentOneBinding
import com.shares.app.extension.toDateStr
import com.shares.app.util.KeyboardUtil
import com.shares.app.view.LevelPopupWindow
import com.shares.app.view.custom.CustomChartConfig
import com.shares.app.view.custom.CustomChartFactory
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat

class LineFragment(val mType: Int) : BaseFragment() {
    private lateinit var mBinding: FragmentOneBinding
    private lateinit var mVm: LineViewModel
    private val keyboardUtil:KeyboardUtil by inject()
    private var kChartType: KChartConfig.KChartType = KChartConfig.KChartType.CANDLE()
    private var kChartIndex: Index? = Index.MA()
    // K线图工厂与配置
    private var kChartFactory: KChartFactory? = null
    private val kChartConfig = KChartConfig(kChartType = kChartType, index = kChartIndex)
    // 时间条图工厂与配置
    private var timeBarFactory: CustomChartFactory? = null
    private val timeBarConfig = CustomChartConfig()
    // 总配置
    private val stockChartConfig = StockChartConfig()
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            mVm.hideHight.value=true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mVm= ViewModelProviders.of(requireActivity())[LineViewModel::class.java]
        mBinding = FragmentOneBinding.inflate(inflater)
        mBinding.lifecycleOwner = this
        mBinding.vm = mVm
        val lay=mBinding.market.layoutParams as RelativeLayout.LayoutParams
        lay.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        lay.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        initKChart()
        initTimeBar()
        stockChartConfig.apply {
            // 将需要显示的子图的工厂添加进StockChart配置
            addChildCharts(
                kChartFactory!!,
                timeBarFactory!!,
            )
            scaleAble=true
            scrollAble=true
            overScrollAble=false
            // 最大缩放比例
            scaleFactorMax = 2f

            scrollSmoothly = false

            // 最小缩放比例
            scaleFactorMin = 0.5f
//            gridLineStrokeWidth = 0.5f
            gridLineColor=requireContext().resources.getColor(R.color.line)
            // 网格线设置
            gridVerticalLineCount = 3
            gridHorizontalLineCount = 4
            horizontalGridLineTopOffsetCalculator={
                0f
            }
            horizontalGridLineSpaceCalculator={
                (  keyboardUtil.dp2px(requireContext(), 250f)/4).toFloat()
            }
            backgroundColor=requireContext().resources.getColor(R.color.white)
        }


        // 绑定配置
        mBinding.stockChart.setConfig(stockChartConfig)
        return mBinding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mVm.event.observe(viewLifecycleOwner, { event ->

        })
        mVm.listData.observe(viewLifecycleOwner, { listData ->
           var temp:List<IKEntity>?=null
            var lastTime=0L
            if(stockChartConfig.getKEntitiesSize()!=0){
                val last=stockChartConfig.kEntities.last()
                lastTime=last.getTime()
            }
            when(mType){
                0->{
                    temp=mVm.parseData(15*60*1000,listData,lastTime)
                }
                1->{
                    temp=mVm.parseData(60*60*1000,listData,lastTime)
                }
                2->{
                    if(lastTime==0L){
                        temp=mVm.dayListData.value.orEmpty()+mVm.parseDayData(listData,lastTime)
                    }else{
                        temp=mVm.parseDayData(listData,lastTime)
                    }
                }
                3->{
                    if(lastTime==0L){
                        temp=mVm.weekListData.value.orEmpty()+mVm.parseDayData(listData,lastTime)
                    }else{
                        temp=mVm.parseDayData(listData,lastTime)
                    }
                }
                4->{
                    if(lastTime==0L){
                        temp=mVm.monthListData.value.orEmpty()+mVm.parseDayData(listData,lastTime)
                    }else{
                        temp=mVm.parseDayData(listData,lastTime)
                    }
                }
                5->{
                    if(lastTime==0L){
                        temp=mVm.yearListData.value.orEmpty()+mVm.parseDayData(listData,lastTime)
                    }else{
                        temp=mVm.parseDayData(listData,lastTime)
                    }
                }
            }
            if(lastTime==0L){
                if(temp!=null&&temp.size>0){
                    if(mType==0){
                        if(temp.size<10){
                            stockChartConfig.setKEntities(temp)
                        }else{
                            stockChartConfig.setKEntities(
                                temp,
                                temp.size-10,
                                temp.size - 1
                            )
                        }
                    }else{
                        stockChartConfig.setKEntities(temp)
                    }
                    mBinding.stockChart.notifyChanged()
                }
            }else{
                if(temp!=null&&temp.size>0){
                    if(temp.size==1){
                        val tempList=stockChartConfig.kEntities
                        var have=false
                        for((index,v) in tempList.withIndex()){
                            if(v.getTime()==temp.get(0).getTime()){
                                stockChartConfig.modifyKEntity(index,temp.get(0))
                                have=true
                            }
                        }
                        if(!have){
                            stockChartConfig.appendRightKEntities(temp)
                        }
                    }else{
                        stockChartConfig.appendRightKEntities(temp)
                    }
                    mBinding.stockChart.notifyChanged()
                }
            }
        })
    }

    /**
     * K线图初始化
     */
    private fun initKChart() {
        kChartFactory = KChartFactory(mBinding.stockChart, kChartConfig)

        kChartConfig.apply {
            kChartType= KChartConfig.KChartType.CANDLE().apply {
                highestAndLowestLabelConfig= KChartConfig.HighestAndLowestLabelConfig(
                    { "${NumberFormatUtil.formatPrice(it)}" },
                    requireContext().getColor(R.color.black),
                    DEFAULT_K_CHART_HIGHEST_AND_LOWEST_LABEL_TEXT_SIZE,
                    DEFAULT_K_CHART_HIGHEST_AND_LOWEST_LABEL_LINE_STROKE_WIDTH,
                    DEFAULT_K_CHART_HIGHEST_AND_LOWEST_LABEL_LINE_LENGTH
                )
            }
            // 指标线宽度
            indexStrokeWidth =keyboardUtil.dp2px(requireContext(), 0.5f).toFloat()

            height=keyboardUtil.dp2px(requireContext(), 250f)
            leftLabelConfig=null

            onHighlightListener = object : OnHighlightListener {
                override fun onHighlight(highlight: Highlight) {
                    val idx = highlight.getIdx()
                    val lay=mBinding.market.layoutParams as RelativeLayout.LayoutParams
                   if(highlight.x<mBinding.stockChart.width/2){
                       lay.removeRule(RelativeLayout.ALIGN_PARENT_LEFT)
                       lay.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                   }else{
                       lay.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                       lay.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                   }
                    val kEntities = stockChartConfig.kEntities
                    if (idx in kEntities.indices) {
                        val kEntity = kEntities[idx]
                        if (kEntity.containFlag(FLAG_EMPTY)) {

                        } else {
                            val label =(kEntity.getTime()/1000).toDateStr("MM/dd HH:mm")
                            mVm.nowTime.value=label
                            mVm.nowPrice.value="${NumberFormatUtil.formatPrice(kEntity.getClosePrice())}"
                            mVm.nowHigh.value="${NumberFormatUtil.formatPrice(kEntity.getHighPrice())}"
                            mVm.nowLow.value="${NumberFormatUtil.formatPrice(kEntity.getLowPrice())}"
                        }

                    }
                }

                override fun onHighlightBegin() {
                    handler.removeMessages(1000)
                    mVm.hideHight.value=false
                }

                override fun onHighlightEnd() {
                    handler.removeMessages(1000)
                    handler.sendEmptyMessageDelayed(1000, 2000)
                }
            }
            // 左侧标签设置
            rightLabelConfig = KChartConfig.LabelConfig(
                5,
                { "${NumberFormatUtil.formatPrice(it)}" },
                keyboardUtil.sp2px(requireContext(), 8.0f).toFloat(),
                requireContext().resources.getColor(R.color.black),
                keyboardUtil.dp2px(requireContext(), 10.0f).toFloat(),
               0f,
                0f,
            )
        }
    }

    /**
     * 时间条图初始化
     */
    private fun initTimeBar() {
        timeBarFactory = CustomChartFactory(mBinding.stockChart, timeBarConfig)

        timeBarConfig.apply {
            // 背景色（时间条这里不像显示网格线，加个背景色覆盖掉）

            type=TimeBarConfig.Type.FiveMinutes(SimpleDateFormat("HH:mm"))
            // 长按标签背景色
            backGroundColor =requireContext().resources.getColor(R.color.white)
        }

    }


    override fun getBaseModel(): BaseModel {
        return mVm
    }
}