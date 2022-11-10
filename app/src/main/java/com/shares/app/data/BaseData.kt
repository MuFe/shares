package com.shares.app.data

open class BaseData<T>(
    var code:Int=0,
    var total:Int=0,
    val msg:String="",
    var data:T
)