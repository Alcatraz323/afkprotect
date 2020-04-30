package io.alcatraz.afkprotect.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import io.alcatraz.afkprotect.Constants
import io.alcatraz.afkprotect.R
import io.alcatraz.afkprotect.extended.CompatWithPipeActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : CompatWithPipeActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialize()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = MenuInflater(this)
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_main_about -> startActivity(
                Intent(
                    this@MainActivity,
                    AboutActivity::class.java
                )
            )
            R.id.menu_main_refresh -> initData()
            R.id.menu_main_log -> startActivity(Intent(this@MainActivity, LogActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.main_card_help -> startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(Constants.SUPPORT_URL)
                )
            )
            R.id.main_card_history -> startActivity(
                Intent(
                    this@MainActivity,
                    HistoryActivity::class.java
                )
            )
            R.id.main_card_profile_mgr, R.id.main_profile_mgr_modify -> {
                val transIntent = Intent(
                    this@MainActivity,
                    ProfileGeneralActivity::class.java
                )
                startTransition(
                    transIntent,
                    main_card_profile_mgr_image,
                    main_card_profile_mgr_indicator,
                    main_profile_mgr_modify
                )
            }
            R.id.main_card_setting -> startActivity(
                Intent(
                    this@MainActivity,
                    PreferenceActivity::class.java
                )
            )
        }
    }

    private fun initialize() {
        initData()
        initViews()
    }

    private fun initViews() {
        setSupportActionBar(main_toolbar)
        main_card_history.setOnClickListener(this)
        main_card_profile_mgr.setOnClickListener(this)
        main_profile_mgr_modify.setOnClickListener(this)
        main_card_setting.setOnClickListener(this)
        main_card_help.setOnClickListener(this)
    }

    private fun initData() {
        val manager = getAEventTaskMgr()
        main_card_status_indicator.text =
            String.format(getString(R.string.main_card_statistic_indicator), manager.records.size)
        main_card_profile_mgr_indicator.text =
            String.format(getString(R.string.main_card_profile_indicator), manager.profiles.size)
    }

    override fun onResume() {
        if (doneFirstInitialize) {
            initData()
        }
        super.onResume()
    }
}