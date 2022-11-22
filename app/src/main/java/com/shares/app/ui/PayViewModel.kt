package com.shares.app.ui


import android.app.Activity
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.alipay.sdk.app.PayTask
import com.shares.app.R
import com.shares.app.base.BaseModel
import com.shares.app.data.PayResult
import com.shares.app.misc.SingleLiveEvent
import com.shares.app.util.NetworkUtil
import com.shares.app.util.PreferenceUtil
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PayViewModel(
    networkUtil: NetworkUtil,
    val mPreferenceUtil:PreferenceUtil
) : BaseModel(networkUtil) {
    val price = MutableLiveData<String>()
    val priceRate = MutableLiveData<String>()
    val priceRate1 = MutableLiveData<String>()
    val price1 = MutableLiveData<String>()
    val contact = MutableLiveData<String>()
    val selectIndex = MutableLiveData<Int>()
    val selectType = MutableLiveData<Int>()
    private val mEvent = SingleLiveEvent<ViewModelEvent>()
    val event: LiveData<ViewModelEvent> = mEvent
    init {
        selectIndex.value=0
        selectType.value=0
        contact.value=""
        price1.value=""
        priceRate1.value=""
        priceRate.value=""
        price.value=""
    }
    fun getData(){
        loadData() { it, result ->
            networkUtil?.getVipPrice {
                for(v in it){
                    if(v.vipType==0){
                        price.value=v.price.toInt().toString()
                        priceRate.value=String.format("%.1f",v.price*10f/299f)
                    }else{
                        price1.value=v.price.toInt().toString()
                        priceRate1.value=String.format("%.1f",v.price*10f/599f)
                    }
                }
            }
        }
    }

    fun choose(value:Int){
        selectIndex.value=value
    }

    fun chooseType(value:Int){
        selectType.value=value
    }

    fun startAliPay(orderInfo:String,activity:Activity){
        viewModelScope.launch(Dispatchers.IO){
            val task=PayTask(activity)
            val map=task.payV2(orderInfo,true)
            val payResult = PayResult(map)

            val resultStatus: String = payResult.resultStatus.orEmpty()
            viewModelScope.launch(Dispatchers.Main) {
                if (TextUtils.equals(resultStatus, "9000")) {
                    mEvent.postValue(ViewModelEvent.PayEvent(false))
                } else {
                    mEvent.postValue(ViewModelEvent.PayEvent(true))
                }
            }
        }
    }

    fun payAli(){
        loadData() { it, result ->
            networkUtil?.pay(selectType.value!!,contact.value.orEmpty()) {
                mEvent.postValue(ViewModelEvent.StartAliEvent(it.payBody))
            }
        }
    }

    fun getUserInfo() {
        loadData() { it, result ->
            networkUtil?.getVip {
                mPreferenceUtil.setHide(true)
                mBaseEvent.postValue(BaseViewModelEvent.NavigateUpEvent)
            }
        }
    }

    fun goPay(){
        if(contact.value.isNullOrEmpty()){
            mBaseEvent.postValue(BaseViewModelEvent.ToastIntEvent(R.string.pay_contact_empty))
            return
        }
        if(selectIndex.value==0){
            payAli()
        }else{

        }
    }

    sealed class ViewModelEvent {
        class StartAliEvent(val msg:String) : ViewModelEvent()
        class PayEvent(val fail:Boolean) : ViewModelEvent()
    }
}