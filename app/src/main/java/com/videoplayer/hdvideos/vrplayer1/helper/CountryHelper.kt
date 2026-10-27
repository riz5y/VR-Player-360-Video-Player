package com.videoplayer.hdvideos.vrplayer1.helper

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import android.util.Log
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

object CountryHelper {
    private const val TAG = "CountryHelper"

    fun detectCountry(
        context: Context,
        onResult: (country: String) -> Unit
    ) {
        val telecomManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        val simCountry = telecomManager?.simCountryIso?.uppercase()?.trim().orEmpty()
        val networkCountry = telecomManager?.networkCountryIso?.uppercase()?.trim().orEmpty()
        val localeCountry = Locale.getDefault().country.uppercase().trim()

        Thread {
            var ipCountry = ""

            // 1. Primary Check: ipapi.co (HTTPS)
            try {
                val conn = (URL("https://ipapi.co/json/").openConnection() as HttpURLConnection).apply {
                    connectTimeout = 2500
                    readTimeout = 2500
                    setRequestProperty("User-Agent", "Mozilla/5.0")
                }
                val json = JSONObject(conn.inputStream.bufferedReader().readText())
                ipCountry = json.optString("country_code", "").uppercase().trim()
            } catch (e: Exception) {
                Log.e(TAG, "ipapi.co check failed: ${e.message}")
            }

            // 2. Secondary Check: ipwho.is (HTTPS)
            if (ipCountry.isEmpty()) {
                try {
                    val conn = (URL("https://ipwho.is/").openConnection() as HttpURLConnection).apply {
                        connectTimeout = 2500
                        readTimeout = 2500
                    }
                    val json = JSONObject(conn.inputStream.bufferedReader().readText())
                    if (json.optBoolean("success", false)) {
                        ipCountry = json.optString("country_code", "").uppercase().trim()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "ipwho.is check failed: ${e.message}")
                }
            }

            // 3. Fallback Check: ip-api.com (HTTP - Free tier requires HTTP)
            if (ipCountry.isEmpty()) {
                try {
                    val conn = (URL("http://ip-api.com/json/").openConnection() as HttpURLConnection).apply {
                        connectTimeout = 2500
                        readTimeout = 2500
                    }
                    val json = JSONObject(conn.inputStream.bufferedReader().readText())
                    ipCountry = json.optString("countryCode", "").uppercase().trim()
                } catch (e: Exception) {
                    Log.e(TAG, "ip-api.com check failed: ${e.message}")
                }
            }

            // Normalize Great Britain code
            if (ipCountry == "UK") ipCountry = "GB"

            // Fallback strategy hierarchy
            val resolvedCountry = when {
                ipCountry.isNotEmpty()      -> ipCountry
                simCountry.isNotEmpty()     -> simCountry
                networkCountry.isNotEmpty() -> networkCountry
                else                        -> localeCountry.ifEmpty { "DEFAULT" }
            }

            Log.d(TAG, "Resolved Country: $resolvedCountry (IP: $ipCountry, SIM: $simCountry, Locale: $localeCountry)")

            Handler(Looper.getMainLooper()).post {
                onResult(resolvedCountry)
            }
        }.start()
    }
}