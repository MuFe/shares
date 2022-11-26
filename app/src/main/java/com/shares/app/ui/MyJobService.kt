package com.shares.app.ui

import android.Manifest
import android.app.*
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.shares.app.R
import com.shares.app.extension.checkPermissions
import com.shares.app.util.CalendarReminderUtils
import java.util.*

class MyJobService:JobService() {
    override fun onStartJob(parameters: JobParameters): Boolean {
        job(parameters.transientExtras.getInt("id",1000),parameters.transientExtras.getString("title").orEmpty())
       return false
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return true
    }
    @RequiresApi(23)
    private fun isPermissionsGranted(): Boolean {
        return applicationContext.checkPermissions(Manifest.permission.READ_CALENDAR) && applicationContext.checkPermissions(
            Manifest.permission.WRITE_CALENDAR
        )
    }
    fun job(id:Int,title: String){
       if(isPermissionsGranted()){
           CalendarReminderUtils.deleteCalendarEvent(applicationContext,title)
       }
        var notification: Notification? = null
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val idStr = "channelId"
            val name = "channelName"
            val channel = NotificationChannel(idStr, name, NotificationManager.IMPORTANCE_LOW)
            manager!!.createNotificationChannel(channel)
            Notification.Builder(applicationContext)
                .setChannelId(idStr)
                .setSmallIcon(R.drawable.notify)
                .setStyle(Notification.DecoratedCustomViewStyle())
                .setContentTitle(title)
                .build()
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val idStr = "channelId"
            val name = "channelName"
            val channel = NotificationChannel(idStr, name, NotificationManager.IMPORTANCE_LOW)
            manager!!.createNotificationChannel(channel)
            Notification.Builder(applicationContext)
                .setChannelId(idStr)
                .setSmallIcon(R.drawable.notify)
                .setStyle(Notification.BigTextStyle().bigText(title))
                .build()
        } else {
            NotificationCompat.Builder(applicationContext)
                .setStyle(NotificationCompat.BigTextStyle().bigText(title))
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
        jb.setOverrideDeadline(86400*1000+20*1000)
        jb.setTransientExtras(bundleOf("title" to title,"id" to id))
        val jobInfo = jb.build()
        val jobScheduler =context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        jobScheduler.cancel(id)
        jobScheduler.schedule(jobInfo);
    }

}