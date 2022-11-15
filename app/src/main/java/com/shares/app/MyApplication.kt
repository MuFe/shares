package com.shares.app

import android.app.Application
import android.content.Context
import android.os.Looper
import android.util.Log

import com.shares.app.di.commonModule
import com.shares.app.di.networkModule
import com.shares.app.di.viewModelModule
import com.shares.app.image.ImageLoader
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@MyApplication)
            modules(
                viewModelModule,
                commonModule,
                networkModule,
            )
        }


        ImageLoader.getDefault().diskCacheOptions()
                .setDiskCacheDirPath(getExternalFilesDir("Cache")?.path ?: filesDir.path)
                .setDiskCacheFolderName("Image")
                .setDiskCacheSize(2 * 1024 * 1024) // 设置磁盘缓存2G
                .setBitmapPoolSize(2.0f)
                .setMemoryCacheSize(1.5f)
                .build()


    }


}