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
import com.shares.app.network.Status
import com.shares.app.util.NetworkUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class DataService:Service() {
    private val networkUtil: NetworkUtil by inject()
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        networkUtil.viewModelScope=GlobalScope
    }

    fun getData(){


    }

    fun setPrice(){
        var notification: Notification? = null
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        val remoteViews: RemoteViews =
            RemoteViews(applicationContext.getPackageName(), R.layout.notify) //RemoteView传入布局
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

}