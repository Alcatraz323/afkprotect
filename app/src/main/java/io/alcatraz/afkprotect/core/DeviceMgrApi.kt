package io.alcatraz.afkprotect.core

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import io.alcatraz.afkprotect.extended.CompatWithPipeActivity
import io.alcatraz.afkprotect.receivers.LockScreenAdmin

class DeviceMgrApi(val context: Context) {
    private val deviceMgr =
        context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    private val componentName = ComponentName(context, LockScreenAdmin::class.java)
    fun isAdminActive(): Boolean {
        return deviceMgr.isAdminActive(componentName)
    }

    fun lockScreen() : Boolean{
        if (isAdminActive()) {
            deviceMgr.lockNow()
            return true
        }
        return false
    }

    companion object {
        fun startDeviceAdminAuth(activity: CompatWithPipeActivity) {
            val component = ComponentName(activity, LockScreenAdmin::class.java)
            val deviceMgrIntent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            deviceMgrIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, component)
            activity.startActivity(deviceMgrIntent)
        }
    }
}