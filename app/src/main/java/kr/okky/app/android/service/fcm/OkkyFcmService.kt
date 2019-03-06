package kr.okky.app.android.service.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import android.widget.RemoteViews
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kr.okky.app.android.R
import kr.okky.app.android.global.*
import kr.okky.app.android.model.PushContent
import kr.okky.app.android.model.PushData
import kr.okky.app.android.ui.MainActivity
import kr.okky.app.android.utils.OkkyLog
import kr.okky.app.android.utils.OkkyUtils
import kr.okky.app.android.utils.Pref
import java.net.URL

class OkkyFcmService : FirebaseMessagingService() {
    companion object {
        val TAG:String = OkkyFcmService::class.java.simpleName
    }

    override fun onMessageReceived(rm: RemoteMessage?) {
        rm?.data?.isNotEmpty()?.let {
            //traceData(rm.data)
            val topActivity = OkkyUtils.getTopActivity(context = applicationContext)
            val pushData = PushData().parseData(rm.data)
            if (MainActivity::class.java.name == topActivity) {
                pushData.startingPoint = StartingPoint.BROADCAST
                //pushDataBroadcast(pushData)
            } else {
                pushData.startingPoint = StartingPoint.NOTIFICATION
                //pushDataNotify(pushData)
            }
            pushDataNotify(pushData)
        }
    }

    private fun traceData(data: Map<String, String>) {
        data.let {
            data.keys.iterator().forEach { k ->
                OkkyLog.err(TAG, "push bundle key=$k, value=${data[k]}")
            }
        }
    }

    override fun onNewToken(tk: String?) {
        OkkyLog.err(TAG, "Refreshed token : $tk")
        tk?.let {
            Pref.saveStringValue(StoreKey.FCM_TOKEN.name, tk)
        }
    }

    private fun pushDataBroadcast(data: PushData) {
        val it = Intent(BroadcastAction.PUSN_RECEIVE_ACTION.action).apply {
            putExtras(Bundle().apply {
                putParcelable(StoreKey.FCM_DATA.name, data)
            })
        }
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(it)
    }

    private fun pushDataNotify(pushData: PushData) {
        //OkkyLog.err(TAG, "push data : $pushData")
        val bundle = Bundle().apply {
            putParcelable(StoreKey.FCM_DATA.name, pushData)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            //addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP xor  Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtras(bundle)
        }

        val pendingIntent = PendingIntent.getActivities(this, 0, arrayOf(intent), PendingIntent.FLAG_ONE_SHOT)
        val channelId = getString(R.string.default_notification_channel_id)
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val title: String?
        val message: String?
        if (pushData.isAd()) {
            title = pushData.advertise?.title
            message = pushData.advertise?.message
        } else {
            title = pushData.content?.title
            message = pushData.content?.message
        }


        val builder: NotificationCompat.Builder =
                NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                        .setSound(soundUri)
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setContentText(message)

        val pushContent: PushContent?
        when {
            pushData.isAd() -> pushContent = pushData.advertise
            else -> pushContent = pushData.content
        }

        pushContent?.let {
            when {
                it.hasImage() -> {
                    val url = URL(it.image)
                    val image: Bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    builder.setStyle(
                            NotificationCompat.BigPictureStyle()
                                    .bigPicture(image)
                                    .setBigContentTitle(it.title)
                                    .setSummaryText(it.message)
                    )
                }
                else -> {
                    builder.setStyle(
                            NotificationCompat.BigTextStyle()
                                    .setBigContentTitle(it.title)
                                    .setSummaryText(it.message)
                    )
                }
            }
        }
        builder.setContentIntent(pendingIntent)

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.color = resources.getColor(R.color.colorAccent)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        with(notificationManager) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId,
                        TAG,
                        NotificationManager.IMPORTANCE_DEFAULT)
                createNotificationChannel(channel)
            }
        }

        notificationManager.notify(0, builder.build())

    }

    private fun getRemoteViews(data: PushContent): RemoteViews {
        return RemoteViews(packageName, R.layout.notification_remoteview).also {
            it.setImageViewResource(R.id.remote_image, R.mipmap.ic_launcher)
            it.setTextViewText(R.id.remote_title, data.title)
            it.setTextViewText(R.id.remote_message, data.message)
        }
    }
}