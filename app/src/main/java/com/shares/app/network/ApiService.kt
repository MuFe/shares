package com.shares.app.network



import com.shares.app.data.*
import okhttp3.FormBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("authserver/user/saveUser")
    suspend fun register(@Body body: RequestBody): Any


    @POST("authserver/oauth/token?client_id=client&client_secret=123456&grant_type=password")
    suspend fun login(@Body body: FormBody):TokenData

    @Headers("Content-Type: application/json")
    @GET()
    suspend fun getPassword(@Url url:String): PassData

    @Headers("Content-Type: application/json")
    @GET("userserver/user/getUserVIp")
    suspend fun getUserVIp(): VipData

    @Headers("Content-Type: application/json")
    @GET("userserver/user/getVipPrice")
    suspend fun getVipPrice(): List<VipData>

    @Headers("Content-Type: application/json")
    @GET("datainfo/data/getPrice")
    suspend fun getPrice(): PriceResultData

    @Headers("Content-Type: application/json")
    @GET("/datainfo/data/getDataFull")
    suspend fun getDataFull(): Any

    @Headers("Content-Type: application/json")
    @GET("datainfo/data/get/ZYData")
    suspend fun getList(): List<LineData>

    @Headers("Content-Type: application/json")
    @GET()
    suspend fun getK(@Url url:String): List<KData>

    @Headers("Content-Type: application/json")
    @POST("userserver/user/payForVip")
    suspend fun payForVip(@Body body: RequestBody): PayData
}


