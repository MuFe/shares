package com.shares.app.ui


import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shares.app.R
import com.shares.app.base.BaseModel
import com.shares.app.data.KData
import com.shares.app.extension.toDateStr
import com.shares.app.misc.SingleLiveEvent
import com.shares.app.util.NetworkUtil
import com.shares.app.util.PreferenceUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class DataViewModel(
    networkUtil: NetworkUtil,
    val mPreferenceUtil:PreferenceUtil
) : BaseModel(networkUtil) {
    val haveVip = MutableLiveData<Int>()
    val hide = MutableLiveData<Boolean>()
    val selectIndex = MutableLiveData<Int>()
    val day = MutableLiveData<String>()
    val numberTime = MutableLiveData<String>()
    val time = MutableLiveData<String>()
    val today = MutableLiveData<String>()
    val yesterday = MutableLiveData<String>()
    val min = MutableLiveData<String>()
    val max = MutableLiveData<String>()
    val timeDe = MutableLiveData<String>()
    val now = MutableLiveData<String>()
    val change = MutableLiveData<String>()
    val isPlus = MutableLiveData<Boolean>()
    val buyNumber = MutableLiveData<String>()
    val buy = MutableLiveData<String>()
    val level = MutableLiveData<String>()
    val levelInt = MutableLiveData<Int>()
    val buyNumberResult = MutableLiveData<String>()
    val buyResult = MutableLiveData<String>()
    val check1 = MutableLiveData<Boolean>()
    val check2 = MutableLiveData<Boolean>()
    val check3 = MutableLiveData<Boolean>()
    val levelList = MutableLiveData<List<String>>()
    private val mEvent = SingleLiveEvent<ViewModelEvent>()
    val event: LiveData<ViewModelEvent> = mEvent

    val nowPrice = MutableLiveData<String>()
    val nowTime = MutableLiveData<String>()
    val nowLow = MutableLiveData<String>()
    val nowHigh = MutableLiveData<String>()
    private val mListData = MutableLiveData<List<KData>>()
    val listData: LiveData<List<KData>> = mListData
    private val mYearListData = MutableLiveData<List<KData>>()
    val yearListData: LiveData<List<KData>> = mYearListData
    private val mMonthListData = MutableLiveData<List<KData>>()
    val monthListData: LiveData<List<KData>> = mMonthListData
    private val mWeekListData = MutableLiveData<List<KData>>()
    val weekListData: LiveData<List<KData>> = mWeekListData
    private val mDayListData = MutableLiveData<List<KData>>()
    val dayListData: LiveData<List<KData>> = mDayListData
    init {
        haveVip.value=0
        selectIndex.value=2
        hide.value=mPreferenceUtil.isHide()
        check1.value=false
        check2.value=false
        check3.value=false
        check1.value=mPreferenceUtil.getCheck1()
        check2.value=mPreferenceUtil.getCheck2()
        check3.value=mPreferenceUtil.getCheck3()
        day.value=""
        timeDe.value="15:00"
        numberTime.value=""
        yesterday.value=""
        today.value=""
        time.value=""
        now.value=""
        buyNumber.value=""
        buy.value=""
        levelInt.value=0
        isPlus.value=true
        val temp="LV1"
        val temp1="LV2"
        val temp2="LV3"
        val temp3="LV4"
        val temp4="LV5"
        val tempList= mutableListOf<String>()
        tempList.add(temp)
        tempList.add(temp1)
        tempList.add(temp2)
        tempList.add(temp3)
        tempList.add(temp4)
        levelList.value=tempList
        level.value=tempList.get(levelInt.value!!)
    }
    fun getUserInfo() {
        loadData() { it, result ->
            networkUtil?.getVip {
                if(!it.vipEndTime.isNullOrBlank()){
                    haveVip.value=1
                }else{
                    haveVip.value=2
                }
                mEvent.postValue(ViewModelEvent.FinishEvent)
            }
        }
    }

    fun startGetData(){
        viewModelScope.launch {
            getData()
            delay(12000)
            startGetData()
        }
    }
    fun getData(){
        loadData() { it, result ->
            networkUtil?.getPrice {
                day.value=(it.current.createTime.toDateStr("yyyy-MM-dd'T'HH:mm:ss")/1000).toDateStr("yyyy-MM-dd")
                time.value=(it.current.createTime.toDateStr("yyyy-MM-dd'T'HH:mm:ss")/1000).toDateStr("HH:mm")
                now.value=it.current.price.toString()
                today.value=it.current.maxPrice.toString()
                yesterday.value=it.yesterday.price.toString()
                max.value=it.current.maxPrice.toString()
                min.value=it.current.minPrice.toString()
                var rate=0.0f
                if(it.yesterday.price!=0f){
                    rate=100*(it.current.price-it.yesterday.price)/it.yesterday.price
                }
                if(rate>=0){
                    isPlus.value=true
                    change.value="+"+String.format("%.2f",rate)+"%"
                }else{
                    isPlus.value=false
                    change.value=String.format("%.2f",rate)+"%"
                }
                mEvent.postValue(ViewModelEvent.ChangeEvent)
            }
        }
    }

    fun startGetDataFull(){
        viewModelScope.launch {
            getDataFull()
            delay(2*60*1000)
            startGetDataFull()
        }
    }
    fun getDataFull(){
        loadData { v, result ->
            networkUtil?.getDataFull(){

            }
        }
    }

    fun goPay(){
        mBaseEvent.postValue(BaseViewModelEvent.NavigateEvent(R.id.navigation_pay, bundleOf()))
    }

    fun goCal(){
        if(buyNumber.value.isNullOrEmpty()||buy.value.isNullOrEmpty()){
            return
        }
        val temp=buyNumber.value!!.toInt()
        val temp2=buy.value!!.toInt()
        var temp1=0.5
        when(levelInt.value){
            0->{
                temp1=0.5
            }
            1->{
                temp1=0.15
            }
            2->{
                temp1=0.05
            }
            3->{
                temp1=0.03
            }
            4->{
                temp1=0.02
            }
        }
        buyNumberResult.value=(temp/(1+temp1)).toInt().toString()
        buyResult.value=((1+temp1)*temp2/temp).toFloat().toString()
    }


    fun chooseCheck(value:Int){
        when(value){
            1->{
                check1.value=!check1.value!!
                mPreferenceUtil.setCheck1(check1.value!!)
            }
            2->{
                check2.value=!check2.value!!
                mPreferenceUtil.setCheck2(check2.value!!)
            }
            3->{
                check3.value=!check3.value!!
                mPreferenceUtil.setCheck3(check3.value!!)
            }
        }
    }

    fun clickLevel(){
        mEvent.postValue(ViewModelEvent.ShowLevelEvent)
    }

    fun clickIndex(value:Int){
        selectIndex.value=value
    }

    fun loadK(){
        loadData { v, result ->
            var now=(System.currentTimeMillis()/1000)
            now=(now.toDateStr("yyyy-MM-dd")+" 00:00:00").toDateStr("yyyy-MM-dd")
            networkUtil?.getK(0,now.toString()){
                mListData.postValue(it)
            }
        }
    }

    fun parseData(diff:Long,list:List<KData>,lastTime:Long):List<KData>{
        val temp= mutableListOf<KData>()
        var startTime=(System.currentTimeMillis()/1000)
        startTime=(startTime.toDateStr("yyyy-MM-dd")+" 09:00:00").toDateStr("yyyy-MM-dd HH:mm:ss")
        var last:KData?=null
        for(v in list){
            if(last==null||v.getTime()>startTime){
                startTime+=diff
                last=v
                temp.add(last)
            }else{
                if(v.getHighPrice()>last.getHighPrice()){
                    last.setHighPrice(v.getHighPrice())
                }
                if(v.getLowPrice()<last.getLowPrice()){
                    last.setLowPrice(v.getLowPrice())
                }
            }
            last.setClosePrice(v.getClosePrice())
        }
        startTime=(System.currentTimeMillis()/1000)
        startTime=(startTime.toDateStr("yyyy-MM-dd")+" 09:00:00").toDateStr("yyyy-MM-dd HH:mm:ss")
        val re= mutableListOf<KData>()
        for(v in temp){
            if(v.getTime()<=lastTime){
                startTime+=diff
                continue
            }
            if(v.getTime()>startTime&&v.getTime()<startTime+diff){
                v.setTime(startTime)
            }
            re.add(v)
            startTime+=diff
        }
        return re
    }

    fun parseDayData(list:List<KData>,lastTime: Long,format: String):List<KData>{
        val temp= mutableListOf<KData>()
        val tempList= mutableListOf<KData>()
        for(v in list){
            if(v.getTime()<=lastTime){
                continue
            }
            tempList.add(v)
        }
        if(tempList.size>0){
            val last:KData=tempList.last()
            last.setOpenPrice(tempList.first().getOpenPrice())
            last.setTime((tempList.first().getTime()/1000).toDateStr(format).toDateStr(format))
            temp.add(last)
            for(v in tempList){
                if(v.getHighPrice()>last.getHighPrice()){
                    last.setHighPrice(v.getHighPrice())
                }
                if(v.getLowPrice()<last.getLowPrice()){
                    last.setLowPrice(v.getLowPrice())
                }
            }
        }
        return temp
    }

    fun mergeWeek(list:List<KData>,nowTemp:List<KData>):List<KData>{
        val re= mutableListOf<KData>()
        if(nowTemp.size>0){
            val v=nowTemp.first()
            if(list.size>0){
                val last=list.last()
                val todayCal: Calendar = Calendar.getInstance()
                val dateCal: Calendar = Calendar.getInstance()

                todayCal.setTimeInMillis(last.getTime())
                dateCal.setTimeInMillis(v.getTime())
                if(todayCal.get(Calendar.WEEK_OF_YEAR) == dateCal.get(Calendar.WEEK_OF_YEAR)){
                    last.setTime(v.getTime())
                    if(v.getHighPrice()>last.getHighPrice()){
                        last.setHighPrice(v.getHighPrice())
                    }
                    if(v.getLowPrice()<last.getLowPrice()){
                        last.setLowPrice(v.getLowPrice())
                    }
                    last.setClosePrice(v.getClosePrice())
                    re.addAll(list)
                }else{
                    re.addAll(list)
                    re.addAll(nowTemp)
                }
            }else{
                re.addAll(nowTemp)
            }
        }else{
            re.addAll(list)
        }
        return re
    }


    fun mergeFormat(list:List<KData>,nowTemp:List<KData>,format:String):List<KData>{
        val re= mutableListOf<KData>()
        if(nowTemp.size>0){
            val v=nowTemp.first()
            if(list.size>0){
                val last=list.last()
                if((last.getTime()/1000).toDateStr(format).equals((v.getTime()/1000).toDateStr(format))){
                    last.setTime((v.getTime()/1000).toDateStr(format).toDateStr(format))
                    Log.e("TAG",last.getTime().toString()+"+"+v.getTime().toString())
                    if(v.getHighPrice()>last.getHighPrice()){
                        last.setHighPrice(v.getHighPrice())
                    }
                    if(v.getLowPrice()<last.getLowPrice()){
                        last.setLowPrice(v.getLowPrice())
                    }
                    last.setClosePrice(v.getClosePrice())
                    re.addAll(list)
                }else{
                    re.addAll(list)
                    re.addAll(nowTemp)
                }
            }else{
                re.addAll(nowTemp)
            }
        }else{
            re.addAll(list)
        }
        return re
    }





    fun loadDay(){
        loadData { v, result ->
            networkUtil?.getK(1,"1"){
                mDayListData.postValue(it)
                loadWeek()
            }
        }
    }

    fun loadWeek(){
        loadData { v, result ->
            networkUtil?.getK(2,"1"){
                mWeekListData.postValue(it)
                loadMonth()
            }
        }
    }

    fun loadMonth(){
        loadData { v, result ->
            networkUtil?.getK(3,"1"){
                mMonthListData.postValue(it)
                loadYear()
            }
        }
    }

    fun loadYear(){
        loadData { v, result ->
            networkUtil?.getK(4,"1"){
                mYearListData.postValue(it)
                loadK()
            }
        }
    }

    fun delayGet(){
        viewModelScope.launch {
            delay(30000)
            loadK()
            delayGet()
        }
    }



    sealed class ViewModelEvent {
        object ChangeEvent : ViewModelEvent()
        object ShowLevelEvent : ViewModelEvent()
        object FinishEvent : ViewModelEvent()
    }
}