package com.safetymarcus.portfolio

import android.app.Application

/**
 * @author Marcus Hooper
 */
class PortfolioApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

    companion object {
        lateinit var INSTANCE: PortfolioApplication
    }
}