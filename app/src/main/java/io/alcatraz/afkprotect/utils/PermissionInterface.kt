package io.alcatraz.afkprotect.utils

interface PermissionInterface {
    fun onResult(requestCode: Int, permissions: Array<String>, granted: IntArray)
}
