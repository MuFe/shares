package com.shares.app.ui

import android.app.*
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.shares.app.R
import java.util.*

class MyJobService:JobService() {
    override fun onStartJob(parameters: JobParameters): Boolean {
        job( parameters.transientExtras.getString("title").orEmpty())
       return false
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return true
    }

    fun job(title: String){
        var notification: Notification? = null
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val id = "channelId"
            val name = "channelName"
            val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW)
            manager!!.createNotificationChannel(channel)
            Notification.Builder(applicationContext)
                .setChannelId(id)
                .setStyle(Notification.BigTextStyle().bigText(title))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.small_icon)
                .build()
        } else {
            NotificationCompat.Builder(applicationContext)
                .setStyle(NotificationCompat.BigTextStyle().bigText(title))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.small_icon)
                .build()
        }
        rePeat(applicationContext,title)
        manager!!.notify(1, notification)
    }

    fun rePeat(context: Context,title:String){
        val jb = JobInfo.Builder(
            1000, ComponentName(
                context.packageName,
                MyJobService::class.java.getName()
            )
        )
        jb.setMinimumLatency(86400)
        jb.setOverrideDeadline(86400+60*1000)
        jb.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)

        val jobInfo = jb.build()
        val jobScheduler =context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        jobScheduler.cancel(1000)
        jobScheduler.schedule(jobInfo);
    }

}