package com.shares.app.image

import android.content.Context
import android.util.Log
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class DiskCacheModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        // 设置内存缓存
        val mOptions = ImageLoader.getDefault().diskCacheOptions().build()
        // 内存缓存大小计算器
        val mCalculator = MemorySizeCalculator.Builder(context)
                .setBitmapPoolScreens(mOptions.bitmapPoolSize)
                .setMemoryCacheScreens(mOptions.memoryCacheSize)
                .build()
        // Bitmap池, LruBitmapPool:负责控制缓存
        builder.setBitmapPool(LruBitmapPool(mCalculator.bitmapPoolSize.toLong()))
        // 内存缓存
        builder.setMemoryCache(LruResourceCache(mCalculator.memoryCacheSize.toLong()))
        // 设置磁盘缓存
        val mDiskCacheDirPath = mOptions.diskCacheDirPath ?: context.filesDir.path
        builder.setDiskCache(DiskLruCacheFactory(mDiskCacheDirPath, mOptions.diskCacheFolderName, mOptions.diskCacheSize))
        // 日志
        builder.setLogLevel(Log.ERROR)
    }

    override fun isManifestParsingEnabled(): Boolean = false
}
