package io.alcatraz.facesaver.utils

interface PermissionInterface {
    fun onResult(requestCode: Int, permissions: Array<String>, granted: IntArray)
}
