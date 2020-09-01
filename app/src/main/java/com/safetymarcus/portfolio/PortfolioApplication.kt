package com.safetymarcus.portfolio

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.HiltAndroidApp

/**
 * @author Marcus Hooper
 */
@HiltAndroidApp
class PortfolioApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

    companion object {
        lateinit var INSTANCE: PortfolioApplication
        val prefs: SharedPreferences by lazy {
            INSTANCE.getSharedPreferences(
                "user",
                Context.MODE_PRIVATE
            )
        }
    }
}