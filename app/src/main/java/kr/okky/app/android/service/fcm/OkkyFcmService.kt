package kr.okky.app.android.service.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kr.okky.app.android.R
import kr.okky.app.android.global.ActionType
import kr.okky.app.android.global.StoreKey
import kr.okky.app.android.model.PushData
import kr.okky.app.android.ui.MainActivity
import kr.okky.app.android.utils.OkkyLog
import kr.okky.app.android.utils.Pref

class OkkyFcmService : FirebaseMessagingService() {
    companion object {
        val TAG:String = OkkyFcmService::class.java.simpleName
    }
    override fun onMessageReceived(rm: RemoteMessage?) {
        OkkyLog.err(TAG, "Remote message from : ${rm?.from}")
        rm?.data?.isNotEmpty()?.let {
            OkkyLog.err(TAG, "Message data : ${rm.data}")
            sendNotification(PushData().parseData(rm.data))
        }
    }

    override fun onNewToken(tk: String?) {
        OkkyLog.err(TAG, "Refreshed token : $tk")
        tk?.let {
            Pref.saveStringValue(StoreKey.FCM_TOKEN.name, tk)
        }
    }

    private fun sendNotification(pushData:PushData){
        OkkyLog.err(TAG, "push data : ${pushData.string()}")
        var intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP xor  Intent.FLAG_ACTIVITY_NEW_TASK)

        var bundle = Bundle()
        bundle.putParcelable(StoreKey.FCM_DATA.name, pushData)
        bundle.putString(StoreKey.ACTION_FLAG.name, ActionType.FCM.name)
        intent.putExtras(bundle)

        val pendingIntent = PendingIntent.getActivities(this, 0, arrayOf(intent), PendingIntent.FLAG_ONE_SHOT)
        val channelId = getString(R.string.default_notification_channel_it)
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(pushData.title)
                .setContentText(pushData.message)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId,
                    pushData.title,
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, builder.build())

    }
}