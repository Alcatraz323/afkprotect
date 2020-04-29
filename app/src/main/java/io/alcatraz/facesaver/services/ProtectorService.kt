package io.alcatraz.facesaver.services

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent
import io.alcatraz.facesaver.Constants
import io.alcatraz.facesaver.FaceSaverApplication
import io.alcatraz.facesaver.LogBuff
import io.alcatraz.facesaver.core.AccessibilityEventTaskManager

class ProtectorService : AccessibilityService() {
    private var targetEventPackage: CharSequence = ""
    private var targetActivity: CharSequence = ""

    override fun onInterrupt() {
        getAEventTaskMgr().clearOnInterrupt()
        getAEventTaskMgr().initService(null)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        getAEventTaskMgr().initService(this)
        LogBuff.accessibilityStatus = true
    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
        when (p0?.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                val currentEventPackage = p0.packageName ?: return
                val currentActivity = p0.className ?: return
                if (targetEventPackage != currentEventPackage) {
                    targetEventPackage = currentEventPackage
                    targetActivity = currentActivity
                    doUpdate()
                } else {
                    if (targetActivity != currentActivity) {
                        targetActivity = currentActivity
                        doUpdate()
                    }
                }
            }
        }
    }

    private fun doUpdate(){
        getAEventTaskMgr().accessibilityUpdate(
            targetEventPackage.toString(),
            targetActivity.toString()
        )
    }

    private fun getAEventTaskMgr(): AccessibilityEventTaskManager {
        val application: FaceSaverApplication = application as FaceSaverApplication
        return application.aEventTaskMgr.get()
    }

    companion object{
        fun startAccessibilityAuthorize(context: Context){
            val accessibilityIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            context.startActivity(accessibilityIntent)
        }
    }
}