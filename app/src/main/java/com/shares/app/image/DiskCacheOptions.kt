package com.shares.app.image

import androidx.annotation.FloatRange

class DiskCacheOptions private constructor(builder: Builder){

    val bitmapPoolSize: Float
    val memoryCacheSize: Float
    val diskCacheDirPath: String?
    val diskCacheFolderName: String
    val diskCacheSize: Long

    init {
        this.bitmapPoolSize = builder.bitmapPoolSize
        this.memoryCacheSize = builder.memoryCacheSize
        this.diskCacheDirPath = builder.diskCacheDirPath
        this.diskCacheFolderName = builder.diskCacheFolderName
        this.diskCacheSize = builder.diskCacheSize
    }

    data class Builder(
            var bitmapPoolSize: Float = .0f,
            var memoryCacheSize: Float = .0f,
            var diskCacheDirPath: String? = null,
            var diskCacheFolderName: String = "Image",
            var diskCacheSize: Long = 1 * 1024 * 1024
    ) {

        /**
         * Bitmap池size, Bitmap池，值范围 1-3，建议在 Application 中设置
         */
        fun setBitmapPoolSize(@FloatRange(from = 1.0, to = 3.0) bitmapPoolSize: Float) = apply { this.bitmapPoolSize = bitmapPoolSize }

        /**
         * 内存缓存size, 默认内存缓存, 值范围 1-2 建议在 Application 中设置
         */
        fun setMemoryCacheSize(@FloatRange(from = 1.0, to = 2.0) memoryCacheSize: Float) = apply { this.memoryCacheSize = memoryCacheSize }

        /**
         * 磁盘缓存文件夹地址
         */
        fun setDiskCacheDirPath(dirPath: String) = apply { this.diskCacheDirPath = dirPath }

        /**
         * 磁盘缓存文件夹目录，默认 image
         */
        fun setDiskCacheFolderName(folderName: String)  =apply { this.diskCacheFolderName = folderName }

        /**
         * 磁盘缓存size，默认 1G
         */
        fun setDiskCacheSize(size: Long) = apply { this.diskCacheSize = size }

        fun build() = DiskCacheOptions(this)
    }
}
