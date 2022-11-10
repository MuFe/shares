package com.shares.app.ui


import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shares.app.R
import com.shares.app.base.BaseModel
import com.shares.app.extension.toDateStr
import com.shares.app.misc.SingleLiveEvent
import com.shares.app.util.NetworkUtil
import com.shares.app.util.PreferenceUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class DataViewModel(
    networkUtil: NetworkUtil,
    val mPreferenceUtil:PreferenceUtil
) : BaseModel(networkUtil) {
    val haveVip = MutableLiveData<Int>()
    val day = MutableLiveData<String>()
    val numberTime = MutableLiveData<String>()
    val time = MutableLiveData<String>()
    val today = MutableLiveData<String>()
    val yesterday = MutableLiveData<String>()
    val min = MutableLiveData<String>()
    val max = MutableLiveData<String>()
    val now = MutableLiveData<String>()
    val change = MutableLiveData<String>()
    val isPlus = MutableLiveData<Boolean>()
    val bOne = MutableLiveData<String>()
    val bTwo = MutableLiveData<String>()
    val bThree = MutableLiveData<String>()
    val bFour = MutableLiveData<String>()
    val bFive = MutableLiveData<String>()
    val sOne = MutableLiveData<String>()
    val sTwo = MutableLiveData<String>()
    val sThree = MutableLiveData<String>()
    val sFour = MutableLiveData<String>()
    val sFive = MutableLiveData<String>()
    val format = MutableLiveData<String>()
    val selectIndex = MutableLiveData<Int>()
    private val mEvent = SingleLiveEvent<ViewModelEvent>()
    val event: LiveData<ViewModelEvent> = mEvent
    init {
        haveVip.value=0
        val temp=mPreferenceUtil.getFlow()
        if(temp){
            selectIndex.value=0
        }else{
            selectIndex.value=1
        }
        day.value=""
        numberTime.value=""
        yesterday.value=""
        today.value=""
        time.value=""
        now.value=""
        isPlus.value=true
    }
    fun getUserInfo() {
        loadData() { it, result ->
            networkUtil?.getVip {
                if(!it.vipEndTime.isNullOrBlank()){
                    haveVip.value=1
                }else{
                    haveVip.value=2
                }
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
                day.value=(it.current.createTime.toDateStr("yyyy-MM-dd'T'HH:mm:ss.SSS")/1000).toDateStr("yyyy-MM-dd")
                time.value=(it.current.createTime.toDateStr("yyyy-MM-dd'T'HH:mm:ss.SSS")/1000).toDateStr("HH:mm")
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

    fun choose(value:Int){
        if(value==0){
            mPreferenceUtil.setFlow(true)
        }else{
            mPreferenceUtil.setFlow(false)
        }
        selectIndex.value=value
    }

    sealed class ViewModelEvent {
        object ChangeEvent : ViewModelEvent()
    }
}