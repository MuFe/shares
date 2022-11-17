package com.shares.app.ui


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shares.app.base.BaseModel
import com.shares.app.data.KData
import com.shares.app.extension.toDateStr
import com.shares.app.misc.SingleLiveEvent
import com.shares.app.util.NetworkUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



class LineViewModel(
) : BaseModel() {
    val time = MutableLiveData<String>()
    val isHour = MutableLiveData<Boolean>()
    val hideHight = MutableLiveData<Boolean>()
    val nowPrice = MutableLiveData<String>()
    val nowTime = MutableLiveData<String>()
    val nowLow = MutableLiveData<String>()
    val nowHigh = MutableLiveData<String>()
    private val mEvent = SingleLiveEvent<ViewModelEvent>()
    val event: LiveData<ViewModelEvent> = mEvent
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
        time.value=(System.currentTimeMillis()/1000).toDateStr("yyyy-MM-dd")
        isHour.value=false
        hideHight.value=true
    }

    fun loadNetWork(tnetworkUtil:NetworkUtil){
        networkUtil=tnetworkUtil
        networkUtil?.viewModelScope=viewModelScope
        networkUtil?.viewModel=this
    }


    fun loadData(){
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
        for((index,v) in list.withIndex()){
            if(last==null||v.getTime()>startTime){
                if(last!=null){
                    last.setClosePrice(list.get(index-1).getClosePrice())
                    startTime+=diff
                }else{
                    startTime+=diff
                }
                last=v
                temp.add(last)
            }else if(v.getHighPrice()>last.getHighPrice()){
                last.setHighPrice(v.getHighPrice())
            }else if(v.getLowPrice()<last.getLowPrice()){
                last.setLowPrice(v.getLowPrice())
            }
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

    fun parseDayData(list:List<KData>,lastTime: Long):List<KData>{
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
            temp.add(last)
            for(v in tempList){
                if(v.getHighPrice()>last.getHighPrice()){
                    last.setHighPrice(v.getHighPrice())
                }else if(v.getLowPrice()<last.getLowPrice()){
                    last.setLowPrice(v.getLowPrice())
                }
            }
        }
        return temp
    }

    fun loadDay(){
        loadData { v, result ->
            networkUtil?.getK(1,"0"){
                mDayListData.postValue(it)
                loadWeek()
            }
        }
    }

    fun loadWeek(){
        loadData { v, result ->
            networkUtil?.getK(2,"0"){
                mWeekListData.postValue(it)
                loadMonth()
            }
        }
    }

    fun loadMonth(){
        loadData { v, result ->
            networkUtil?.getK(3,"0"){
                mMonthListData.postValue(it)
                loadYear()
            }
        }
    }

    fun loadYear(){
        loadData { v, result ->
            networkUtil?.getK(4,"0"){
                mYearListData.postValue(it)
                loadData()
            }
        }
    }

    fun delayGet(){
        viewModelScope.launch {
            delay(5000)
            loadData()
            delayGet()
        }
    }



    sealed class ViewModelEvent {
        object FinishLoginEvent : ViewModelEvent()
        object RegisterEvent : ViewModelEvent()
    }
}