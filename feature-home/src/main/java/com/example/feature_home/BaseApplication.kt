package com.example.feature_home

import android.app.Application
import com.example.feature_home.data.dataModule
import com.example.feature_home.ui_home.homeModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BaseApplication)
            modules(
                listOf(
                dataModule,homeModule
            ))
        }
    }

}
