package kr.okky.app.android.service.fcm

import android.content.Context
import android.content.Intent
import android.support.v4.content.WakefulBroadcastReceiver
import android.os.Bundle
import android.support.v4.app.NotificationCompat.getExtras
import kr.okky.app.android.utils.OkkyLog


class FcmIntentReceiver : WakefulBroadcastReceiver() {
    companion object {
        val TAG = FcmIntentReceiver::class.java.simpleName
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        OkkyLog.log(TAG, "fcm action : ${intent?.action}")
        intent?.let {
            if (intent.getExtras() != null) {
                for (key in intent.getExtras().keySet()) {
                    val value = intent.getExtras().get(key)
                    OkkyLog.err("FirebaseDataReceiver", "Key: $key Value: $value")
                    if (key.equals("gcm.notification.body", ignoreCase = true) && value != null) {
                        /*val bundle = Bundle()
                        val backgroundIntent = Intent(context, BackgroundSyncJobService::class.java)
                        bundle.putString("push_message", value.toString() + "")
                        backgroundIntent.putExtras(bundle)
                        context?.startService(backgroundIntent)*/
                    }
                }
            }
        }
    }
}