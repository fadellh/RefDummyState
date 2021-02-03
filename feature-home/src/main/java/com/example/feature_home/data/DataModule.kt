package com.example.feature_home.data

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.feature_home.BuildConfig
import com.example.feature_home.data.api.ApiExecutor
import com.example.feature_home.data.api.Constant
import com.example.feature_home.data.api.apiClient
import com.example.feature_home.data.api.httpClient
import com.example.feature_home.data.api.service.RefferalService
import com.example.feature_home.data.repository.RefferalRepository
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.CookieJar
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.android.get
import org.koin.dsl.module
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.CookieStore
import javax.inject.Singleton


val Application.dataModule get() = module {

//    single<CookieStore> { SharedPreferencesCookieStore(applicationContext, COOKIE_NAME) }
//    single<CookieHandler> { CookieManager(get(), CookiePolicy.ACCEPT_ALL) }
//    single<CookieJar> { JavaNetCookieJar(get()) }

    single { Moshi.Builder().build() }
    single { ApiExecutor(get()) }

    single { httpClient(TIMEOUT, HttpLoggingInterceptor.Level.BODY, ChuckerInterceptor.Builder(get()).build()) }

    single { apiClient<RefferalService>(get()) }

    single { Room.databaseBuilder(
            get(),
            CoreDatabase::class.java,
            REFFERAL_DATABASE
    ).fallbackToDestructiveMigration().build() }

    single { get<CoreDatabase>().refferalDao() }

    single { RefferalRepository(get(), get(), get()) }

}

private const val TIMEOUT = 30L

private const val REFFERAL_DATABASE = "refferal_db"