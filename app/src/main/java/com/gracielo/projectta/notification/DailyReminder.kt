package com.gracielo.projectta.notification

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.gracielo.projectta.R
import com.gracielo.projectta.ui.login.TestLoginActivity
import com.gracielo.projectta.util.*
import java.util.*

class DailyReminder : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {

        executeThread {
            var time = intent.getStringExtra("time")
            if(time.isNullOrEmpty()) time="Eat"
            showNotification(context, time)
        }
    }

    //TODO 12 : Implement daily reminder for every 06.00 a.m using AlarmManager
    fun setDailyReminder(context: Context) {

        val sharedPreference = context.getSharedPreferences("Notification Time", Context.MODE_PRIVATE)
        val breakfastTime = sharedPreference.getString("Breakfast","")!!
        val lunchTime = sharedPreference.getString("Lunch","")!!
        val dinnerTime = sharedPreference.getString("Dinner","")!!
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intentBreakfast = Intent(context, DailyReminder::class.java)
        val intentLunch = Intent(context, DailyReminder::class.java)
        val intentDinner = Intent(context, DailyReminder::class.java)

        val splitBreakfast = breakfastTime.split(":")
        val calendarBreakfast = Calendar.getInstance()
        calendarBreakfast.set(Calendar.HOUR_OF_DAY, splitBreakfast[0].toInt())
        calendarBreakfast.set(Calendar.MINUTE, splitBreakfast[1].toInt())
        calendarBreakfast.set(Calendar.SECOND,0)
        intentBreakfast.putExtra("time","Breakfast")

        val splitlunch = lunchTime.split(":")
        val calendarLunch = Calendar.getInstance()
        calendarLunch.set(Calendar.HOUR_OF_DAY,splitlunch[0].toInt())
        calendarLunch.set(Calendar.MINUTE, splitlunch[1].toInt())
        calendarLunch.set(Calendar.SECOND,0)
        intentLunch.putExtra("time","Lunch")

        val splitDinner = dinnerTime.split(":")
        val calendarDinner = Calendar.getInstance()
        calendarDinner.set(Calendar.HOUR_OF_DAY, splitDinner[0].toInt())
        calendarDinner.set(Calendar.MINUTE, splitDinner[1].toInt())
        calendarDinner.set(Calendar.SECOND,0)
        intentDinner.putExtra("time","Dinner")

        val pendingIntentBreakfast = PendingIntent.getBroadcast(context, ID_REPEATING_BREAKFAST, intentBreakfast, 0)
        val pendingIntentLunch = PendingIntent.getBroadcast(context, ID_REPEATING_LUNCH, intentLunch, 0)
        val pendingIntentDinner = PendingIntent.getBroadcast(context, ID_REPEATING_DINNER, intentDinner, 0)

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendarBreakfast.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntentBreakfast)
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendarLunch.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntentLunch)
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendarDinner.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntentDinner)

    }



    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val i = Intent(context, DailyReminder::class.java)
        val pendingIntentBreakfast = PendingIntent.getBroadcast(context, ID_REPEATING_BREAKFAST, i, 0)
        val pendingIntentLunch = PendingIntent.getBroadcast(context, ID_REPEATING_LUNCH, i, 0)
        val pendingIntentDinner = PendingIntent.getBroadcast(context, ID_REPEATING_DINNER, i, 0)
        pendingIntentBreakfast.cancel()
        pendingIntentLunch.cancel()
        pendingIntentDinner.cancel()
        alarmManager.cancel(pendingIntentBreakfast)
        alarmManager.cancel(pendingIntentLunch)
        alarmManager.cancel(pendingIntentDinner)
    }

    private fun showNotification(context: Context,time:String) {
        //TODO 13 : Show today schedules in inbox style notification & open HomeActivity when notification tapped
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val i = Intent(context, TestLoginActivity::class.java)
        var contentText = "Hey it's time for ${time} now, Check out The Recipe You Want To Eat Now"
        val notificationStyle = NotificationCompat.BigTextStyle().bigText(contentText)
        val pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.logo_apps)
            .setContentTitle("Eat Reminder")
            .setStyle(notificationStyle)
            .setContentText(contentText)
            .setAutoCancel(true)
            .setSound(alarmSound)
            .setVibrate( longArrayOf(200, 500, 200,500,200))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NOTIFICATION_CHANNEL_ID)

            val ringtoneManager = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()

            val channel =
                NotificationChannel(NOTIFICATION_CHANNEL_ID,NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)

            channel.enableLights(true)
            channel.lightColor = Color.WHITE
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(200, 500, 200,500,200)
            channel.setSound(ringtoneManager, audioAttributes)
            notificationManager.createNotificationChannel(channel)
        }
        val notification=builder.build()
        notification.flags = Notification.FLAG_AUTO_CANCEL or Notification.FLAG_ONGOING_EVENT
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}