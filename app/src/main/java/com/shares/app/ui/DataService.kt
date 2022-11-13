package com.shares.app.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import com.shares.app.R
import com.shares.app.base.BaseModel
import com.shares.app.extension.toDateStr
import com.shares.app.network.Status
import com.shares.app.util.NetworkUtil
import com.shares.app.util.PreferenceUtil
import com.xdandroid.hellodaemon.AbsWorkService
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

class DataService:AbsWorkService() {
    private val networkUtil: NetworkUtil by inject()
    private val util: PreferenceUtil by inject()
    var temp=1


    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        networkUtil.viewModelScope=CoroutineScope(Dispatchers.IO )
        startGetDataFull()
    }

    fun startGetDataFull(){
        CoroutineScope(Dispatchers.IO ).launch {
            getData()
            delay(12000)
            startGetDataFull()
        }
    }

    fun getData(){
        networkUtil.getPrice {
            var rate=0.0f
            temp++
            if(it.yesterday.price!=0f){
                rate=100*(it.current.price-it.yesterday.price)/it.yesterday.price
                rate+=temp
            }
            var isPlus=false
            var change=""
            if(rate>=0){
                isPlus=true
                change="+"+String.format("%.2f",rate)+"%"
            }else{
                isPlus=false
                change=String.format("%.2f",rate)+"%"
            }
            setPrice(it.current.price.toString(),change,isPlus)
        }

    }

    fun setPrice(now:String,change:String,plus:Boolean){
        var notification: Notification? = null
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        val remoteViews: RemoteViews =
            RemoteViews(applicationContext.getPackageName(), R.layout.notify) //RemoteView传入布局
        remoteViews.setTextViewText(R.id.today,now)
        if(plus){
            remoteViews.setTextColor(R.id.today,applicationContext.resources.getColor(R.color.red))
            remoteViews.setTextColor(R.id.change,applicationContext.resources.getColor(R.color.red))
        }else{
            remoteViews.setTextColor(R.id.today,applicationContext.resources.getColor(R.color.green))
            remoteViews.setTextColor(R.id.change,applicationContext.resources.getColor(R.color.green))
        }

        remoteViews.setTextViewText(R.id.change,change)
        notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val id = "channelId"
            val name = "channelName"
            val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW)
            manager!!.createNotificationChannel(channel)
            Notification.Builder(applicationContext)
                .setChannelId(id)
                .setCustomContentView(remoteViews)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.icon)
                .build()
        } else {
            NotificationCompat.Builder(applicationContext)
                .setContent(remoteViews)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.icon)
                .build()
        }
        manager!!.notify(1, notification)
    }

    override fun onBind(intent: Intent?, alwaysNull: Void?): IBinder? {
       return null
    }

    override fun shouldStopService(intent: Intent?, flags: Int, startId: Int): Boolean {
       return !util.getCheck1()
    }

    override fun startWork(intent: Intent?, flags: Int, startId: Int) {

    }

    override fun stopWork(intent: Intent?, flags: Int, startId: Int) {

    }

    override fun isWorkRunning(intent: Intent?, flags: Int, startId: Int): Boolean {
        return true
    }

    override fun onServiceKilled(rootIntent: Intent?) {
        TODO("Not yet implemented")
    }
}