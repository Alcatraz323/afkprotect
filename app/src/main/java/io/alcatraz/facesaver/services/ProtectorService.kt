package io.alcatraz.facesaver.services

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import io.alcatraz.facesaver.FaceSaverApplication
import io.alcatraz.facesaver.LogBuff
import io.alcatraz.facesaver.core.AccessibilityEventTaskManager

class ProtectorService : AccessibilityService() {
    override fun onInterrupt() {

    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
        when(p0?.eventType){
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                val currentEventPackage = p0.packageName?:return
                val currentActivity = p0.className?:return

            }
        }
    }

    private fun getAEventTaskMgr() : AccessibilityEventTaskManager{
        val application: FaceSaverApplication = application as FaceSaverApplication
        return application.aEventTaskMgr.get()
    }
}