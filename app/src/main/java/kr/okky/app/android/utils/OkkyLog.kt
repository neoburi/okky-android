package kr.okky.app.android.utils

import android.util.Log
import kr.okky.app.android.BuildConfig
import kr.okky.app.android.global.TAG

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
