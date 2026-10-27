//package com.videoplayer.hdvideos.vrplayer1.ads
//
//import android.Manifest
//import android.app.Application
//import android.content.Context
//import android.content.SharedPreferences
//import android.content.pm.PackageManager
//import android.database.ContentObserver
//import android.os.Handler
//import android.os.Looper
//import android.util.Log
//import androidx.appcompat.app.AppCompatDelegate
//import androidx.core.content.ContextCompat
//import androidx.multidex.MultiDex
//import com.google.firebase.FirebaseApp
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//
//
//class MyApp : Application() {
//
//    companion object {
//        var gpsArea: MyApp? = null
//        var appsPreferences: SharedPreferences? = null
//
//      var appOpenAdManagerLocator: AppOpenAdResume? = null
//
//        private lateinit var contactObserver: ContentObserver
//        private val handler = Handler(Looper.getMainLooper())
//         var isMonitoringStarted = false
//        @JvmStatic
//        fun getStr(id: Int): String {
//            return gpsArea!!.getString(id)
//        }
//
//        fun myAppCheckForDarkMode() {
//            if (appsPreferences != null) {
//                val isDarkSelected = appsPreferences!!.getBoolean("isDarkModeOnP", false)
//                setNightDayMode(isDarkSelected)
//            }
//        }
//
//        fun setNightDayMode(status: Boolean) {
//            if (status) {
//                try {
//                    AppCompatDelegate
//                        .setDefaultNightMode(
//                            AppCompatDelegate.MODE_NIGHT_YES)
//                } catch (e: Exception) {
//                }
//            } else {
//                try {
//                    AppCompatDelegate
//                        .setDefaultNightMode(
//                            AppCompatDelegate.MODE_NIGHT_NO
//                        )
//                } catch (e: Exception) {
//                }
//            }
//        }
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//
//        gpsArea = this
//        appsPreferences = getSharedPreferences(
//            "sharedPrefs", MODE_PRIVATE
//        )
//    /*    FontsOverride.setDefaultFont(this, "DEFAULT", "Montserrat-Medium.otf")
//        FontsOverride.setDefaultFont(this, "MONOSPACE", "Montserrat-Medium.otf")*/
//
//
//
//        try {
//            FirebaseApp.initializeApp(this)
//        } catch (e: Exception) {
//        }
//        myAppCheckForDarkMode()
//      appOpenAdManagerLocator = AppOpenAdResume(this)
//       // sStreetViewTruckCTAppOpenSplashAdManager = PhoneLocatorAppOpenSplashAdManager(this)
//        //sStreetViewhighTruckCTAppOpenSplashAdManager = HghPhoneLocatorAppOpenSplashAdManager(this)
//      //  honeyBeeGPSOpenAdManager = PhoneLocatorOpenAdManager(this)
////        PhoneLocatorLoadAds.insertADSToFirebase(this)
//       // getDataFromFirebase()
//
//
////        // Only start if permissions already granted (returning users)
////        if (hasContactsPermissions()) {
////            startContactMonitoring()
////        }
//
//    }
//
//    override fun attachBaseContext(base: Context) {
//        super.attachBaseContext(base)
//        MultiDex.install(this)
//    }
//
//    private fun getDataFromFirebase() {
//        try {
//            val databaseReference =
//                FirebaseDatabase.getInstance().getReference("PhoneLocatorTrackerAdsID")
//            databaseReference.child("ADS_IDS").addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    val model = dataSnapshot.getValue<MyAdsModel>(
//                        MyAdsModel::class.java
//                    )
//                    if (model != null) {
//
//                        PhoneLocatorLoadAds.current_counter = model.current_counter
//                        PhoneLocatorLoadAds.adShowAfter=model.adShowAfter
//
//                        Log.d("ondata", "onDataChange: "+ PhoneLocatorLoadAds.adShowAfter)
//                        Log.d("ondata", "onDataChange: "+ PhoneLocatorLoadAds.interstitial_admob_inApp)
////                        Log.d("ondata", "onDataChange: "+ PhoneLocatorLoadAds.banner_large_admob_inApp)
//
//                    }else{
////                        Log.d("ondata", "onDataChange: "+ PhoneLocatorLoadAds.banner_large_admob_inApp)
//                    }
//                }
//
//                override fun onCancelled(databaseError: DatabaseError) {
//                    Log.i("LoadAds", databaseError.message)
//                }
//            })
//        } catch (e: Exception) {
//            Log.i("LoadAds", e.message.toString())
//        }
//    }
//
//    private fun hasContactsPermissions(): Boolean {
//        return ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.READ_CONTACTS
//        ) == PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.WRITE_CONTACTS
//                ) == PackageManager.PERMISSION_GRANTED
//    }
//}


package com.videoplayer.hdvideos.vrplayer1.ads

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.multidex.MultiDex
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyApp : Application() {

    companion object {
        var gpsArea: MyApp? = null
        var appsPreferences: SharedPreferences? = null
        var appOpenAdManagerLocator: AppOpenAdResume? = null
        private lateinit var contactObserver: ContentObserver
        private val handler = Handler(Looper.getMainLooper())
        var isMonitoringStarted = false

        @JvmStatic
        fun getStr(id: Int): String {
            return gpsArea!!.getString(id)
        }

        fun myAppCheckForDarkMode() {
            if (appsPreferences != null) {
                val isDarkSelected = appsPreferences!!.getBoolean("isDarkModeOnP", false)
                setNightDayMode(isDarkSelected)
            }
        }

        fun setNightDayMode(status: Boolean) {
            if (status) {
                try {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } catch (e: Exception) {
                }
            } else {
                try {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                } catch (e: Exception) {
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        gpsArea = this
        appsPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)

        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
        }
        myAppCheckForDarkMode()

        // ← DO NOT initialize AppOpenAdResume here anymore
        // It is now initialized in SplashActivity AFTER Remote Config sets canShowAppOpen
        // appOpenAdManagerLocator = AppOpenAdResume(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    private fun getDataFromFirebase() {
        try {
            val databaseReference =
                FirebaseDatabase.getInstance().getReference("PhoneLocatorTrackerAdsID")
            databaseReference.child("ADS_IDS").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val model = dataSnapshot.getValue(MyAdsModel::class.java)
                    if (model != null) {
                        PhoneLocatorLoadAds.current_counter = model.current_counter
                        PhoneLocatorLoadAds.adShowAfter = model.adShowAfter
                        Log.d("ondata", "onDataChange: " + PhoneLocatorLoadAds.adShowAfter)
                        Log.d("ondata", "onDataChange: " + PhoneLocatorLoadAds.interstitial_admob_inApp)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.i("LoadAds", databaseError.message)
                }
            })
        } catch (e: Exception) {
            Log.i("LoadAds", e.message.toString())
        }
    }

    private fun hasContactsPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED
    }
}