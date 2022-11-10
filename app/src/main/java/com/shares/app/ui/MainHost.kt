package com.shares.app.ui


interface MainHost {
    fun resetNavToHome()
    fun resetNavToLogin()
    fun showFlow(isShow:Boolean)
    fun change(price:String,rate:String,up:Boolean)
}
