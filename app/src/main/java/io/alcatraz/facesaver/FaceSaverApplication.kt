package io.alcatraz.facesaver

import android.app.Application
import android.content.Context
import io.alcatraz.facesaver.core.AccessibilityEventTaskManager
import java.util.concurrent.atomic.AtomicReference

class FaceSaverApplication : Application() {
    lateinit var overallContext: Context
    lateinit var aEventTaskMgr: AtomicReference<AccessibilityEventTaskManager>
    //TODO : Check string.xml/Setup versionCode/build.gradle when release update
    //TODO : Set Empty View for all adapter views
    override fun onCreate() {
        overallContext = applicationContext
        aEventTaskMgr = AtomicReference(AccessibilityEventTaskManager(applicationContext))
        super.onCreate()
    }
}
