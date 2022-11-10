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
    fun getTimeStr():String{
        return (createTime.toDateStr("yyyy-MM-dd'T'")/1000).toDateStr("yyyy-MM-dd")
    }

    fun getTimeDayStr():String{
        return (createTime.toDateStr("yyyy-MM-dd'T'")/1000).toDateStr("dd")
    }

    fun getDataFromIndex(index:Int):Float{
        if(index==0){
            return wOption
        }else if(index==1){
            return cOption
        }else if(index==2){
            return sOption
        }else{
            return rOption
        }
    }
}