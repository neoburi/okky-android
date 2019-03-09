package kr.okky.app.android.global

import android.content.Context
import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

enum class UrlCompareValue constructor(private val path: String) {
    INTENT("intent:"),
    MARKET("market://"),
    EMAIL("mailto:"),
    HTTP("http"),
    GOOGLE_OAUTH("/oauth/google"),
    LOGIN("/login/authAjax"),
    LOGIN_FAIL("/login/authfail?ajax=true");

    fun value(): String = path
}

enum class Url constructor(private val url: String) {
    DEV("http://192.168.25.49:8080/okky"),
    SERVICE("https://okky.kr");

    fun get(): String = url
}

enum class Mode{
    DEV, SERVICE
}

enum class StoreKey{
    FCM_TOKEN, FCM_DATA, DATA_STARTING_POINT
}

enum class StartingPoint {
    NOTIFICATION, BROADCAST
}

enum class FcmDataKey(
        var key: String
) {
    PUSH_TYPE("type"),
    PUSH_TITLE("title"),
    PUSH_MESSAGE("message"),
    PUSH_URL("url"),
    PUSH_IMAGE("image")
}

enum class BroadcastAction(
        var action: String
) {
    PUSN_RECEIVE_ACTION("kr.okky.app.android.PUSH_RECEIVE")
}

enum class PushType(
        var type: String,
        var key: String
) {
    CONTENT("CONTENT", "content"), ADVERTISE("AD", "advertise")
}

const val TAG:String = "OKKY"
const val DRAWER_MENU_JSON = "NAVIGATION_DRAWER_MENU_JSON"
const val DRAWER_MENU_CHANGED = "DRAWER_MENU_CHANGED"

val MODE: Mode = Mode.SERVICE
const val DEVICE_TYPE: String = "A"

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

fun isHome(url: String?): Boolean = url?.matches("^(http?).*(okky.kr(/?))$".toRegex())!!

fun isList(url: String?): Boolean = !isHome(url).and(url?.matches("^(http?).*(\\D)$".toRegex())!!)

fun isArticleOpen(url: String?): Boolean = url?.matches("^(http?).*(\\d)$".toRegex())!!

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

fun getPublicDirectory(publicDir: String): File = Environment.getExternalStoragePublicDirectory(publicDir)

val timeStamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA).format(Date())!!

fun createFilenameWithTimestamp(prefix: String, suffix: String): String = prefix.plus(timeStamp).plus(suffix)

fun createFilenameWithTimestamp(prefix: String, suffix: String, ext: String): String = createFilenameWithTimestamp(prefix, suffix).plus(ext)

enum class FileExt constructor(private val ext: String) {
    JPEG(".JPG"),
    PNG(".PNG"),
    AVI(".AVI"),
    MP3(".MP3"),
    MP4(".MP4");

    fun value(): String = ext
}

enum class MimeType constructor(private val type: String) {
    TEXT_PLAIN("text/plain"),
    TEXT_HTML("text/html"),
    IMAGE_PNG("image/png"),
    IMAGE_JPG("image/jpg"),
    IMAGE_JPEG("image/jpeg"),
    PDF("application/pdf"),
    AVI("video/x-msvideo"),
    MPEG("video/mpeg"),
    ZIP("application/zip");

    fun value(): String = type
}