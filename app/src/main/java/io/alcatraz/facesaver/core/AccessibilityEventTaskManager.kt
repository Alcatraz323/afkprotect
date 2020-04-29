package io.alcatraz.facesaver.core

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import io.alcatraz.facesaver.Constants
import io.alcatraz.facesaver.LogBuff
import io.alcatraz.facesaver.R
import io.alcatraz.facesaver.activities.RecordActivity
import io.alcatraz.facesaver.databinding.FloatToggleBinding
import io.alcatraz.facesaver.utils.IOUtils
import io.alcatraz.facesaver.utils.Utils


class AccessibilityEventTaskManager(val context: Context) {
    private var accessibilityService: AccessibilityService? = null
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val layoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val deviceMgrApi = DeviceMgrApi(context)
    private var timer: CountDownTimer? = null
    private val screenReceiver = ScreenBroadcastReceiver()

    internal val profiles: MutableMap<String, ApplicationProfile> = mutableMapOf()
    internal val records: MutableList<Record> = mutableListOf()

    private val ignorePackage: MutableList<String> =
        mutableListOf(Constants.MY_PACKAGE_NAME)   //Default ignore myself or trigger self issue

    @SuppressLint("InflateParams")
    private val floatView: View = layoutInflater.inflate(R.layout.float_toggle, null)
    private val floatBinding: FloatToggleBinding = FloatToggleBinding.bind(floatView)
    private var hasShownWindow: Boolean = false
    private lateinit var currentProfile: ApplicationProfile

