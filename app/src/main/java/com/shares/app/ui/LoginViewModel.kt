package com.shares.app.ui


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shares.app.R
import com.shares.app.base.BaseModel
import com.shares.app.misc.SingleLiveEvent
import com.shares.app.util.NetworkUtil
import com.shares.app.util.PreferenceUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class LoginViewModel(
    networkUtil: NetworkUtil,
    val mPreferenceUtil:PreferenceUtil
) : BaseModel(networkUtil) {
    val pass = MutableLiveData<String>()
    val invite = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val type = MutableLiveData<Int>()
    private val mEvent = SingleLiveEvent<ViewModelEvent>()
    val event: LiveData<ViewModelEvent> = mEvent
    init {
        type.value=1
        invite.value=""
    }
    fun login() {
        if(phone.value.isNullOrEmpty()){
            mBaseEvent.postValue(BaseViewModelEvent.ToastIntEvent(R.string.login_empty_phone))
        }else if(pass.value.isNullOrEmpty()){
            mBaseEvent.postValue(BaseViewModelEvent.ToastIntEvent(R.string.login_empty_pass))
        } else {
            networkUtil?.getPass(pass.value!!){
                val str=it.replace("{SM2}","")
                startLogin(str)
            }

        }
    }

    fun startLogin(passValue:String){
        loadData(){ it,result->
            networkUtil?.login(phone.value.orEmpty(),passValue,{
                mPreferenceUtil.setToken(it.access_token,it.token_type)
                mPreferenceUtil.setUserName(phone.value.orEmpty())
                mEvent.postValue(ViewModelEvent.FinishLoginEvent)
            })
        }
    }



    fun goRegister(){
        mEvent.postValue(ViewModelEvent.RegisterEvent)
    }






    fun register() {
        loadData { v, result ->
            networkUtil?.getPass(pass.value!!){
                startRegister(it)
            }
        }
    }

    fun startRegister(pass:String){
        loadData { v, result ->
            networkUtil?.register(phone.value!!,pass,invite.value!!){
                mBaseEvent.postValue(BaseViewModelEvent.ToastIntEvent(R.string.register_toast))
            }
        }
    }

    sealed class ViewModelEvent {
        object FinishLoginEvent : ViewModelEvent()
        object RegisterEvent : ViewModelEvent()
    }
}