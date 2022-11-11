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


class MyViewModel(
    networkUtil: NetworkUtil,
    val mPreferenceUtil: PreferenceUtil
) : BaseModel(networkUtil) {
    val name = MutableLiveData<String>()
    val endTime = MutableLiveData<String>()
    val recommend = MutableLiveData<String>()
    val code = MutableLiveData<String>()
    private val mEvent = SingleLiveEvent<ViewModelEvent>()
    val event: LiveData<ViewModelEvent> = mEvent

    init {
        name.value = mPreferenceUtil.getUserData()
        endTime.value = ""
        recommend.value = ""
        code.value = ""
    }


    fun getUserInfo() {
        loadData() { it, result ->
            networkUtil?.getVip {
                val temp=it.vipEndTime
                if(!temp.isNullOrEmpty()){
                    endTime.value =(temp.toDateStr("yyyy-MM-dd'T'HH:mm:ss")/1000).toDateStr("yyyy-MM-dd")
                }
                code.value = mPreferenceUtil.getUserData()
                recommend.value = it.recommendId.orEmpty()
            }
        }
    }

    fun goPay() {
        mBaseEvent.postValue(BaseViewModelEvent.NavigateEvent(R.id.navigation_pay, bundleOf()))
    }

    fun logout(){
        mBaseEvent.postValue(BaseViewModelEvent.LogoutEvent)
    }


    sealed class ViewModelEvent {
        object FinishLoginEvent : ViewModelEvent()
        object RegisterEvent : ViewModelEvent()
    }
}