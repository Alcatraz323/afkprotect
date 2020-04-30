package io.alcatraz.afkprotect

interface AsyncInterface<T> {
    fun onDone(result: T)
}
