@file:Suppress("UsePropertyAccessSyntax")

package io.alcatraz.afkprotect.extended

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import io.alcatraz.afkprotect.R
import io.alcatraz.afkprotect.adapters.SetupPagerAdapter
import io.alcatraz.afkprotect.beans.SetupPage
import io.alcatraz.afkprotect.utils.AnimateUtils.textChange
import io.alcatraz.afkprotect.utils.SharedPreferenceUtil
import io.alcatraz.afkprotect.utils.Utils.getNavigationBarHeight
import io.alcatraz.afkprotect.utils.Utils.getTintedDrawable
import io.alcatraz.afkprotect.utils.Utils.setImageWithTint
import kotlinx.android.synthetic.main.activity_setup.*
import kotlinx.android.synthetic.main.activity_setup_nav.*
import java.util.*

abstract class SetupWizardBaseActivity : CompatWithPipeActivity() {
    private val setupPrefPrefix = "alc_setup_"
    private lateinit var adapter: SetupPagerAdapter

    //Data
    val pages: MutableList<SetupPage> = LinkedList()

    //Prefs
    private var version: Int = 0
    private var hasRunFullSetup = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_Setup)
        setContentView(R.layout.activity_setup)
        initViews()
        initPrefs()
        initPages()
    }

    abstract fun onSetupPageInit(pages: MutableList<SetupPage>)
    abstract fun onUpdate(pages: MutableList<SetupPage>)
    abstract fun onFinishSetup()
    abstract fun getVersionCode(): Int
    abstract fun specialUpdateShow(): Boolean

    @JvmOverloads
    fun setPageTitle(title: CharSequence, animate: Boolean = true) {
        if (animate) textChange(setup_title, title) else setup_title.text = title
    }

    fun removeAllPages() {
        pages.clear()
        adapter.notifyDataSetChanged()
    }

    fun addPage(page: SetupPage) {
        pages.add(page)
        adapter.notifyDataSetChanged()
    }

    fun removePage(any: Any) {
        if (any is SetupPage) pages.remove(any) else pages.removeAt(any as Int)
        adapter.notifyDataSetChanged()
    }

    fun setShowProgress(showProgress: Boolean) {
        if (showProgress) setup_progress.visibility =
            View.VISIBLE else setup_progress_bar_limit.visibility = View.GONE
    }

    fun restoreState() {
        setShowProgress(false)
        setup_btn_forward.isEnabled = true
        setImageWithTint(
            setup_btn_forward,
            R.drawable.ic_chevron_left_black_24dp,
            Color.DKGRAY
        )
        setup_btn_next.isEnabled = true
        setup_btn_next.setTextColor(Color.DKGRAY)
        setup_btn_next.setCompoundDrawablesRelative(
            null, null,
            getTintedDrawable(
                this,
                R.drawable.ic_chevron_right_black_24dp,
                Color.DKGRAY
            ), null
        )
    }

    fun banNextStep() {
        setup_btn_next.isEnabled = false
        setup_btn_next.setTextColor(Color.GRAY)
        setup_btn_next.setCompoundDrawablesRelative(
            null, null,
            getTintedDrawable(
                this,
                R.drawable.ic_chevron_right_black_24dp,
                Color.GRAY
            ), null
        )
    }

    fun banForwardStep() {
        setup_btn_forward.isEnabled = false
        setImageWithTint(
            setup_btn_forward,
            R.drawable.ic_chevron_left_black_24dp,
            Color.GRAY
        )
    }

    fun banPageSwitch() {
        setup_btn_next.isEnabled = false
        setup_btn_forward.isEnabled = false
        setup_btn_next.setTextColor(Color.GRAY)
        setup_btn_next.setCompoundDrawablesRelative(
            null, null,
            getTintedDrawable(
                this,
                R.drawable.ic_chevron_right_black_24dp,
                Color.GRAY
            ), null
        )
        setImageWithTint(
            setup_btn_forward,
            R.drawable.ic_chevron_left_black_24dp,
            Color.GRAY
        )
    }

    fun startPending() {
        setShowProgress(true)
        banPageSwitch()
    }

    fun endPending() {
        setShowProgress(false)
        restoreState()
    }

    private fun createPrefKey(action: String): String {
        return setupPrefPrefix + action
    }

    private fun initPrefs() {
        version =
            SharedPreferenceUtil.getPref(this, createPrefKey(PREF_ACTION_PREVIOUS_VERSIONCODE), getVersionCode()) as Int
        hasRunFullSetup = SharedPreferenceUtil.getPref(this, createPrefKey(PREF_ACTION_HAS_RUN_FULL_SETUP), false) as Boolean
    }

    private fun initViews() {
        setup_nav_bar!!.setPadding(
            setup_nav_bar!!.paddingLeft,
            setup_nav_bar!!.paddingTop,
            setup_nav_bar!!.paddingRight,
            setup_nav_bar!!.paddingBottom + getNavigationBarHeight(
                this
            )
        )

        setup_pager.setPadding(
            setup_pager.paddingLeft,
            setup_pager.paddingTop,
            setup_pager.paddingRight,
            setup_pager.paddingBottom + getNavigationBarHeight(
                this
            )
        )

        setup_pager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
            override fun onPageSelected(i: Int) {
                setPageTitle(pages[i].title)
                when (i) {
                    0 -> {
                        setup_btn_forward.visibility = View.GONE
                    }
                    pages.size - 1 -> {
                        setup_btn_next.setText(R.string.setup_step_next_final)
                    }
                    else -> {
                        setup_btn_next.visibility = View.VISIBLE
                        setup_btn_forward.visibility = View.VISIBLE
                        setup_btn_next.setText(R.string.setup_step_next)
                    }
                }
            }

            override fun onPageScrollStateChanged(i: Int) {}
        })

        setup_btn_forward.setOnClickListener {
            if (setup_pager.currentItem != 0) setup_pager!!.currentItem =
                setup_pager.currentItem - 1
        }

        setup_btn_next.setOnClickListener {
            if (setup_pager.currentItem != pages.size - 1) setup_pager.currentItem =
                setup_pager.currentItem + 1 else {
                if (!hasRunFullSetup) SharedPreferenceUtil.put(
                    this@SetupWizardBaseActivity,
                    createPrefKey(PREF_ACTION_HAS_RUN_FULL_SETUP),
                    true
                )
                SharedPreferenceUtil.put(
                    this@SetupWizardBaseActivity,
                    createPrefKey(PREF_ACTION_PREVIOUS_VERSIONCODE),
                    getVersionCode()
                )
                onFinishSetup()
            }
        }
    }

    private fun initPages() {
        pages.clear()
        if (!hasRunFullSetup) {
            onSetupPageInit(pages)
            onUpdate(pages)
        } else if (getVersionCode() > version || specialUpdateShow()) onUpdate(pages)
        if (pages.size == 0) {
            onFinishSetup()
            return
        }
        adapter = SetupPagerAdapter(pages, this)
        setup_pager.adapter = adapter
        setup_btn_forward.visibility = View.GONE
        setPageTitle(pages[0].title)
    }

    companion object {
        const val PREF_ACTION_HAS_RUN_FULL_SETUP = "has_run_full_setup"
        const val PREF_ACTION_PREVIOUS_VERSIONCODE = "previous_version_code"
    }
}