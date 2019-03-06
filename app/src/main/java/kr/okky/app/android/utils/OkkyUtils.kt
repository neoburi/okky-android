package kr.okky.app.android.utils

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kr.okky.app.android.global.DRAWER_MENU_JSON
import kr.okky.app.android.global.TAG
import kr.okky.app.android.model.NaviMenu
import java.io.*
import java.util.*


object OkkyUtils {
    const val menuFile = "drawerMenu.json"
    fun checkDrawerMenuJsonOfPref(context:Context){
        if(notExistStoredMenuJson()){
            Pref.saveStringValue(
                    DRAWER_MENU_JSON,
                    readMenuJsonFromAssets(context.assets)
            )
        }
    }

    fun notExistStoredMenuJson():Boolean
            = Pref.getStringValue(DRAWER_MENU_JSON, "").isNullOrEmpty()

    fun getDrawerMenuJson():String
            = Pref.getStringValue(DRAWER_MENU_JSON, "[]")!!

    fun createNavigationDrawerMenu(): List<NaviMenu>{
        val jsonStr = OkkyUtils.getDrawerMenuJson()
        val listType = object : TypeToken<ArrayList<NaviMenu>>() {}.type
        return Gson().fromJson<List<NaviMenu>>(jsonStr, listType)
    }

    fun existDrawerMenuJson(context:Context):Boolean
            = File(context.cacheDir.absolutePath, menuFile).exists()

    fun copyDrawerMenuJsonToCacheDir(context:Context):Boolean
            = copyAssetFileToTarget(context, menuFile, File(context.cacheDir.absolutePath, menuFile))

    fun copyAssetFileToTarget(context: Context, fileName: String, target: File): Boolean {
        var input: InputStream? = null
        try {
            input = context.assets.open(fileName)
            return copyTo(input!!, target)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            input?.close()
        }
        return false
    }

    fun readMenuJsonFromAssets(assetManager: AssetManager): String {
        val contents = StringBuilder()
        var input: InputStream? = null
        var reader: BufferedReader? = null
        try {
            input = assetManager.open(menuFile)
            reader = BufferedReader(InputStreamReader(input))
            while (true) {
                val line = reader.readLine()
                if(line == null) {
                    break
                }else{
                    contents.append(line)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                input?.close()
                reader?.close()
            } catch (e: IOException) {}
        }
        return contents.toString()
    }

    fun copyTo(inputStream: InputStream, target: File): Boolean {
        val out = FileOutputStream(target)
        try {
            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (true) {
                bytesRead = inputStream.read(buffer)
                if(bytesRead >= 0) {
                    out.write(buffer, 0, bytesRead)
                }else{
                    break
                }
            }
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            out.flush()
            out.close()
        }
        return false
    }

    fun loadRaw(context: Context, rawId: Int): String {
        val inputData = context.resources.openRawResource(rawId)
        val stringBuilder = StringBuilder()
        var bufferedReader:BufferedReader? = null
        try {
            bufferedReader = BufferedReader(InputStreamReader(inputData, "UTF-8"))
            while (true) {
                val str = bufferedReader.readLine()
                if(!str.isNullOrEmpty()) {
                    stringBuilder.append(str).append("\n")
                }else{
                    break
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }finally {
            bufferedReader?.close()
        }

        return stringBuilder.toString()
    }

    fun getVersionName(context: Context): String =
            getPackageInfo(context)?.versionName ?: "unknown"

    fun getVersionCode(context: Context, targetPackage: String): Int =
            getPackageInfo(context)?.versionCode ?: -1

    fun getPackageInfo(context:Context): PackageInfo? =
            context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_META_DATA)

    fun getTopActivity(context: Context): String {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val taskInfo = am.getRunningTasks(1)
        val componentInfo = taskInfo[0].topActivity
        /*OkkyLog.err(TAG,
                "CURRENT Activity ::${taskInfo[0].topActivity.className}, Package Name : ${componentInfo.packageName}")*/
        return taskInfo[0].topActivity.className
    }
}