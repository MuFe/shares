package com.shares.app.ui

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.shares.app.R
import java.util.*


class MyReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.getExtras()
        if (extras != null) {
            val title=extras.getString("title").orEmpty()
            var notification: Notification? = null
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val id = "channelId"
                val name = "channelName"
                val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW)
                manager!!.createNotificationChannel(channel)
                Notification.Builder(context)
                    .setChannelId(id)
                    .setStyle(Notification.BigTextStyle().bigText(title))
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.small_icon)
                    .build()
            } else {
                NotificationCompat.Builder(context)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(title))
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.small_icon)
                    .build()
            }
            rePeat(context,title)
            manager!!.notify(1, notification)
        }
    }

    fun rePeat(context: Context,title:String){
        val i = Intent(context, MyReceiver::class.java)
        i.putExtra("title",title)
        //创建PendingIntent对象
        //创建PendingIntent对象
        val pi: PendingIntent = PendingIntent.getBroadcast(context, 0, i, 0)
        val c: Calendar = Calendar.getInstance()
        c.setTimeInMillis(System.currentTimeMillis())

        c.add(Calendar.SECOND, 86400)

        val am: AlarmManager =  context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //API19以上使用
            am.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi)
        } else {
            am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi)
        }
    }
}