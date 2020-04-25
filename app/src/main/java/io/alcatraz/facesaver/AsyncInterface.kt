package io.alcatraz.facesaver

interface AsyncInterface<T> {
    fun onDone(result: T)
}
