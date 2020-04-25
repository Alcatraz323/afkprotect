package io.alcatraz.facesaver.core

import android.content.Context
import android.content.Intent
import android.os.Handler

class AccessibilityEventTaskManager(var context: Context) {
    val protectorTask: Runnable = Runnable {
        goHome()
        showMySelf()
        pausePlayer()
    }

    val taskHandler: Handler = Handler()

    private fun showMySelf() {

    }

    private fun goHome() {
        val intentHome = Intent(Intent.ACTION_MAIN)
        intentHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intentHome.addCategory(Intent.CATEGORY_HOME)
        context.startActivity(intentHome)
    }

    private fun pausePlayer() {
        val pauseIntent = Intent()
        pauseIntent.action = "com.android.music.musicservicecommand.pause"
        pauseIntent.putExtra("command", "pause")
        context.sendBroadcast(pauseIntent)
    }
}