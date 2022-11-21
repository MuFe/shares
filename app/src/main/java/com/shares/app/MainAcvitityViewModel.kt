package com.shares.app


import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shares.app.R
import com.shares.app.base.BaseModel
import com.shares.app.extension.toDateStr
import com.shares.app.misc.SingleLiveEvent
import com.shares.app.ui.DataViewModel
import com.shares.app.util.NetworkUtil
import com.shares.app.util.PreferenceUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainAcvitityViewModel(
    networkUtil: NetworkUtil,
    val mPreferenceUtil:PreferenceUtil
) : BaseModel(networkUtil) {
    val timeDe = MutableLiveData<String>()
    val timeDeInt = MutableLiveData<Int>()
    val hide = MutableLiveData<Boolean>()
    init{
        timeDe.value=""
        timeDeInt.value=900
        hide.value=true
    }

    fun changeTime(){
        viewModelScope.launch {
            delay(1000)
            var t=timeDeInt.value!!
            if(t>0){
                t=t-1
                timeDeInt.value=t
                val s=(t%60)
                var ss=""
                if(s<10){
                    ss="0"+s
                }else{
                    ss=s.toString()
                }
                if(t==0){
                    hide.value=false
                    mPreferenceUtil.setHide(false)
                }
                timeDe.value=(t/60).toString()+":"+ss
                changeTime()
            }else{
                hide.value=false
                mPreferenceUtil.setHide(false)
            }
        }
    }

    fun getUserInfo() {
        loadData() { it, result ->
            networkUtil?.getVip {
                if(!it.vipEndTime.isNullOrBlank()){
                    hide.value=true
                    mPreferenceUtil.setHide(true)
                    timeDe.value="0:00"
                }else{
                    if(it.experienceTime>900) {
                        hide.value = false
                        mPreferenceUtil.setHide(false)
                        timeDe.value="0:00"
                    } else if(it.experienceTime==0){
                        hide.value = false
                        mPreferenceUtil.setHide(false)
                        timeDe.value="0:00"
                    }else{
                        timeDeInt.value=900-it.experienceTime
                        changeTime()
                    }
                }

            }
        }
    }
}