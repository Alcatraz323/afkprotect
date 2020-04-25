package io.alcatraz.facesaver.extended

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import io.alcatraz.facesaver.Constants
import io.alcatraz.facesaver.utils.PermissionInterface
import io.alcatraz.facesaver.utils.SharedPreferenceUtil

@SuppressLint("Registered")
open class CompatWithPipeActivity : AppCompatActivity() {
    private var permissionInterface: PermissionInterface? = null
    private lateinit var updatePreferenceReceiver: UpdatePreferenceReceiver

    private var requestQueue = 0

    var doneFirstInitialize = false

    //=========PREFERENCES==============

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissionInterface != null && requestCode == requestQueue - 1) {
            permissionInterface!!.onResult(requestCode, permissions, grantResults)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadPreference()
        registerReceivers()
        doneFirstInitialize = true
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissionWithCallback(
        pi: PermissionInterface,
        requestCode: Int,
        vararg permissions: String
    ) {
        this.permissionInterface = pi
        requestPermissions(permissions, requestCode)
    }

    fun requestPermissionWithCallback(pi: PermissionInterface, vararg permissions: String) {
        requestPermissionWithCallback(pi, requestQueue, *permissions)
        requestQueue++
    }

    fun onReloadPreferenceDone() {}

    fun loadPreference() {
        val spf = SharedPreferenceUtil.instance

    }

    private fun registerReceivers() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constants.BROADCAST_ACTION_UPDATE_PREFERENCES)
        updatePreferenceReceiver = UpdatePreferenceReceiver()
        registerReceiver(updatePreferenceReceiver, intentFilter)
    }

    override fun onDestroy() {
        unregisterReceiver(updatePreferenceReceiver)
        super.onDestroy()
    }

    fun threadSleep() {
        try {
            Thread.sleep(500L)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    fun toast(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
    }

    fun toast(resId: Int) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
    }

    internal inner class UpdatePreferenceReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            loadPreference()
            onReloadPreferenceDone()
        }
    }
}
