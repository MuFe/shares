package com.shares.app.ui


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shares.app.R
import com.shares.app.base.BaseModel
import com.shares.app.data.LineData
import com.shares.app.extension.toDateStr
import com.shares.app.misc.SingleLiveEvent
import com.shares.app.util.NetworkUtil
import com.shares.app.util.PreferenceUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class HomeViewModel(
    networkUtil: NetworkUtil,
    val mPreferenceUtil:PreferenceUtil
) : BaseModel(networkUtil) {
    val time = MutableLiveData<String>()
    val value1 = MutableLiveData<String>()
    val value2 = MutableLiveData<String>()
    val value3 = MutableLiveData<String>()
    private val mEvent = SingleLiveEvent<ViewModelEvent>()
    val event: LiveData<ViewModelEvent> = mEvent
    private val mListData = MutableLiveData<List<LineData>>()
    val listData: LiveData<List<LineData>> = mListData
    init {
        time.value=(System.currentTimeMillis()/1000).toDateStr("yyyy-MM-dd")
        value2.value=""
        value3.value=""
    }

    fun loadData(){
        loadData { v, result ->
            networkUtil?.getList(){
                mListData.postValue(it)
                var timeInt=0L
                for(v in it){
                    val temp=v.createTime.toDateStr("yyyy-MM-dd'T'HH:mm:ss")
                    if(temp>timeInt){
                        timeInt=temp
                        time.value=(timeInt/1000).toDateStr("yyyy-MM-dd")
                        value1.value=v.wOption.toString()
                        value2.value=v.cOption.toString()
                        value3.value=v.sOption.toString()
                    }
                }
            }
        }
    }


    sealed class ViewModelEvent {
        object FinishLoginEvent : ViewModelEvent()
        object RegisterEvent : ViewModelEvent()
    }
}