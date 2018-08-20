package kr.okky.app.android.global

import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer

object BusProvider :Bus(){
    val eventBus = Bus(ThreadEnforcer.ANY)
}
