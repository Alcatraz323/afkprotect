package io.alcatraz.facesaver.extended

import android.R
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import io.alcatraz.facesaver.Constants
import io.alcatraz.facesaver.FaceSaverApplication
import io.alcatraz.facesaver.core.AccessibilityEventTaskManager
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
        setupTransition()
        registerReceivers()
    }

    override fun onResume() {
        super.onResume()
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

    }

    private fun registerReceivers() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constants.BROADCAST_ACTION_UPDATE_PREFERENCES)
        updatePreferenceReceiver = UpdatePreferenceReceiver()
        registerReceiver(updatePreferenceReceiver, intentFilter)
    }

    fun getAEventTaskMgr(): AccessibilityEventTaskManager {
        val application: FaceSaverApplication = application as FaceSaverApplication
        return application.aEventTaskMgr.get()
    }

    override fun onDestroy() {
        unregisterReceiver(updatePreferenceReceiver)
        super.onDestroy()
    }

    fun toast(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
    }

    fun toast(resId: Int) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
    }

    fun startTransition(intent: Intent?, vararg elements: View) {
        val pairs: Array<androidx.core.util.Pair<View, String>?> = arrayOfNulls<androidx.core.util.Pair<View, String>?>(elements.size)
        for (i in elements.indices) {
            val pair: androidx.core.util.Pair<View, String> =
                androidx.core.util.Pair<View, String>(elements[i], elements[i].transitionName)
            pairs[i] = pair
        }
        val optionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(this, *pairs)
        startActivity(intent, optionsCompat.toBundle())
    }

    open fun setupTransition() {
        val slideRight: Transition =
            TransitionInflater.from(this).inflateTransition(R.transition.slide_right)
        val slideLeft: Transition =
            TransitionInflater.from(this).inflateTransition(R.transition.slide_right)
        window.enterTransition = slideRight
        window.exitTransition = slideLeft
        window.returnTransition = slideRight
    }

    open fun setupExplodeTransition() {
        val explode: Transition =
            TransitionInflater.from(this).inflateTransition(R.transition.explode)
        window.enterTransition = explode
        window.exitTransition = explode
        window.returnTransition = explode
    }

    internal inner class UpdatePreferenceReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            loadPreference()
            onReloadPreferenceDone()
        }
    }
}
