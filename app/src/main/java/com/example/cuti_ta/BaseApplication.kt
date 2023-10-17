package com.example.cuti_ta

import android.app.Application
import com.example.cuti_ta.Helper.NotificationHelper

class BaseApplication : Application() {

    companion object {
        lateinit var notificationHelper: NotificationHelper
    }

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(applicationContext)
    }
}

