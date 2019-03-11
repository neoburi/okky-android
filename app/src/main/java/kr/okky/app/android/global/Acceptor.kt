package kr.okky.app.android.global

interface Acceptor {

    fun accept()

    fun deny()

    fun accept(vararg params: Any)

    fun deny(vararg params: Any)

}
