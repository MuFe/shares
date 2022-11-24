package com.shares.app.ui

import android.app.*
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.shares.app.R
import java.util.*

class MyJobService:JobService() {
    override fun onStartJob(parameters: JobParameters): Boolean {
        Log.e("TAG",System.currentTimeMillis().toString())
        job(parameters.transientExtras.getInt("id",1000),parameters.transientExtras.getString("title").orEmpty())
       return false
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return true
    }

    fun job(id:Int,title: String){
        var notification: Notification? = null
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val id = "channelId"
            val name = "channelName"
            val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW)
            manager!!.createNotificationChannel(channel)
            Notification.Builder(applicationContext)
                .setChannelId(id)
                .setStyle(Notification.DecoratedCustomViewStyle())
                .setContentTitle(title)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.notify)
                .build()
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val id = "channelId"
            val name = "channelName"
            val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW)
            manager!!.createNotificationChannel(channel)
            Notification.Builder(applicationContext)
                .setChannelId(id)
                .setStyle(Notification.BigTextStyle().bigText(title))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.notify)
                .build()
        } else {
            NotificationCompat.Builder(applicationContext)
                .setStyle(NotificationCompat.BigTextStyle().bigText(title))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.notify)
                .build()
        }
        rePeat(id,applicationContext,title)
        manager!!.notify(id, notification)
    }

    fun rePeat(id:Int,context: Context,title:String){
        val jb = JobInfo.Builder(
            id, ComponentName(
                context.packageName,
                MyJobService::class.java.getName()
            )
        )
        jb.setMinimumLatency(86400*1000)
        jb.setOverrideDeadline(86400*1000+60*1000)
//        jb.setMinimumLatency(180*1000)
//        jb.setOverrideDeadline(180*1000+60*1000)
        jb.setTransientExtras(bundleOf("title" to title,"id" to id))
        val jobInfo = jb.build()
        val jobScheduler =context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        jobScheduler.cancel(id)
        jobScheduler.schedule(jobInfo);
    }

}