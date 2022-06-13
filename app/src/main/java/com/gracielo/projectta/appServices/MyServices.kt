package com.gracielo.projectta.appServices

import android.content.Intent
import android.os.IBinder
import com.gracielo.projectta.ui.login.TestLoginActivity
import android.app.PendingIntent
import android.app.Service
import androidx.core.app.NotificationCompat
import com.gracielo.projectta.appServices.MyServices
import com.gracielo.projectta.R
import com.gracielo.projectta.util.NOTIFICATION_CHANNEL_ID
import com.gracielo.projectta.util.NOTIFICATION_ID

class MyServices : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        // do your jobs here
        startForeground()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForeground() {
        val notificationIntent = Intent(this, TestLoginActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, 0
        )
//        startForeground(
//            NOTIFICATION_ID, NotificationCompat.Builder(
//                this,
//                NOTIFICATION_CHANNEL_ID
//            ) // don't forget create a notification channel first
//                .setOngoing(true)
//                .setSmallIcon(R.drawable.ic_notifications)
//                .setContentTitle(getString(R.string.app_name))
//                .setContentText("Service is running background")
//                .setContentIntent(pendingIntent)
//                .build()
//        )
    }

}