package com.shares.app.image

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import com.bumptech.glide.Glide
import java.io.File
import java.io.FileInputStream


class ImageLoader {

    companion object {

        @Volatile
        private var INSTANCES: ImageLoader? = null

        fun getDefault(): ImageLoader = INSTANCES
                ?: synchronized(this) { ImageLoader().also { INSTANCES = it } }
    }


    fun diskCacheOptions() = DiskCacheOptions.Builder()

    /**
     * 暂停当前上下文中的Glide请求
     */
    fun pauseRequests(context: Context) {
        Glide.with(context).pauseRequests()
    }

    /**
     * 暂停所有Glide请求
     */
    fun pauseAllRequests(context: Context) {
        Glide.with(context).pauseAllRequests()
    }

    /**
     * 恢复Glide请求
     */
    fun resumeRequests(context: Context) {
        Handler(Looper.getMainLooper()).post { Glide.with(context).resumeRequests() }
    }

    /**
     * 清除Glide的磁盘缓存
     */
    fun clearDiskCache(context: Context) {
        Glide.get(context).clearDiskCache()
    }

    /**
     * 清除Glide的磁盘缓存，与上面函数作用一致。获取上下文的方式不同而已。
     */
    fun clear(view: View) {
        Glide.with(view).clear(view)
    }

    /**
     * 清除Glide的内存缓存
     */
    fun clearMemory(context: Context) {
        Glide.get(context).clearMemory()
    }

    /**
     * 清除一些内存缓存，具体数量取决于给应用分配的级别。
     */
    fun trimMemory(context: Context, level: Int) {
        Glide.get(context).trimMemory(level)
    }

    /**
     * 获取磁盘缓存的数据大小，单位：KB
     */
    fun getDiskCacheSize(context: Context): Long {
        val options = diskCacheOptions().build()
        val diskCacheDirPath = options.diskCacheDirPath ?: context.filesDir.path
        val diskCacheFolderName = options.diskCacheFolderName
        val file = File("$diskCacheDirPath${File.separator}$diskCacheFolderName")
        var blockSize = 0L
        try {
            blockSize = if (file.isDirectory) getFileSizes(file) else getFileSize(file)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return blockSize
    }

    private fun getFileSizes(file: File): Long {
        var size = 0L
        file.listFiles()?.forEach {
            if (it.isDirectory) {
                size += getFileSizes(it)
            } else {
                try {
                    size += getFileSize(it)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return size
    }

    private fun getFileSize(file: File): Long {
        var size = 0L
        if (file.exists()) {
            FileInputStream(file).use {
                size = it.available().toLong()
            }
        }
        return size
    }


}