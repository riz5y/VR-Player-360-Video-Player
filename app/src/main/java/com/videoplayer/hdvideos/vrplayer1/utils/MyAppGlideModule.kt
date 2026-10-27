package com.videoplayer.hdvideos.vrplayer1.utils

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import java.io.File
import java.io.InputStream
import java.util.concurrent.TimeUnit
import javax.net.ssl.X509TrustManager

@GlideModule
class MyAppGlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val memoryCacheSizeBytes = 40L * 1024L * 1024L // 40 MB RAM
        builder.setMemoryCache(LruResourceCache(memoryCacheSizeBytes))
        builder.setBitmapPool(LruBitmapPool(memoryCacheSizeBytes))
        builder.setDiskCache(
            InternalCacheDiskCacheFactory(context, "glide_thumbs_cache", 150L * 1024L * 1024L) // 150 MB Disk
        )
        builder.setDefaultRequestOptions(
            RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .format(DecodeFormat.PREFER_RGB_565) // 50% less RAM, 2x faster bitmap decode
        )
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val cacheDir = File(context.cacheDir, "http_glide_cache")
        val cache = Cache(cacheDir, 100L * 1024L * 1024L) // 100 MB HTTP cache

        val client = OkHttpClient.Builder()
            .cache(cache)
            .connectionPool(ConnectionPool(15, 5, TimeUnit.MINUTES))
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .sslSocketFactory(
                UrlUtils.sslSocketFactory,
                UrlUtils.trustAllCerts[0] as X509TrustManager
            )
            .hostnameVerifier(UrlUtils.hostnameVerifier)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Referer", UrlUtils.REFERER)
                    .header("User-Agent", UrlUtils.USER_AGENT)
                    .build()
                chain.proceed(request)
            }
            .build()

        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(client)
        )
    }

    override fun isManifestParsingEnabled(): Boolean = false
}
