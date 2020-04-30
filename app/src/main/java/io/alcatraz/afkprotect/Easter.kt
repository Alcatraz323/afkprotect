package io.alcatraz.afkprotect

import android.content.Context
import android.content.Intent
import android.net.Uri

class Easter(private val context: Context) {
    private val target = intArrayOf(0, 1, 0, 1, 0, 0, 1, 0, 1, 0)
    private var current = 0

    fun shortClick() {
        val expect = target[current]
        if (expect == 0) {
            if (current == target.size - 1) {
                showEaster()
                clearCounter()
            } else {
                current++
            }
        } else {
            clearCounter()
        }
    }

    fun longClick() {
        val expect = target[current]
        if (expect == 1) {
            if (current == target.size - 1) {
                showEaster()
                clearCounter()
            } else {
                current++
            }
        } else {
            clearCounter()
        }
    }

    private fun showEaster() {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(Constants.EASTER_URL)
            )
        )
    }

    private fun clearCounter() {
        current = 0
    }
}
