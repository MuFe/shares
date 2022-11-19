package com.shares.app.ui

import androidx.lifecycle.MutableLiveData


interface MainHost {
    fun resetNavToHome()
    fun resetNavToLogin()

    fun change(price:String,rate:String,up:Boolean)

    fun changTime()
    fun getTime():MutableLiveData<String>


}
