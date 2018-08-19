package kr.okky.app.android.global

import android.util.Log
import kr.okky.app.android.BuildConfig

object OkkyLog {

    fun log(msg: String) {
        (BuildConfig.DEBUG).let {
            Log.d(TAG, msg)
        }
    }

    fun err(msg: String, t: Throwable) {
        (BuildConfig.DEBUG).let {
            Log.e(TAG, msg, t)
        }
    }
}
