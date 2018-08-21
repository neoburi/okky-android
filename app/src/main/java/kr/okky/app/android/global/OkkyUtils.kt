package kr.okky.app.android.global

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

object OkkyUtils {
    const val menuFile = "drawerMenu.json"

    fun existDrawerMenuJson(context:Context):Boolean{
        val file = File(context.cacheDir.absolutePath, menuFile)
        return file.exists()
    }

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
}