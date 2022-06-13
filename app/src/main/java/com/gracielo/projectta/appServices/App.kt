package com.gracielo.projectta.appServices

import android.app.Application
import android.content.Intent

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startService(Intent(this, MyServices::class.java))
    }
}