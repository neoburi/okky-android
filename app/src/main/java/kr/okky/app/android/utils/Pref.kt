package kr.okky.app.android.utils

import android.content.Context
import android.content.SharedPreferences

object Pref {
    private var preferences: SharedPreferences? = null
    private const val prefName = "APP_PREF"

    fun init(context: Context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
        }
    }

    fun saveStringValue(key: String, value: String): Boolean
            = preferences!!.edit().putString(key, value).commit()

    fun getStringValue(key: String, defValue: String): String?
            = preferences!!.getString(key, defValue)

    fun saveIntValue(key: String, value: Int): Boolean
            = preferences!!.edit().putInt(key, value).commit()

    fun getIntValue(key: String, def: Int): Int
            = preferences!!.getInt(key, def)

    fun saveLongValue(key: String, value: Long): Boolean
            = preferences!!.edit().putLong(key, value).commit()

    fun getLongValue(key: String, def: Long): Long
            = preferences!!.getLong(key, def)

    fun saveBooleanValue(key: String, value: Boolean): Boolean
            = preferences!!.edit().putBoolean(key, value).commit()

    fun getBooleanValue(key: String, def: Boolean): Boolean
            = preferences!!.getBoolean(key, def)
}
