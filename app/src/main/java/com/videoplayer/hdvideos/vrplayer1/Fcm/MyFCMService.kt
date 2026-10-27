package com.videoplayer.hdvideos.vrplayer1.Fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.videoplayer.hdvideos.vrplayer1.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class MyFCMService : FirebaseMessagingService() {

    private var title: String? = null
    private var body: String? = null
    private var description: String? = null
    private var icon: String? = null
    private var featureGlobeMap: String? = null
    private var bitmap: Bitmap? = null
    private var bitIcon: Bitmap? = null
    private val CHANNEL_ID_MY_FCM: String = "id_channel"
    private val CHANNEL_NAME_MY_FCM: String = "name_channel"
    private val CHANNEL_DESCRIPTION_MY_FCM: String = "description_channel"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.i("onMessageReceived","onMessageReceived")
        Log.i("Notification","onMessageReceived")

        try {
            if (remoteMessage.data["title"]!=null) {
                title = remoteMessage.data["title"]
            }
            if (remoteMessage.data["app_url"]!=null) {
                body = remoteMessage.data["app_url"]
            }
            if (remoteMessage.data["short_desc"]!=null) {
                description = remoteMessage.data["short_desc"]
            }
            if (remoteMessage.data["icon"]!=null){
                icon = remoteMessage.data["icon"]
            }
            if (remoteMessage.data["feature"]!=null){
                featureGlobeMap = remoteMessage.data["feature"]
            }
        } catch (e: Exception) {
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID_MY_FCM, CHANNEL_NAME_MY_FCM, importance)
            mChannel.description = CHANNEL_DESCRIPTION_MY_FCM
            mChannel.enableLights(true)
            mChannel.lightColor = Color.BLUE
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            try {
                mNotificationManager.createNotificationChannel(mChannel)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            if (icon!=null){
                getBitmapImageFromRemoteUrl(icon!!)
            }
            if (featureGlobeMap!=null){
                getBitmapImageFromRemoteUrl2(featureGlobeMap!!)
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            Log.i("onMessageReceived","displayFcmNotification")
            displayFcmNotification()
        },5000)

    }

     fun displayFcmNotification() {
        val bigView = RemoteViews(packageName, R.layout.layout_fcm_big_notification)
        val smallView = RemoteViews(packageName, R.layout.layout_fcm_small_notification)
        if (description != null) {
            bigView.setTextViewText(R.id.text, description)
            smallView.setTextViewText(R.id.text, description)
        }
        if (title != null) {
            bigView.setTextViewText(R.id.titlePlaceName, title)
            smallView.setTextViewText(R.id.titlePlaceName, title)
        }

        if (bitIcon != null) {
            smallView.setImageViewBitmap(R.id.image_app, bitIcon)
            bigView.setImageViewBitmap(R.id.image_app, bitIcon)
        }
        if (bitmap != null) {
            bigView.setImageViewBitmap(R.id.image_pic, bitmap)
        }
        if (body != null) {
            val notificationComp =
                NotificationCompat.Builder(this, CHANNEL_ID_MY_FCM)
                    .setSmallIcon(R.drawable.ic_baseline_notification_24)
                    .setCustomBigContentView(bigView).setCustomContentView(smallView)
//                    .setShowWhen(false)
            val resultIntent = Intent(Intent.ACTION_VIEW, Uri.parse(body))
            resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            resultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_IMMUTABLE)

            } else {
                PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            }

            notificationComp.setContentIntent(pendingIntent)
            val notificaitonManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificaitonManager.notify(1, notificationComp.build())
        }
    }


    private suspend fun getBitmapImageFromRemoteUrl(imageUrl: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                var input: InputStream? = null
                try {
                    input = connection.inputStream
                } catch (e: IOException) {
                    Log.i("onMessageReceived","CoroutineScope in bitIcon: "+e.message.toString())
                    Log.i("onMessageReceived","CoroutineScope in  bitIcon: "+e.localizedMessage?.toString())
                    e.printStackTrace()
                }

                Log.i("onMessageReceived","CoroutineScope bitIcon")
                bitIcon = BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                Log.i("onMessageReceived","CoroutineScope out  bitIcon: "+e.message.toString())
                Log.i("onMessageReceived","CoroutineScope out  bitIcon: "+e.localizedMessage?.toString())
                e.printStackTrace()
            }
        }
    }
    private suspend fun getBitmapImageFromRemoteUrl2(imageUrl: String){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                var input: InputStream? = null
                try {
                    input = connection.inputStream
                } catch (e: IOException) {
                    Log.i("onMessageReceived","CoroutineScope in bitmap: "+e.message.toString())
                    Log.i("onMessageReceived","CoroutineScope in  bitmap: "+e.localizedMessage?.toString())
                    e.printStackTrace()

                }
                Log.i("onMessageReceived","CoroutineScope bitmap")
                bitmap = BitmapFactory.decodeStream(input)

            } catch (e: Exception) {
                Log.i("onMessageReceived","CoroutineScope out  bitmap: "+e.message.toString())
                Log.i("onMessageReceived","CoroutineScope out  bitmap: "+e.localizedMessage?.toString())
                e.printStackTrace()

            }
        }
    }

}