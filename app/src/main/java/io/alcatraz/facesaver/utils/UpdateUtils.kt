package io.alcatraz.facesaver.utils

import java.io.File

object UpdateUtils {
    val FILE_DEFAULT_EXPIRATION_MILLS = 24 * 3600 * 1000
    val WEATHER_FILE_DEFAULT_EXPIRATION_MILLS = 5 * 60 * 1000

    @JvmOverloads
    fun checkFileNeedsUpdate(
        dir: String,
        mills_to_expire: Long = FILE_DEFAULT_EXPIRATION_MILLS.toLong()
    ): Boolean {
        val toCheck = File(dir)
        val expi_min = (mills_to_expire / 60000).toDouble()
        return toCheck.lastModified() + mills_to_expire < System.currentTimeMillis()
    }

    fun checkUpdate(): Boolean {
        //TODO : Implement this method
        return false
    }
}
