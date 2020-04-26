package io.alcatraz.facesaver.core

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import io.alcatraz.facesaver.R
import io.alcatraz.facesaver.databinding.FloatToggleBinding
import io.alcatraz.facesaver.utils.Utils


class AccessibilityEventTaskManager(var context: Context) {
    private val profiles: MutableMap<String, ApplicationProfile> = mutableMapOf()
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val floatBinding: FloatToggleBinding = FloatToggleBinding.inflate(layoutInflater)

    private val layoutParams = WindowManager.LayoutParams()
    private val slide_in_animation = AnimationUtils.loadAnimation(context, R.anim.slide_right)
    private val slide_out_animation = AnimationUtils.loadAnimation(context, R.anim.slide_left)

    private val protectorTask: Runnable = Runnable {
        showWarningWindow()
    }

    private val warningWindowTask: Runnable = Runnable {
        goHome()
        removeWarningWindow()
        showMySelf()
        pausePlayer()
    }

    private val taskHandler: Handler = Handler()
    private val windowHandler: Handler = Handler()

    internal fun accessibilityUpdate(pack: String, activity: String, callback: TaskRegisterInterface) {
        if (profiles.containsKey(pack)) {
            val profile = profiles[pack] ?: return
            if (profile.useActIndividualCtl && profile.intendedActs.size > 0) {

            } else {
                taskHandler.postDelayed(protectorTask,profile.afkTime.toLong())
            }
        }
    }

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

    private fun showWarningWindow(){
        setUpLayoutParams()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(context)) {
                windowManager.addView(floatBinding.root, layoutParams)
                floatBinding.floatToggle.startAnimation(slide_in_animation)
            }
        } else {
            windowManager.addView(floatBinding.root, layoutParams)
            floatBinding.floatToggle.startAnimation(slide_in_animation)
        }
        windowHandler.postDelayed(warningWindowTask,8000/*Default may add to preference future*/)
    }

    private fun removeWarningWindow(){
        floatBinding.floatToggle.startAnimation(slide_out_animation)
    }

    private fun setUpLayoutParams(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        layoutParams.format = PixelFormat.RGBA_8888
        layoutParams.flags =
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.END or Gravity.TOP
        layoutParams.x = 0
        layoutParams.y = Utils.Dp2Px(context, 128f)

        slide_out_animation.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationRepeat(p0: Animation?) {}

            override fun onAnimationEnd(p0: Animation?) {
                windowManager.removeView(floatBinding.root)
            }

            override fun onAnimationStart(p0: Animation?) {}
        })
    }

    interface TaskRegisterInterface{
        fun onRegisterSuccessful(countDown: Int)
    }
}