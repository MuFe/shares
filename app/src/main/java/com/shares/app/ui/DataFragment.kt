package com.shares.app.ui

import android.Manifest
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.CalendarContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
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
import com.jaeger.library.StatusBarUtil
import com.shares.app.R
import com.shares.app.base.BaseModel
import com.shares.app.data.KData
import com.shares.app.databinding.FragmentDataBinding
import com.shares.app.extension.FormatPrice
import com.shares.app.extension.checkPermissions
import com.shares.app.extension.toDateStr
import com.shares.app.util.CalendarReminderUtils
import com.shares.app.util.KeyboardUtil
import com.shares.app.view.LevelPopupWindow
import com.shares.app.view.custom.CustomChartConfig
import com.shares.app.view.custom.CustomChartFactory
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class DataFragment : BaseFragment() {
    private lateinit var mBinding: FragmentDataBinding
    private val mVm: DataViewModel by viewModel()
    private val keyboardUtil: KeyboardUtil by inject()
    private var popupWindow: LevelPopupWindow? = null
    private var kChartType: KChartConfig.KChartType = KChartConfig.KChartType.CANDLE()
    private var kChartIndex: Index? = Index.MA()
    // K?????????????????????
    private var kChartFactory: KChartFactory? = null
    private val kChartConfig = KChartConfig(kChartType = kChartType, index = kChartIndex)
    // ???????????????????????????
    private var timeBarFactory: CustomChartFactory? = null
    private val timeBarConfig = CustomChartConfig()
    // ?????????
    private val stockChartConfig = StockChartConfig()
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            mBinding.market.visibility=View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private val mGrantStoragePermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val title=mPreferenceUtil.getSaveTitle()
            val saveTime=mPreferenceUtil.getSaveTime()
            if(saveTime!=0L){
                parseStoragePermission(false,title,saveTime,true)
            }else{
                parseStoragePermission(false,title,saveTime,false)
            }

        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentDataBinding.inflate(inflater)
        mBinding.lifecycleOwner = this
        mBinding.vm = mVm
        StatusBarUtil.setTranslucentForImageViewInFragment(requireActivity(),0,mBinding.top)
        val lay=mBinding.market.layoutParams as RelativeLayout.LayoutParams
        lay.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        lay.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        initKChart()
        initTimeBar()
        stockChartConfig.apply {
            // ??????????????????????????????????????????StockChart??????
            addChildCharts(
                kChartFactory!!,
                timeBarFactory!!,
            )
            scaleAble=true
            scrollAble=true
            overScrollAble=false
            // ??????????????????
            scaleFactorMax = 2f

            scrollSmoothly = false
            downColor=requireContext().resources.getColor(R.color.f57bd6a)
            // ??????????????????
            scaleFactorMin = 0.5f
//            gridLineStrokeWidth = 0.5f
            gridLineColor=requireContext().resources.getColor(R.color.line)
            // ???????????????
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


        // ????????????
        mBinding.stockChart.setConfig(stockChartConfig)
        mVm.loadDay()
        mVm.startGetData()
        mVm.startGetDataFull()
        mVm.getUserInfo()
        mVm.delayGet()

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mVm.event.observe(viewLifecycleOwner, { event ->
            when (event) {
                DataViewModel.ViewModelEvent.ChangeEvent->{
                    if(mVm.isHide.value==true){
                        mVm.isHide.value=false
                        setPrice(mVm.check1.value!!)
                    }
                    (requireActivity() as MainHost).change(mVm.now.value.orEmpty(),mVm.change.value.orEmpty(),mVm.isPlus.value!!)
                }
                DataViewModel.ViewModelEvent.ShowLevelEvent->{
                    createPop(mBinding.levelLay,mVm.levelInt.value!!)
                }
                DataViewModel.ViewModelEvent.FinishEvent->{
                  
                }
                DataViewModel.ViewModelEvent.HideEvent->{
                    setPrice(false)
                }
            }
        })

        mVm.check1.observe(viewLifecycleOwner,{event->
            setPrice(event)
        })
        mVm.check2.observe(viewLifecycleOwner,{event->
            setAl(1000,requireContext().resources.getString(R.string.data_notify1),8,55,event)
        })
        mVm.check3.observe(viewLifecycleOwner,{event->
            setAl(10001,requireContext().resources.getString(R.string.data_notify2),17,25,event)
        })

        val temp=(requireActivity() as MainHost).getTime()
        temp.observe(viewLifecycleOwner,{event->
            mVm.timeDe.value=event
            if(event.equals("0:00")){
                mVm.hide.value=mPreferenceUtil.isHide()
            }
        })

        mVm.listData.observe(viewLifecycleOwner, { listData ->
            var lastTime=0L
            if(stockChartConfig.getKEntitiesSize()!=0){
                val last=stockChartConfig.kEntities.last()
                lastTime=last.getTime()
            }
            initData(listData,lastTime)
        })
        mVm.selectIndex.observe(viewLifecycleOwner, { listData ->
            handler.removeMessages(1000)
            mBinding.market.visibility=View.GONE
            initData(mVm.listData.value.orEmpty(),0L)
        })
    }

    fun initData(listData:List<KData>,lastTime:Long){
        var temp:List<IKEntity>?= mutableListOf()
        when(mVm.selectIndex.value){
            0->{
                temp=mVm.parseData(15*60*1000,listData,lastTime)
            }
            1->{
                temp=mVm.parseData(60*60*1000,listData,lastTime)
            }
            2->{
                if(lastTime==0L){
                    temp= mVm.mergeFormat(mVm.dayListData.value.orEmpty(),mVm.parseDayData(listData,lastTime,"yyyy-MM-dd"),"yyyy-MM-dd")
                }else{
                    temp= mVm.parseDayData(listData,lastTime,"yyyy-MM-dd")
                }
            }
            3->{
                val result= mutableListOf<KData>()
                for (i in 0 until 20) {
                    result.add(KData.obtainEmptyKData()) // ?????????????????????????????????EmptyKEntity()??????
                }
                if(lastTime==0L){
                    temp= mVm.mergeWeek(mVm.weekListData.value.orEmpty(),mVm.parseDayData(listData,lastTime,"yyyy-MM-dd"))+result
                }else{
                    temp= mVm.parseDayData(listData,lastTime,"yyyy-MM-dd")
                }
            }
            4->{
                val result= mutableListOf<KData>()
                for (i in 0 until 12) {
                    result.add(KData.obtainEmptyKData()) // ?????????????????????????????????EmptyKEntity()??????
                }
                if(lastTime==0L){
                    temp= mVm.mergeFormat(mVm.monthListData.value.orEmpty(),mVm.parseDayData(listData,lastTime,"yyyy-MM"),"yyyy-MM")+result
                }else{
                    Log.e("TG",lastTime.toString()+"")
                    temp= mVm.parseDayData(listData,lastTime,"yyyy-MM")
                }
            }
            5->{
                val result= mutableListOf<KData>()
                for (i in 0 until (10)) {
                    result.add(KData.obtainEmptyKData()) // ?????????????????????????????????EmptyKEntity()??????
                }
                if(lastTime==0L){
                    temp= mVm.mergeFormat(mVm.yearListData.value.orEmpty(),mVm.parseDayData(listData,lastTime,"yyyy"),"yyyy")+result
                }else{
                    temp= mVm.parseDayData(listData,lastTime,"yyyy")
                }
            }
        }
        if(lastTime==0L){
            if(temp!=null&&temp.size>0){
                if(mVm.selectIndex.value==0){
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
                val tempList=stockChartConfig.kEntities
                val insert= mutableListOf<IKEntity>()

                for(vv in temp){
                    var have=false
                    for((index,v) in tempList.withIndex()){
                        if(v.getTime()==vv.getTime()){
                            stockChartConfig.modifyKEntity(index,vv)
                            have=true
                        }
                    }
                    if(!have){
                        insert.add(vv)
                    }
                }
                if(insert.size>0){
                    stockChartConfig.appendRightKEntities(insert)
                }
                mBinding.stockChart.notifyChanged()
            }
        }
    }
    fun setPrice(isShow: Boolean){
        val it=Intent(requireActivity(),DataService::class.java)
        it.action="data_service"
       if(isShow){
           val notification = NotificationManagerCompat.from(requireContext())
           val isEnabled = notification.areNotificationsEnabled()
           if (!isEnabled) {
               open()
           }
           requireActivity().startForegroundService(it)
       }else{
           requireActivity().stopService(it)
       }
    }

    fun setAl(id:Int,title:String,hour:Int,min:Int,isShow:Boolean){
        val jb = JobInfo.Builder(
            id, ComponentName(
                requireContext().packageName,
                MyJobService::class.java.getName()
            )
        )
        val c: Calendar = Calendar.getInstance()
        c.setTimeInMillis(System.currentTimeMillis())
        c.set(Calendar.MINUTE, min)
        c.set(Calendar.HOUR_OF_DAY, hour)
        c.set(Calendar.SECOND, 0)

        var t=c.getTimeInMillis()-System.currentTimeMillis()
        if(t<0){
           t=t+86400*1000
        }
        parseStoragePermission(true,title,System.currentTimeMillis()+t+2*60*1000,isShow)
        jb.setMinimumLatency(t)
        jb.setOverrideDeadline(t+20*1000)
        jb.setTransientExtras(bundleOf("title" to title,"id" to id))
        val jobInfo = jb.build()

        val jobScheduler =requireContext().getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        jobScheduler.cancel(id)
        if(isShow){
            val notification = NotificationManagerCompat.from(requireContext())
            val isEnabled = notification.areNotificationsEnabled()
            if (!isEnabled) {
               open()
            }
            jobScheduler.schedule(jobInfo)
        }
    }

    fun open(){
        val intent = Intent()
        intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
        intent.putExtra("app_package", requireContext().packageName)
        intent.putExtra("app_uid", requireContext().applicationInfo.uid)
        intent.putExtra("android.provider.extra.APP_PACKAGE", requireContext().packageName)
        requireContext().startActivity(intent)
    }

    fun createPop(view: View,index:Int) {
        if (popupWindow == null) {
            popupWindow = LevelPopupWindow { nowData ->
                mVm.levelInt.value=nowData
                mVm.level.value=mVm.levelList.value!!.get(nowData)
                popupWindow?.dismiss()
            }
            popupWindow?.setContent(requireContext(), this)
        }
        popupWindow?.showWithList(view, mVm.levelList.value!!,index)
    }

    override fun onResume() {
        super.onResume()
        mVm.hide.value=mPreferenceUtil.isHide()
    }

    /**
     * K???????????????
     */
    private fun initKChart() {
        kChartFactory = KChartFactory(mBinding.stockChart, kChartConfig)

        kChartConfig.apply {
            kChartType= KChartConfig.KChartType.CANDLE().apply {
                highestAndLowestLabelConfig= KChartConfig.HighestAndLowestLabelConfig(
                    {
                        "${NumberFormatUtil.formatPrice(it)}"
                    },
                    requireContext().getColor(R.color.black),
                    DEFAULT_K_CHART_HIGHEST_AND_LOWEST_LABEL_TEXT_SIZE,
                    DEFAULT_K_CHART_HIGHEST_AND_LOWEST_LABEL_LINE_STROKE_WIDTH,
                    DEFAULT_K_CHART_HIGHEST_AND_LOWEST_LABEL_LINE_LENGTH
                )
            }
            // ???????????????
            indexStrokeWidth =keyboardUtil.dp2px(requireContext(), 0.5f).toFloat()

            height=keyboardUtil.dp2px(requireContext(), 250f)
            leftLabelConfig=null
            indexColors= mutableListOf(
                requireContext().resources.getColor(R.color.f6998d9),
                requireContext().resources.getColor(R.color.fca9f92),
                requireContext().resources.getColor(R.color.f7d5a9f))
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
                            var format="MM/dd HH:mm"
                            when(mVm.selectIndex.value){
                                2->{
                                    format="MM/dd"
                                }
                                3->{
                                    format="MM/dd"
                                }
                                4->{
                                    format="yyyy/MM"
                                }
                                5->{
                                    format="yyyy"
                                }
                            }
                            val label =(kEntity.getTime()/1000).toDateStr(format)
                            mVm.nowTime.value=label
                            mVm.nowPrice.value="${NumberFormatUtil.formatPrice(kEntity.getClosePrice())}"
                            mVm.nowHigh.value="${NumberFormatUtil.formatPrice(kEntity.getHighPrice())}"
                            mVm.nowLow.value="${NumberFormatUtil.formatPrice(kEntity.getLowPrice())}"
                        }

                    }
                }

                override fun onHighlightBegin() {
                    handler.removeMessages(1000)
                    mBinding.market.visibility=View.VISIBLE
                }

                override fun onHighlightEnd() {
                    handler.removeMessages(1000)
                    handler.sendEmptyMessageDelayed(1000, 2000)
                }
            }
            // ??????????????????
            rightLabelConfig = KChartConfig.LabelConfig(
                5,
                {

                    "${ it.FormatPrice()}"
//                    "${NumberFormatUtil.formatPrice(it)}"
                },
                keyboardUtil.sp2px(requireContext(), 8.0f).toFloat(),
                requireContext().resources.getColor(R.color.black),
                keyboardUtil.dp2px(requireContext(), 10.0f).toFloat(),
                0f,
                0f,
            )
        }
    }

    /**
     * ?????????????????????
     */
    private fun initTimeBar() {
        timeBarFactory = CustomChartFactory(mBinding.stockChart, timeBarConfig)

        timeBarConfig.apply {
            // ??????????????????????????????????????????????????????????????????????????????
            if(mVm.selectIndex.value!=0&&mVm.selectIndex.value!=1){
                labelTextColor=  requireContext().resources.getColor(R.color.white)
                type= TimeBarConfig.Type.Day()
            }else{
                type= TimeBarConfig.Type.FiveMinutes(SimpleDateFormat("MM/dd HH:mm"))
            }

            // ?????????????????????
            backGroundColor =requireContext().resources.getColor(R.color.white)
        }

    }
    @RequiresApi(23)
    private fun isPermissionsGranted(): Boolean {
        return requireContext().checkPermissions(Manifest.permission.READ_CALENDAR) && requireContext().checkPermissions(
            Manifest.permission.WRITE_CALENDAR
        )
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun parseStoragePermission(first: Boolean,title: String,time:Long,isShow:Boolean) {
        if (!isPermissionsGranted()) {
            if (first) {
                if(isShow){
                    mPreferenceUtil.setSave(title,time)
                    mGrantStoragePermissionResult.launch(
                        arrayOf(
                            Manifest.permission.WRITE_CALENDAR,
                            Manifest.permission.READ_CALENDAR
                        )
                    )
                }else{
                    mPreferenceUtil.setSave(title,0)
                }
                return
            } else {
                if (!isPermissionsGranted()) {
                    return
                }
            }
        }
        CalendarReminderUtils.deleteCalendarEvent(requireContext(),title)
        if(isShow){
            CalendarReminderUtils.addCalendarEvent(requireContext(),title,title,time)
        }
        mPreferenceUtil.clearSave()
    }




    override fun getBaseModel(): BaseModel {
        return mVm
    }
}