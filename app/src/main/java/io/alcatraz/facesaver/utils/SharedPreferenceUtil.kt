package io.alcatraz.facesaver.utils

import android.content.Context
import android.content.SharedPreferences
import io.alcatraz.facesaver.Constants

class SharedPreferenceUtil {

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

    fun getPref(context: Context, key: String, defValue: Any): Any? {
        val sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        when (defValue) {
            is Int -> sharedPreferences.getInt(key, defValue)
            is Boolean -> sharedPreferences.getBoolean(key, defValue)
            is Float -> sharedPreferences.getFloat(key, defValue)
            is Long -> sharedPreferences.getLong(key, defValue)
            is String -> sharedPreferences.getString(key, defValue)
        }
        return null
    }

    companion object {
        private const val FILE_NAME = Constants.MY_PACKAGE_NAME + "_preferences"
        private var mInstance: SharedPreferenceUtil? = null

        val instance: SharedPreferenceUtil
            get() {
                if (mInstance == null) {
                    synchronized(SharedPreferenceUtil::class.java) {
                        if (mInstance == null) {
                            mInstance = SharedPreferenceUtil()
                        }
                    }
                }
                return mInstance!!
            }
    }
}
