package com.videoplayer.hdvideos.vrplayer1.utils

import java.net.URI
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object UrlUtils {

    const val REFERER    = "https://antiquewhite-louse-208094.hostingersite.com/"
    const val USER_AGENT = "Mozilla/5.0 (Linux; Android 12) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"

    fun encode(rawUrl: String): String {
        return try {
            val url = URL(rawUrl)
            URI(
                url.protocol, url.userInfo, url.host, url.port,
                url.path, url.query, url.ref
            ).toASCIIString()
        } catch (e: Exception) {
            rawUrl
        }
    }

    val trustAllCerts: Array<TrustManager> = arrayOf(
        object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }
    )

    val sslContext: SSLContext = SSLContext.getInstance("TLS").apply {
        init(null, trustAllCerts, SecureRandom())
    }

    // ✅ Explicitly typed as SSLSocketFactory — fixes "Unresolved reference: socketFactory"
    val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory as SSLSocketFactory

    val hostnameVerifier: HostnameVerifier = HostnameVerifier { _, _ -> true }

    fun trustAllForConnection(connection: java.net.URLConnection) {
        if (connection is HttpsURLConnection) {
            connection.sslSocketFactory = sslSocketFactory
            connection.hostnameVerifier = hostnameVerifier
        }
    }
}