package com.example.moviemuse.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.example.moviemuse.model.Movie
import com.example.moviemuse.service.NotificationService

class AppLifecycleObserver(private val application: Application) : Application.ActivityLifecycleCallbacks {

    private var activeActivityCount = 0
    private var lastAddedToWatchlist: Movie? = null
    private val notificationService = NotificationService(application)

    init {
        application.registerActivityLifecycleCallbacks(this)
        Log.d(TAG, "AppLifecycleObserver initialized")
    }

    fun setLastAddedToWatchlist(movie: Movie) {
        lastAddedToWatchlist = movie
        Log.d(TAG, "Added to watchlist: ${movie.title}")

        // For testing - show notification immediately regardless of app state
        notificationService.scheduleWatchlistReminder(movie)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Log.d(TAG, "Activity created: ${activity.javaClass.simpleName}")
    }

    override fun onActivityStarted(activity: Activity) {
        activeActivityCount++
        Log.d(TAG, "Activity started: ${activity.javaClass.simpleName}, active count: $activeActivityCount")
    }

    override fun onActivityResumed(activity: Activity) {
        Log.d(TAG, "Activity resumed: ${activity.javaClass.simpleName}")
    }

    override fun onActivityPaused(activity: Activity) {
        Log.d(TAG, "Activity paused: ${activity.javaClass.simpleName}")
    }

    override fun onActivityStopped(activity: Activity) {
        activeActivityCount--
        Log.d(TAG, "Activity stopped: ${activity.javaClass.simpleName}, active count: $activeActivityCount")
        checkIfAppIsInBackground()
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        Log.d(TAG, "Activity destroyed: ${activity.javaClass.simpleName}")
    }

    private fun checkIfAppIsInBackground() {
        if (activeActivityCount == 0 && lastAddedToWatchlist != null) {
            // App is going to background, schedule notification
            Log.d(TAG, "App going to background, scheduling notification for: ${lastAddedToWatchlist?.title}")
            notificationService.scheduleWatchlistReminder(lastAddedToWatchlist!!)
            lastAddedToWatchlist = null // Reset after scheduling
        }
    }

    companion object {
        private const val TAG = "AppLifecycleObserver"
        private var instance: AppLifecycleObserver? = null

        fun getInstance(application: Application): AppLifecycleObserver {
            if (instance == null) {
                instance = AppLifecycleObserver(application)
            }
            return instance!!
        }
    }
}