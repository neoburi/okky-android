package kr.okky.app.android.utils

import android.util.Log
import kr.okky.app.android.BuildConfig
import kr.okky.app.android.global.TAG

object OkkyLog {

    fun log(msg: String) {
            log(TAG, msg)
    }

    fun log(tag:String, msg:String){
        if(BuildConfig.DEBUG){
            Log.d(tag, msg)
        }
    }

    fun err(msg: String, t: Throwable) {
        if(BuildConfig.DEBUG){
            Log.e(TAG, msg, t)
        }
    }

    fun err(tag:String, msg:String){
        if(BuildConfig.DEBUG){
            Log.e(tag, msg)
        }
    }
}
