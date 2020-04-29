package io.alcatraz.facesaver.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import io.alcatraz.facesaver.R
import io.alcatraz.facesaver.beans.SetupPage
import io.alcatraz.facesaver.core.DeviceMgrApi
import io.alcatraz.facesaver.databinding.Setup2Binding
import io.alcatraz.facesaver.extended.SetupWizardBaseActivity
import io.alcatraz.facesaver.services.ProtectorService
import kotlinx.android.synthetic.main.activity_setup.*

class SetupActivity : SetupWizardBaseActivity() {
    private lateinit var permissionBinding: Setup2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        permissionBinding = Setup2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
    }

    override fun onSetupPageInit(pages: MutableList<SetupPage>) {
        val setupTitles: Array<String> =
            resources.getStringArray(R.array.setup_page_titles)
        val pageLayoutIds = intArrayOf(
            R.layout.setup_1
        )
        for (i in setupTitles.indices) {
            val page = SetupPage(setupTitles[i], pageLayoutIds[i])
            pages.add(page)
        }
        setup_pager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
            override fun onPageSelected(i: Int) {
                when (i) {
                    1 -> onSelectSetup2()
                    else -> restoreState()
                }
            }

            override fun onPageScrollStateChanged(i: Int) {}
        })
    }

    override fun onUpdate(pages: MutableList<SetupPage>) {
        val permissionPage = SetupPage(getString(R.string.setup_permission), R.layout.setup_2)
        permissionPage.rootView = permissionBinding.root
        pages.add(permissionPage)
        val updatePage =
            SetupPage(resources.getString(R.string.setup_current_update), R.layout.setup_3)
        pages.add(updatePage)
    }

    override fun onFinishSetup() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun getVersionCode(): Int {
        return 1
    }

    override fun specialUpdateShow(): Boolean {
        return !checkAndUpdatePermissionStatus()
    }

    override fun onResume() {
        super.onResume()
        checkAndUpdatePermissionStatus()
    }

    private fun onSelectSetup2() {
        banNextStep()
        checkAndUpdatePermissionStatus()
    }

    private fun checkAndUpdatePermissionStatus(): Boolean {
        var passAllCriticalPermission = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                permissionBinding.setup2FloatCheckIndicator.setImageResource(R.drawable.ic_check_green_24dp)
                permissionBinding.setup2FloatCheckState.text = getString(R.string.setup_granted)
                permissionBinding.setup2FloatCheckTitle.setTextColor(getColor(R.color.green_colorPrimary))
            } else {
                permissionBinding.setup2FloatCheckIndicator.setImageResource(R.drawable.ic_close_red_24dp)
                permissionBinding.setup2FloatCheckState.text = getString(R.string.setup_denied)
                permissionBinding.setup2FloatCheckTitle.setTextColor(getColor(R.color.pink_colorPrimary))
                passAllCriticalPermission = false
                permissionBinding.setup2FloatCheck.setOnClickListener {
                    startActivity(
                        Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:$packageName")
                        )
                    )
                }
            }
        } else {
            permissionBinding.setup2FloatCheckIndicator.setImageResource(R.drawable.ic_check_green_24dp)
            permissionBinding.setup2FloatCheckState.text = getString(R.string.setup_granted)
            permissionBinding.setup2FloatCheckTitle.setTextColor(resources.getColor(R.color.green_colorPrimary))
        }

        if (getAEventTaskMgr().isAccessibilityServiceOnline()) {
            permissionBinding.setup2AccessibilityCheckIndicator.setImageResource(R.drawable.ic_check_green_24dp)
            permissionBinding.setup2AccessibilityCheckState.text = getString(R.string.setup_granted)
            permissionBinding.setup2AccessibilityCheckTitle.setTextColor(resources.getColor(R.color.green_colorPrimary))
        } else {
            permissionBinding.setup2AccessibilityCheckIndicator.setImageResource(R.drawable.ic_close_red_24dp)
            permissionBinding.setup2AccessibilityCheckState.text = getString(R.string.setup_denied)
            permissionBinding.setup2AccessibilityCheckTitle.setTextColor(resources.getColor(R.color.pink_colorPrimary))
            passAllCriticalPermission = false
            permissionBinding.setup2AccessibilityCheck.setOnClickListener {
                ProtectorService.startAccessibilityAuthorize(this)
            }
        }

        if (getAEventTaskMgr().isDeviceAdminAuthorized()) {
            permissionBinding.setup2DeviceAdminCheckIndicator.setImageResource(R.drawable.ic_check_green_24dp)
            permissionBinding.setup2DeviceAdminCheckState.text = getString(R.string.setup_granted)
            permissionBinding.setup2DeviceAdminCheckTitle.setTextColor(resources.getColor(R.color.green_colorPrimary))
        } else {
            permissionBinding.setup2DeviceAdminCheckIndicator.setImageResource(R.drawable.ic_close_red_24dp)
            permissionBinding.setup2DeviceAdminCheckState.text = getString(R.string.setup_denied)
            permissionBinding.setup2DeviceAdminCheckTitle.setTextColor(resources.getColor(R.color.pink_colorPrimary))
            passAllCriticalPermission = false
            permissionBinding.setup2DeviceAdminCheck.setOnClickListener {
                DeviceMgrApi.startDeviceAdminAuth(this)
            }
        }

        if (passAllCriticalPermission) {
            restoreState()
        }

        return passAllCriticalPermission
    }
}