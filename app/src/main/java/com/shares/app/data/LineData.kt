package com.shares.app.data


import com.shares.app.extension.toDateStr
import java.io.Serializable
import java.text.DecimalFormat

class LineData(
    val createTime:String,
    val wOption:Float,
    val cOption:Float,
    val sOption:Float,
    val rOption:Float,
){
    var createTimeStr: String?=null
    var createDayTimeStr: String?=null
    fun getTimeStr():String{
        if(createTimeStr.isNullOrEmpty()){
            if(createTime.length>10){
                createTimeStr=createTime.substring(0,10)
            }else{
                createTimeStr=(createTime.toDateStr("yyyy-MM-dd'T'")/1000).toDateStr("yyyy-MM-dd")
            }
        }
        return createTimeStr.orEmpty()
    }

    fun getTimeDayStr():String{
        if(createDayTimeStr.isNullOrEmpty()){
            if(createTime.length>10){
                createDayTimeStr=createTime.substring(8,10)
            }else{
                createDayTimeStr=(createTime.toDateStr("yyyy-MM-dd'T'")/1000).toDateStr("dd")
            }
        }
        return createDayTimeStr.orEmpty()
    }

    fun getDataFromIndex(index:Int):Float{
        if(index==0){
            return wOption
        }else if(index==1){
            return rOption
        }else if(index==2){
            return cOption
        }else{
            return rOption
        }
    }
}