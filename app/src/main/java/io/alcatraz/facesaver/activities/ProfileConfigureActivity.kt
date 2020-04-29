package io.alcatraz.facesaver.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.alcatraz.support.v4.appcompat.StatusBarUtil
import com.google.android.material.textfield.TextInputLayout
import io.alcatraz.facesaver.Constants
import io.alcatraz.facesaver.R
import io.alcatraz.facesaver.beans.ProfileParamCheckResult
import io.alcatraz.facesaver.core.ApplicationProfile
import io.alcatraz.facesaver.extended.CompatWithPipeActivity
import io.alcatraz.facesaver.utils.PackageCtlUtils
import kotlinx.android.synthetic.main.activity_profile_configure.*
import java.lang.Exception


class ProfileConfigureActivity : CompatWithPipeActivity(), View.OnClickListener {
    private var pickedPack: String? = null
    private var targetProfile: ApplicationProfile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_configure)
        initialize()
    }

    private fun initialize() {
        initViews()
        initData()
    }

    private fun initViews() {
        setSupportActionBar(configure_toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        StatusBarUtil.setColor(this, Color.parseColor("#212121"), 0)
        configure_pack_pick.setOnClickListener {
            val intent =
                Intent(this@ProfileConfigureActivity, AppPickActivity::class.java)
            intent.action = ACTION_PACK_PICK
            startActivityForResult(intent, 0)
        }
        configure_try_audiofocus.setOnClickListener(this)
        configure_try_broadcast_pause.setOnClickListener(this)
        configure_try_back_button.setOnClickListener(this)
    }

    private fun initData() {
        if (intent.action == ACTION_LOAD_FORMER) {
            pickedPack = intent.getStringExtra(ACTION_LOAD_FORMER)
            targetProfile = getAEventTaskMgr().profiles[pickedPack!!]
            packageReady()
            profileReady()
        }
    }

    private fun packageReady() {
        configure_pack_icon.setImageDrawable(PackageCtlUtils.getIcon(this, pickedPack ?: ""))
        configure_pack_name.text = pickedPack
        configure_pack_label.text = PackageCtlUtils.getLabel(this, pickedPack ?: "")
    }

    private fun profileReady() {
        val afkTime = targetProfile!!.afkTime
        val hour: Int = afkTime / 3600000
        val min: Int = (afkTime - hour * 3600000) / 60000
        val sec: Int = (afkTime - hour * 3600000 - min * 60000) / 1000
        configure_afk_hour.editText?.setText(hour.toString())
        configure_afk_min.editText?.setText(min.toString())
        configure_afk_sec.editText?.setText(sec.toString())
        configure_back_times.editText?.setText(targetProfile?.backClickTimes.toString())
        configure_try_broadcast_pause_switch.isChecked = targetProfile!!.doPauseMusicBroadcast
        configure_try_audiofocus_switch.isChecked = targetProfile!!.doRequestAudioFocus
        configure_try_back_button_switch.isChecked = targetProfile!!.backClickTimes > 0
    }

    private fun runParameterCheck(): ProfileParamCheckResult {
        val profileParamCheckResult = ProfileParamCheckResult(true, 0,0)
        val hourText = configure_afk_hour.editText?.text.toString()
        var hour = 0
        try {
            hour = Integer.parseInt(hourText)
            if (hour < 0) {
                setTextFieldError(configure_afk_hour)
                profileParamCheckResult.success = false
                return profileParamCheckResult
            }
        } catch (e: Exception) {
            setTextFieldError(configure_afk_hour)
            profileParamCheckResult.success = false
            return profileParamCheckResult
        }

        val minText = configure_afk_min.editText?.text.toString()
        var min = 0
        try {
            min = Integer.parseInt(minText)
            if (min < 0) {
                setTextFieldError(configure_afk_min)
                profileParamCheckResult.success = false
                return profileParamCheckResult
            }
        } catch (e: Exception) {
            setTextFieldError(configure_afk_min)
            profileParamCheckResult.success = false
            return profileParamCheckResult
        }

        val secText = configure_afk_sec.editText?.text.toString()
        var sec = 0
        try {
            sec = Integer.parseInt(secText)
            if (sec < 0) {
                setTextFieldError(configure_afk_sec)
                profileParamCheckResult.success = false
                return profileParamCheckResult
            }
        } catch (e: Exception) {
            setTextFieldError(configure_afk_sec)
            profileParamCheckResult.success = false
            return profileParamCheckResult
        }

        val backText = configure_back_times.editText?.text.toString()
        var backTimes = 0
        try {
            backTimes = Integer.parseInt(backText)
            if (backTimes < 0) {
                setTextFieldError(configure_back_times)
                profileParamCheckResult.success = false
                return profileParamCheckResult
            }
            profileParamCheckResult.backTimes = backTimes
        } catch (e: Exception) {
            setTextFieldError(configure_back_times)
            profileParamCheckResult.success = false
            return profileParamCheckResult
        }

        val timeToElapse = hour * 3600000 + min * 60000 + sec * 1000
        if (timeToElapse < 15000) {
            toast(R.string.profile_configure_too_low_second)
            profileParamCheckResult.success = false
            return profileParamCheckResult
        }
        profileParamCheckResult.timeToElapse = timeToElapse
        return profileParamCheckResult
    }

    private fun setTextFieldError(target: TextInputLayout) {
        target.isErrorEnabled = true
        target.error = getString(R.string.profile_configure_invalid_param)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            pickedPack = data?.getStringExtra(ACTION_PACK_PICK)
            packageReady()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = MenuInflater(this)
        inflater.inflate(R.menu.activity_profile_configure_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menu_configure_clear -> {
                if (pickedPack != null) {
                    getAEventTaskMgr().removeProfile(pickedPack!!)
                }
                finish()
            }
            R.id.menu_configure_confirm -> {
                if (pickedPack != null) {
                    val checkResult = runParameterCheck()
                    if (checkResult.success){
                        val newProfile = ApplicationProfile(pickedPack!!, checkResult.timeToElapse)
                        newProfile.doPauseMusicBroadcast = configure_try_broadcast_pause_switch.isChecked
                        newProfile.doRequestAudioFocus = configure_try_audiofocus_switch.isChecked
                        newProfile.backClickTimes = checkResult.backTimes
                        getAEventTaskMgr().addOrReplaceProfile(newProfile)
                        finish()
                    }
                } else {
                    toast(R.string.profile_configure_no_pack)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val ACTION_PACK_PICK = Constants.MY_PROJECT_CODE + "_key_pack_pick_data"
        const val ACTION_LOAD_FORMER = Constants.MY_PROJECT_CODE + "_key_profile_load_former"
        const val ACTION_NEW = Constants.MY_PROJECT_CODE + "_key_profile_new"
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.configure_try_audiofocus -> {
                configure_try_audiofocus_switch.isChecked =
                    !configure_try_audiofocus_switch.isChecked
            }
            R.id.configure_try_broadcast_pause -> {
                configure_try_broadcast_pause_switch.isChecked =
                    !configure_try_broadcast_pause_switch.isChecked
            }
            R.id.configure_try_back_button -> {
                if (configure_try_back_button_switch.isChecked) {
                    configure_back_times.editText?.setText("0")
                    configure_try_back_button_switch.isChecked = false
                    configure_back_times.isEnabled = false
                } else {
                    configure_back_times.editText?.setText("1")
                    configure_try_back_button_switch.isChecked = true
                    configure_back_times.isEnabled = true
                }
            }
        }
    }
}