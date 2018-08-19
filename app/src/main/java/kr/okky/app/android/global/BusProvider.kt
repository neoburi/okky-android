package kr.okky.app.android.global

import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer

class BusProvider private constructor() : Bus() {
    companion object {
        private val instance = Bus(ThreadEnforcer.ANY)
        fun instance(): Bus = instance
    }
}
