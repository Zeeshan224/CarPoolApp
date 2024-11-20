package com.example.carpoolapp.ui.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CarPoolApp : Application() {
    override fun onCreate() {
        super.onCreate()

    }
}
