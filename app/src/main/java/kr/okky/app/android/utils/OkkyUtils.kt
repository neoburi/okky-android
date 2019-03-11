package kr.okky.app.android.utils

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kr.okky.app.android.global.StoreKey
import kr.okky.app.android.model.NaviMenu
import kr.okky.app.android.model.PushSetData
import java.io.*


object OkkyUtils {
    private const val menuFile = "drawerMenu.json"
    private const val pushFile = "push.json"
    fun checkDrawerMenuJsonInPref(context:Context){
        if(Pref.getStringValue(StoreKey.DRAWER_MENU_JSON.name, "").isNullOrEmpty()){
            Pref.saveStringValue(
                    StoreKey.DRAWER_MENU_JSON.name,
                    readMenuJsonFromAssets(context.assets, menuFile)
            )
        }
        checkPushSetJsonInPref(context)
    }

    fun notExistStoredMenuJson():Boolean
            = Pref.getStringValue(StoreKey.DRAWER_MENU_JSON.name, "").isNullOrEmpty()

    fun createNavigationDrawerMenu(): List<NaviMenu>{
        val jsonStr = Pref.getStringValue(StoreKey.DRAWER_MENU_JSON.name, "[]")!!
        val listType = object : TypeToken<ArrayList<NaviMenu>>() {}.type
        return Gson().fromJson(jsonStr, listType)
    }

    val shareTargetExceptions = ArrayList<String>().apply {
        add("/articles/promote")
        add("/articles/recruit")
        add("/articles/resumes")
        add("/user/info/35324")
        add("/articles/recruit?filter.jobType=FULLTIME")
        add("info@okky.kr")
        add("https://github.com/okjsp/okky/issues")
        add("settings://")
        add("/user/info/35324")
    }

    fun loadShareTargetSections():List<NaviMenu>{
        var secs = createNavigationDrawerMenu()
        return secs.filterIndexed { index, naviMenu -> index < 5 }
    }

    fun checkPushSetJsonInPref(context: Context):Boolean
        = when(Pref.getStringValue(StoreKey.PUSH_SET_DATA_JSON.name, "").isNullOrEmpty()){
            true->{
                Pref.saveStringValue(
                    StoreKey.PUSH_SET_DATA_JSON.name,
                    readMenuJsonFromAssets(context.assets, pushFile)
                )
                true
            }
            else->{
                false
            }
    }

    fun loadPushSettingData():List<PushSetData>{
        val jsonStr = Pref.getStringValue(StoreKey.PUSH_SET_DATA_JSON.name, "[]")
        val listType = object : TypeToken<ArrayList<PushSetData>>(){}.type
        return Gson().fromJson(jsonStr, listType)
    }

    fun storePushSetDataJsonToPref(list:List<PushSetData>){
        val jsonTxt = Gson().toJson(list)
        Pref.saveStringValue(StoreKey.PUSH_SET_DATA_JSON.name, jsonTxt)
    }

    fun getActiveTopics():String{
        val items = loadPushSettingData()
        val target = ArrayList<PushSetData>()
        items.forEach {
            if(it.active == true){
                target.add(it)
            }
        }
        return Gson().toJson(target)
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

    fun readMenuJsonFromAssets(assetManager: AssetManager, assetFilename:String): String {
        val contents = StringBuilder()
        var input: InputStream? = null
        var reader: BufferedReader? = null
        try {
            input = assetManager.open(assetFilename)
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