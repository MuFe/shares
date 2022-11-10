package com.shares.app.extension

import android.annotation.SuppressLint
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun Long.toDateStr(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
    val date = Date(this * 1000)
    val format = SimpleDateFormat(pattern)
    return format.format(date)
}

@SuppressLint("SimpleDateFormat")
fun String.toDateStr(pattern: String = "yyyy-MM-dd HH:mm:ss"): Long {
    val format = SimpleDateFormat(pattern)
    var tLong: Long = 0
    try {
        tLong = format.parse(this).time
    } catch (e: Exception) {
    }
    return tLong
}

@SuppressLint("SimpleDateFormat")
fun String.getMaxDateFromMonth(pattern: String = "yyyy-MM-dd HH:mm:ss"): Int {
    val format = SimpleDateFormat(pattern)
    val a = Calendar.getInstance()
    try {
        a.time = format.parse(this)
        a.set(Calendar.DATE, 1)
        a.roll(Calendar.DATE, -1) //日期回滚一天，也就是最后一天
    } catch (e: Exception) {
    }
    return a.get(Calendar.DATE)
}

@SuppressLint("SimpleDateFormat")
fun String.getTimeDiff(pattern: String = "yyyy-MM-dd HH:mm:ss"): Long {
    val format = SimpleDateFormat(pattern)
    var tLong: Long = 0
    try {
        tLong = format.parse(this).time
    } catch (e: Exception) {
    }
    val now=System.currentTimeMillis()
    val diff=now-tLong
    return diff/1000
}

fun Int.toDateStr(): String {
    val s = this / 1000
    var temp1 = (s / 60).toString()
    if (temp1.length < 2) {
        temp1 = "0" + temp1
    }
    var temp2 = (s % 60).toString()
    if (temp2.length < 2) {
        temp2 = "0" + temp2
    }
    return temp1 + ":" + temp2
}

/**
 * 获取星座
 */
fun  String.calculate(pattern: String = "yyyy-MM-dd HH:mm:ss"):Int {
    val format = SimpleDateFormat(pattern)
    val a = Calendar.getInstance()
    try {
        a.time = format.parse(this)
    } catch (e: Exception) {
    }
    val m=a.get(Calendar.MONTH)+1
    val d=a.get(Calendar.DAY_OF_MONTH)
    var name1=0
    if((m ==3 && d>20) || (m ==4 && d<21)) {
        name1 = 0
    }else if((m ==4 && d>20) || (m ==5 && d<21)){
        name1 = 1
    }else if((m ==5 && d>20) || (m ==6 && d<22)){
        name1=2
    }else if((m ==6 && d>21) || (m ==7 && d<23)){
        name1=3
    }else if((m ==7 && d>22) || (m ==8 && d<23)){
        name1=4
    }else if((m ==8 && d>22) || (m ==9 && d<23)){
        name1=5
    }else if((m ==9 && d>22) || (m ==10 && d<23)){
        name1=6
    }else if((m ==10 && d>22) || (m ==11 && d<22)){
        name1=7
    }else if((m ==11 && d>21) || (m ==12 && d<22)){
        name1=8
    }else if((m ==12 && d>21) || (m ==1 && d<20)){
        name1=9
    }else if((m ==1 && d>19) || (m ==2 && d<19)){
        name1=10
    }else if((m ==2 && d>18) || (m ==3 && d<21)) {
        name1 = 11
    }
    return name1;
}