package kr.okky.app.android.utils

import android.content.Context
import android.content.res.AssetManager
import java.io.*


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

    fun readMenuJsonFromAssets(assetManager: AssetManager): String {
        var contents = StringBuilder()
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
}