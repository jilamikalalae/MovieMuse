package com.example.moviemuse

import android.app.Application
import com.example.moviemuse.utils.AppLifecycleObserver

class MovieMuseApplication : Application() {

    lateinit var lifecycleObserver: AppLifecycleObserver

    override fun onCreate() {
        super.onCreate()
        lifecycleObserver = AppLifecycleObserver.getInstance(this)
    }
}