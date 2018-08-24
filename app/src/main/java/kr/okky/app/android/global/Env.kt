package kr.okky.app.android.global

import android.content.Context
import android.content.pm.PackageManager



enum class Url constructor(private val url: String) {
    DEV("http://192.168.25.49:8080/okky"),
    SERVICE("https://okky.kr");

    fun get(): String = url
}

enum class Mode{
    DEV, SERVICE
}

const val TAG:String = "OKKY"
const val DRAWER_MENU_JSON = "NAVIGATION_DRAWER_MENU_JSON"
const val DRAWER_MENU_CHANGED = "DRAWER_MENU_CHANGED"

val MODE: Mode = Mode.SERVICE

fun getUrl():String{
    return when(MODE){
        Mode.SERVICE -> Url.SERVICE.get()
        Mode.DEV -> Url.DEV.get()
    }
}

fun getLoginUrl(url:String?):String
        = getUrl().plus("/login/auth?redirectUrl=").plus(getLoginRedirectPath(url!!))

fun getLoginRedirectPath(url:String):String
        = url.replace("^(http|https)://okky.kr".toRegex(), "")


val PERMISSIONS = arrayOf(
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.CAMERA"
)

fun loadResourceId(context: Context, resName: String, resIdName: String): Int {
    return if (resName.isEmpty() || resIdName.isEmpty()) {
        -1
    } else context.resources.getIdentifier(resIdName, resName, context.packageName)
}
