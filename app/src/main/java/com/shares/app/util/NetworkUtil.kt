package com.shares.app.util


import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import com.shares.app.base.BaseModel
import com.shares.app.data.*
import com.shares.app.network.ApiService
import com.shares.app.network.Resource

import kotlinx.coroutines.launch
import okhttp3.*
import retrofit2.HttpException


class NetworkUtil(val service: ApiService, val preferenceUtil: PreferenceUtil) {
    var viewModelScope: CoroutineScope? = null
    var viewModel: BaseModel? = null
    fun getPass(pass:String,listener: (String) -> Unit) {
        viewModelScope?.launch {
            try{
                val data = service.getPassword("authserver/getPassword/"+pass)
                listener(data.first)
            }catch (e:Exception){
                if(e::class==HttpException::class){
                    viewModel?.onError(Resource.error("密码错误", null, 201))
                }else{
                    viewModel?.onError(Resource.error("网络不给力", null, 201))
                }
            }
        }
    }

    fun register(name:String,pass:String,recommendId:String,listener: () -> Unit){
        viewModelScope?.launch {
            try{
                val map=HashMap<String,String>()
                map.put("username",name)
                map.put("password",pass)
                map.put("recommendId",recommendId)
                val body=RequestBody.create(MediaType.parse("application/json"), Gson().toJson(map))
                val data = service.register(body)
                listener()
            }catch (e:Exception){
                if(e::class==HttpException::class){
                    viewModel?.onError(Resource.error("此账号已存在", null, 201))
                }else{
                    viewModel?.onError(Resource.error("网络不给力", null, 201))
                }

            }
        }
    }

    fun login(name:String,pass:String,listener: (TokenData) -> Unit){
        viewModelScope?.launch {
            try{
                val body=FormBody.Builder()
                    .add("username",name)
                    .add("client_id","client")
                    .add("client_secret","123456")
                    .add("grant_type","password")
                    .add("password",pass).build()
                val data = service.login(body)
                listener(data)
            }catch (e:Exception){
                viewModel?.onError(Resource.error("网络不给力", null, 201))
            }
        }
    }


    fun getVip(listener: (VipData) -> Unit) {
        viewModelScope?.launch {
            try{
                val data = service.getUserVIp()
                listener(data)
            }catch (e:Exception){
                Log.e("TAG",e.message.orEmpty())
                if(e::class==HttpException::class){
                    viewModel?.onError(Resource.error("网络不给力", null, 202))
                } else{
                    Log.e("TAG",e.message.orEmpty())
                    viewModel?.onError(Resource.error("网络不给力", null, 201))
                }
            }
        }
    }

    fun getVipPrice(listener: (List<VipData>) -> Unit) {
        viewModelScope?.launch {
            try{
                val data = service.getVipPrice()
                listener(data)
            }catch (e:Exception){
                Log.e("TAG",e.message.orEmpty())
                if(e::class==HttpException::class){
                    viewModel?.onError(Resource.error("网络不给力", null, 202))
                } else{
                    Log.e("TAG",e.message.orEmpty())
                    viewModel?.onError(Resource.error("网络不给力", null, 201))
                }
            }
        }
    }

    fun getPrice(listener: (PriceResultData) -> Unit) {
        viewModelScope?.launch {
            try{
                val data = service.getPrice()
                listener(data)
            }catch (e:Exception){
                Log.e("TAG",e.message.orEmpty())
                if(e::class==HttpException::class){
                    viewModel?.onError(Resource.error("网络不给力", null, 202))
                } else{
                    Log.e("TAG",e.message.orEmpty())
                    viewModel?.onError(Resource.error("网络不给力", null, 201))
                }
            }
        }
    }

    fun getDataFull(listener: () -> Unit) {
        viewModelScope?.launch {
            try{
                val data = service.getDataFull()
                listener()
            }catch (e:Exception){
                if(e::class==HttpException::class){
                    viewModel?.onError(Resource.error("网络不给力", null, 202))
                } else{
                    Log.e("TAG",e.message.orEmpty())
                    viewModel?.onError(Resource.error("网络不给力", null, 201))
                }
            }
        }
    }

    fun getList(listener: (List<LineData>) -> Unit) {
        viewModelScope?.launch {
            try{
                val data = service.getList()
                listener(data)
            }catch (e:Exception){
                if(e::class==HttpException::class){
                    viewModel?.onError(Resource.error("网络不给力", null, 202))
                } else{
                    Log.e("TAG",e.message.orEmpty())
                    viewModel?.onError(Resource.error("网络不给力", null, 201))
                }

            }
        }
    }

    fun getK(type:Int,date:String,listener: (List<KData>) -> Unit) {
        viewModelScope?.launch {
            try{
                val data = service.getK("datainfo/data/dailyData/get/"+type+"/"+date)
                listener(data)
            }catch (e:Exception){
                if(e::class==HttpException::class){
                    viewModel?.onError(Resource.error("网络不给力", null, 202))
                } else{
                    Log.e("TAG",e.message.orEmpty())
                    viewModel?.onError(Resource.error("网络不给力", null, 201))
                }

            }
        }
    }

    fun pay(vipType:Int,recommendId:String,listener: (PayData) -> Unit) {
        viewModelScope?.launch {
            try{
                val map=HashMap<String,String>()
                map.put("vipType",vipType.toString())
                map.put("recommendId",recommendId)
                val body=RequestBody.create(MediaType.parse("application/json"), Gson().toJson(map))
                val data = service.payForVip(body)
                listener(data)
            }catch (e:Exception){
                if(e::class==HttpException::class){
                    viewModel?.onError(Resource.error("网络不给力", null, 202))
                } else{
                    Log.e("TAG",e.message.orEmpty())
                    viewModel?.onError(Resource.error("网络不给力", null, 201))
                }

            }
        }
    }
}
