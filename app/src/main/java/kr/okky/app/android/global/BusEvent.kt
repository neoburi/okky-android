package kr.okky.app.android.global

class BusEvent(val event: Evt){
    enum class Evt{
        BOTTOM_HISTORY,
        BOTTOM_DISABLE,
        DRAWER_RELOAD
    }
}
