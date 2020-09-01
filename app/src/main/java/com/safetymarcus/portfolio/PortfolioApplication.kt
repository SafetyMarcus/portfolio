package com.safetymarcus.portfolio

import android.app.Application
import android.content.Context
import com.safetymarcus.portfolio.utils.DispatchScope
import com.safetymarcus.portfolio.video.VideoCaptureContract
import com.safetymarcus.portfolio.video.VideoCaptureContractStore
import com.safetymarcus.portfolio.video.VideoCaptureController
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * @author Marcus Hooper
 */
class PortfolioApplication : Application() {

    //DI modules
    private val videoModule = module {
        factory { (view: VideoCaptureContract.View, store: VideoCaptureContractStore, scope: DispatchScope) ->
            VideoCaptureController(view, store, scope)
        }
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        startKoin {
            loadKoinModules(videoModule)
        }
    }

    companion object {
        lateinit var INSTANCE: PortfolioApplication
        val prefs by lazy { INSTANCE.getSharedPreferences("user", Context.MODE_PRIVATE) }
    }
}