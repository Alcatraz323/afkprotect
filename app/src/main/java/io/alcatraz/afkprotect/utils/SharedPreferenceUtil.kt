package io.alcatraz.afkprotect.utils

import android.content.Context
import io.alcatraz.afkprotect.Constants

object SharedPreferenceUtil {
    private const val FILE_NAME = Constants.MY_PACKAGE_NAME + "_preferences"
    fun put(context: Context, key: String, value: Any) {
        val sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        when (value) {
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Float -> editor.putFloat(key, value)
            is Long -> editor.putLong(key, value)
            is String -> editor.putString(key, value)
        }
        editor.apply()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getPref(context: Context, key: String, defValue: T): T? {
        val sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        var value: Any? = null
        when (defValue) {
            is Int -> value = sharedPreferences.getInt(key, defValue)
            is Boolean -> value = sharedPreferences.getBoolean(key, defValue)
            is Float -> value = sharedPreferences.getFloat(key, defValue)
            is Long -> value = sharedPreferences.getLong(key, defValue)
            is String -> value = sharedPreferences.getString(key, defValue)
        }
        return value as T
    }
}
