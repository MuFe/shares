package com.shares.app.ui


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.stockChart.model.KLineDataModel
import com.shares.app.R
import com.shares.app.base.BaseModel
import com.shares.app.data.LineData
import com.shares.app.extension.toDateStr
import com.shares.app.misc.SingleLiveEvent
import com.shares.app.util.NetworkUtil
import com.shares.app.util.PreferenceUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject


class LineViewModel(
) : BaseModel() {
    val time = MutableLiveData<String>()
    val isHour = MutableLiveData<Boolean>()
    private val mEvent = SingleLiveEvent<ViewModelEvent>()
    val event: LiveData<ViewModelEvent> = mEvent
    private val mListData = MutableLiveData<List<KLineDataModel>>()
    val listData: LiveData<List<KLineDataModel>> = mListData
    private val mYearListData = MutableLiveData<List<KLineDataModel>>()
    val yearListData: LiveData<List<KLineDataModel>> = mYearListData
    private val mMonthListData = MutableLiveData<List<KLineDataModel>>()
    val monthListData: LiveData<List<KLineDataModel>> = mMonthListData
    private val mWeekListData = MutableLiveData<List<KLineDataModel>>()
    val weekListData: LiveData<List<KLineDataModel>> = mWeekListData
    private val mDayListData = MutableLiveData<List<KLineDataModel>>()
    val dayListData: LiveData<List<KLineDataModel>> = mDayListData
    init {
        time.value=(System.currentTimeMillis()/1000).toDateStr("yyyy-MM-dd")
        isHour.value=false
    }

    fun loadNetWork(tnetworkUtil:NetworkUtil){
        networkUtil=tnetworkUtil
        networkUtil?.viewModelScope=viewModelScope
        networkUtil?.viewModel=this
    }


    fun loadData(){
        loadData { v, result ->
            var now=(System.currentTimeMillis()/1000-86400)
            now=(now.toDateStr("yyyy-MM-dd")+" 00:00:00").toDateStr("yyyy-MM-dd")
            networkUtil?.getK(0,now.toString()){
                mBaseEvent.postValue(BaseViewModelEvent.ToastStrEvent("11111="+it.size))
                mListData.postValue(it)
            }
        }
    }

    fun parseData(diff:Long,list:List<KLineDataModel>):List<KLineDataModel>{
        val temp= mutableListOf<KLineDataModel>()
        var startTime=(System.currentTimeMillis()/1000-86400)
        startTime=(startTime.toDateStr("yyyy-MM-dd")+" 09:00:00").toDateStr("yyyy-MM-dd HH:mm:ss")
        var last:KLineDataModel?=null
        for((index,v) in list.withIndex()){
            if(last==null||v.dateMills>startTime+diff){
                if(last!=null){
                    last.close=list.get(index-1).low
                }
                last=v
                temp.add(last)
                startTime+=diff
            }else if(v.high>last.high){
                last.high=v.high
            }else if(v.low<last.low){
                last.low=v.low
            }
        }
        mBaseEvent.postValue(BaseViewModelEvent.ToastStrEvent("22222="+temp.size))
        return temp
    }

    fun parseDayData(list:List<KLineDataModel>):List<KLineDataModel>{
        val temp= mutableListOf<KLineDataModel>()
        if(list.size>0){
            var last:KLineDataModel=list.last()
            last.open=list.first().open
            temp.add(last)
            for(v in list){
                if(v.high>last.high){
                    last.high=v.high
                }else if(v.low<last.low){
                    last.low=v.low
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
//        viewModelScope.launch {
//            delay(5000)
//            loadData()
//            delayGet()
//        }
    }


    sealed class ViewModelEvent {
        object FinishLoginEvent : ViewModelEvent()
        object RegisterEvent : ViewModelEvent()
    }
}