package com.vtnd.duynn

import android.app.Application
import com.vtnd.duynn.di.networkModule
import com.vtnd.duynn.di.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber
import kotlin.time.ExperimentalTime

/**
 * Created by duynn100198 on 3/17/21.
 */
@FlowPreview
@ExperimentalTime
@ExperimentalStdlibApi
@ExperimentalCoroutinesApi
@Suppress("unused")
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin()
        setupTimber()
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            // TODO(Timber): plant release tree
        }
    }

    private fun startKoin() {
        startKoin {
            // use AndroidLogger as Koin Logger
            androidLogger(level = if (BuildConfig.DEBUG) Level.DEBUG else Level.NONE)
            // use the Android context given there
            androidContext(this@MainApplication)
            modules(
                listOf(
                    appModule,
                    dispatcherModule,
                    networkModule,
                    dataSourceModule,
                    repositoryModule,
                    viewModelModule,
                    useCaseModule,
                    mapperModule
                )
            )
        }
    }
}
