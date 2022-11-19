package com.shares.app


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


class MainAcvitityViewModel(
    val mPreferenceUtil:PreferenceUtil
) : BaseModel() {
    val timeDe = MutableLiveData<String>()
    val timeDeInt = MutableLiveData<Int>()
    val hide = MutableLiveData<Boolean>()
    init{
        timeDe.value="15:00"
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
}