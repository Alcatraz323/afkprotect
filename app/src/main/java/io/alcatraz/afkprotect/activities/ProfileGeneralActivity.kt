package io.alcatraz.afkprotect.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alcatraz.support.v4.appcompat.StatusBarUtil
import io.alcatraz.afkprotect.R
import io.alcatraz.afkprotect.adapters.ProfileAdapter
import io.alcatraz.afkprotect.core.ApplicationProfile
import io.alcatraz.afkprotect.extended.CompatWithPipeActivity
import kotlinx.android.synthetic.main.activity_profile_general.*

class ProfileGeneralActivity : CompatWithPipeActivity() {
    private val profiles: MutableList<ApplicationProfile> = mutableListOf()
    private lateinit var adapter: ProfileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_general)
        initialize()
    }

    private fun initialize() {
        adapter = ProfileAdapter(this, profiles)
        initViews()
        initData()
    }

    private fun initViews() {
        setSupportActionBar(profile_general_toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        StatusBarUtil.setColor(this, Color.parseColor("#212121"), 0)
        profile_general_recycler.layoutManager = LinearLayoutManager(this)
        val controller: LayoutAnimationController =
            AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fall_down)
        profile_general_recycler.layoutAnimation = controller
        profile_general_recycler.adapter = adapter
        profile_general_add.setOnClickListener {
            val newIntent = Intent(this@ProfileGeneralActivity, ProfileConfigureActivity::class.java)
            newIntent.action = ProfileConfigureActivity.ACTION_NEW
            startActivity(newIntent)
        }
    }

    private fun initData() {
        profiles.clear()
        profile_general_counter.text = String.format(
            getString(R.string.main_card_profile_indicator),
            getAEventTaskMgr().profiles.size
        )
        val data = getAEventTaskMgr().profiles.values.toList()
        profiles.addAll(data)
        adapter.notifyDataSetChanged()
        profile_general_recycler.scheduleLayoutAnimation()
    }

    override fun onResume() {
        if (doneFirstInitialize) {
            initData()
        }
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = MenuInflater(this)
        menuInflater.inflate(R.menu.activity_profile_general_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finishAfterTransition()
            R.id.menu_profile_general_refresh -> initData()
        }
        return super.onOptionsItemSelected(item)
    }

}