    private val layoutParams = WindowManager.LayoutParams()
    private val slideInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_right)
    private val slideOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_right_back)
    private val slideOutAnimationNoCallback =
        AnimationUtils.loadAnimation(context, R.anim.slide_right_back)

    private var record: Record? = null

    private val protectorTask: Runnable = Runnable {
        if (!hasShownWindow) {
            LogBuff.I("Afk limit reached for " + currentProfile.pack)
            showWarningWindow()
            hasShownWindow = true
        }
    }

    private val focusListener = AudioManager.OnAudioFocusChangeListener {
        if (it == AudioManager.AUDIOFOCUS_GAIN) {
            LogBuff.I("Got audio focus")
        } else if (it == AudioManager.AUDIOFOCUS_LOSS) {
            LogBuff.I("Losing audio focus")
        }
    }

    private var focusRequest: AudioFocusRequest? = null

    private val warningWindowTask: Runnable = Runnable {
        callRemoveWarningWindow(true)
    }

    private val taskHandler: Handler = Handler()
    private val windowHandler: Handler = Handler()

    init {
        try {
            val savedProfiles = Utils.json2Object(
                IOUtils.read(IOUtils.getProfilesPath(context), null),
                Profiles::class.java
            )
            if (savedProfiles != null) {
                profiles.putAll(savedProfiles.profileMap)
            }
            LogBuff.I("Loaded profiles, size = ${profiles.size}")
        } catch (e: Exception) {
            LogBuff.E(e.message ?: "")
        }

        try {
            val savedHistory = Utils.json2Object(
                IOUtils.read(IOUtils.getHistoryPath(context), null),
                History::class.java
            )
            if (savedHistory != null) {
                records.addAll(savedHistory.historyList)
            }
            LogBuff.I("Loaded history, size = ${records.size}")
        } catch (e: Exception) {
            LogBuff.E(e.message ?: "")
        }

        slideOutAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {}

            override fun onAnimationEnd(p0: Animation?) {
                LogBuff.I("doing lock function")
                removeWarningWindow()
                killPlayer()
                lockScreen()
            }

            override fun onAnimationStart(p0: Animation?) {}
        })

        slideOutAnimationNoCallback.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {}

            override fun onAnimationEnd(p0: Animation?) {
                LogBuff.I("user click cancel")
                removeWarningWindow()
            }

            override fun onAnimationStart(p0: Animation?) {}
        })

        floatBinding.floatToggle.setOnClickListener {
            clearOnInterrupt()
            taskHandler.postDelayed(protectorTask, currentProfile.afkTime.toLong())
        }
    }

    internal fun initService(accessibilityService: AccessibilityService?) {
        if (accessibilityService == null) {
            LogBuff.W("detaching accessibility service")
            this.accessibilityService?.unregisterReceiver(screenReceiver)
        } else {
            LogBuff.I("attached accessibility service, register receivers")
            registerReceiver()
        }
        this.accessibilityService = accessibilityService
    }

    internal fun accessibilityUpdate(pack: String, activity: String) {
        if (!Rectifier.isPackageIgnored(ignorePackage, pack)) {
            clearOnInterrupt()
            if (profiles.containsKey(pack)) {
                currentProfile = profiles[pack] ?: return
                if (currentProfile.useActIndividualCtl && currentProfile.intendedActs.size > 0) {

                } else {
                    LogBuff.I("refreshing task manager for [$pack]")
                    taskHandler.postDelayed(protectorTask, currentProfile.afkTime.toLong())
                }
            }
        }
    }

    internal fun clearOnInterrupt() {
        windowHandler.removeCallbacks(warningWindowTask)
        taskHandler.removeCallbacks(protectorTask)
        callRemoveWarningWindow(false)
    }

    internal fun addOrReplaceProfile(profile: ApplicationProfile) {
        if (profiles.containsKey(profile.pack)) {
            profiles.remove(profile.pack)
        }
        profiles[profile.pack] = profile
        saveProfiles()
        LogBuff.I("profile updated for ${profile.pack}")
    }

    internal fun removeProfile(pack: String) {
        if (profiles.containsKey(pack)) {
            profiles.remove(pack)
            LogBuff.I("removed profile for $pack")
        }
        saveProfiles()
    }

    internal fun clearProfile() {
        profiles.clear()
        saveProfiles()
        LogBuff.I("cleared profile")
    }

    internal fun addHistory(history: Record) {
        records.add(history)
        saveHistory()
        LogBuff.I("history added for ${history.startTime}")
    }

    internal fun removeHistory(startTime: Long) {
        for ((index, i) in records.withIndex()) {
            if (i.startTime == startTime) {
                records.removeAt(index)
                saveHistory()
                LogBuff.I("removed history for time $startTime")
                return
            }
        }
        LogBuff.W("history for time $startTime not found")
    }

    internal fun clearHistory() {
        records.clear()
        saveHistory()
        LogBuff.I("cleared history")
    }

    internal fun saveAll() {
        LogBuff.I("saving all")
        saveProfiles()
        saveHistory()
    }

    internal fun clearAll() {
        LogBuff.W("clear all action")
        clearProfile()
        clearHistory()
    }

    private fun saveProfiles() {
        val toSave = Profiles()
        toSave.profileMap = profiles
        IOUtils.write(IOUtils.getProfilesPath(context), Utils.obj2Json(toSave))
        LogBuff.I("saved profile")
    }

    private fun saveHistory() {
        val toSave = History()
        toSave.historyList = records
        IOUtils.write(IOUtils.getHistoryPath(context), Utils.obj2Json(toSave))
        LogBuff.I("saved history")
    }

    private fun showRecord() {
        LogBuff.I("calling record show/save")
        addHistory(record ?: Record(System.currentTimeMillis()))
        val intent = Intent(context, RecordActivity::class.java)
        intent.putExtra(RecordActivity.KEY_RECORD_DATA, record)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
        record = null
    }

    private fun lockScreen() {
        LogBuff.I("Locking screen")
        if (deviceMgrApi.lockScreen()) {
            LogBuff.I("lock successful, starting record")
            record = Record(System.currentTimeMillis())
            record?.packName = currentProfile.pack
        }
    }

    private fun killPlayer() {
        var rtime = 0
        LogBuff.I("performing GLOBAL_BACK for ${currentProfile.backClickTimes} times")
        while (rtime++ < currentProfile.backClickTimes)
            accessibilityService?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)

        if (currentProfile.doPauseMusicBroadcast) {
            val pauseIntent = Intent()
            pauseIntent.action = "com.android.music.musicservicecommand.pause"
            pauseIntent.putExtra("command", "pause")
            context.sendBroadcast(pauseIntent)
            LogBuff.I("sending generic music pause intent")
        }

        if (currentProfile.doRequestAudioFocus) {
            requestAudioFocus()
        }
    }

    private fun startTimerView() {
        timer?.cancel()
        floatBinding.floatCountdown.text =
            String.format(context.getString(R.string.float_countdown), 7)
        timer = object : CountDownTimer(7000, 1000) {
            override fun onFinish() {
                floatBinding.floatCountdown.text = context.getString(R.string.float_locking)
            }

            override fun onTick(p0: Long) {
                floatBinding.floatCountdown.text =
                    String.format(context.getString(R.string.float_countdown), p0 / 1000)
            }
        }
        timer?.start()
    }

    @Synchronized
    private fun showWarningWindow() {
        setUpLayoutParams()
        LogBuff.I("adding window")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(context)) {
                windowManager.addView(floatView, layoutParams)
                floatBinding.floatToggle.startAnimation(slideInAnimation)
            }
        } else {
            windowManager.addView(floatView, layoutParams)
            floatBinding.floatToggle.startAnimation(slideInAnimation)
        }
        startTimerView()
        windowHandler.postDelayed(
            warningWindowTask,
            8000    //may add to preference future
        )
    }

    @Synchronized
    private fun callRemoveWarningWindow(useFunctionCallback: Boolean) {
        if (hasShownWindow) {
            LogBuff.I("remove animation start")
            if (useFunctionCallback) {
                floatBinding.floatToggle.startAnimation(slideOutAnimation)
            } else {
                floatBinding.floatToggle.startAnimation(slideOutAnimationNoCallback)
            }
        }
    }

    @Synchronized
    private fun removeWarningWindow() {
        if (hasShownWindow) {
            LogBuff.I("removed window")
            windowManager.removeView(floatView)
            hasShownWindow = false
        }
    }

    private fun setUpLayoutParams() {
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
        layoutParams.y = Utils.dp2Px(context, 128f)
    }

    private fun requestAudioFocus() {
        val result: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setOnAudioFocusChangeListener(focusListener)
                .build()
            audioManager.requestAudioFocus(focusRequest!!)
        } else {
            audioManager.requestAudioFocus(
                focusListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            LogBuff.I("audio focus request granted")
        } else {
            LogBuff.I("audio focus request denied")
        }
    }

    private fun abandonAudioFocus() {
        LogBuff.I("abandon audio focus")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (focusRequest != null) {
                audioManager.abandonAudioFocusRequest(focusRequest!!)
            }
        } else {
            audioManager.abandonAudioFocus(focusListener)
        }
    }

    private fun registerReceiver() {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_USER_PRESENT)
        context.registerReceiver(screenReceiver, filter)
    }

    private inner class ScreenBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            val action = intent.action
            if (Intent.ACTION_SCREEN_ON == action) {
                LogBuff.I("screen receiver: received screen on")
                record?.addScreenOnT()
            } else if (Intent.ACTION_USER_PRESENT == action) {
                LogBuff.I("screen receiver: received unlock")
                if (record != null) {
                    record?.endTime = System.currentTimeMillis()
                    showRecord()
                    abandonAudioFocus()
                }
            }
        }
    }
}