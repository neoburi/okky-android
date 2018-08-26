package kr.okky.app.android.global

import android.app.Application
import kr.okky.app.android.utils.Pref

class OkkyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Pref.init(this)
    }
}