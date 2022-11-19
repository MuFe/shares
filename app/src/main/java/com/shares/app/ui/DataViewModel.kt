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
    val changeCheck= MutableLiveData<Boolean>()
    val buyNumber = MutableLiveData<String>()
    val buy = MutableLiveData<String>()
    val level = MutableLiveData<String>()
    val levelInt = MutableLiveData<Int>()
    val buyNumberResult = MutableLiveData<String>()
    val buyResult = MutableLiveData<String>()
    val selectIndex = MutableLiveData<Int>()
    val check1 = MutableLiveData<Boolean>()
    val check2 = MutableLiveData<Boolean>()
    val check3 = MutableLiveData<Boolean>()
    val levelList = MutableLiveData<List<String>>()
    private val mEvent = SingleLiveEvent<ViewModelEvent>()
    val event: LiveData<ViewModelEvent> = mEvent
    init {
        haveVip.value=0
        check1.value=false
        check2.value=false
        check3.value=false
        changeCheck.value=false
        check1.value=mPreferenceUtil.getCheck1()
        check2.value=mPreferenceUtil.getCheck2()
        check3.value=mPreferenceUtil.getCheck3()
        day.value=""
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
        changeCheck.value=true
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

    sealed class ViewModelEvent {
        object ChangeEvent : ViewModelEvent()
        object ShowLevelEvent : ViewModelEvent()
    }
}