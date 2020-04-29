package io.alcatraz.facesaver.utils

import java.io.File

object UpdateUtils {
    const val FILE_DEFAULT_EXPIRATION_MILLS = 24 * 3600 * 1000
    const val WEATHER_FILE_DEFAULT_EXPIRATION_MILLS = 5 * 60 * 1000

    @JvmOverloads
    fun checkFileNeedsUpdate(
        dir: String,
        mills_to_expire: Long = FILE_DEFAULT_EXPIRATION_MILLS.toLong()
    ): Boolean {
        val toCheck = File(dir)
        return toCheck.lastModified() + mills_to_expire < System.currentTimeMillis()
    }

    fun checkUpdate(): Boolean {
        return false
    }
}
