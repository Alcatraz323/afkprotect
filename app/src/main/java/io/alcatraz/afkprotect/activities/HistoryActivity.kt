package io.alcatraz.afkprotect.activities

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alcatraz.support.v4.appcompat.StatusBarUtil
import io.alcatraz.afkprotect.R
import io.alcatraz.afkprotect.adapters.HistoryAdapter
import io.alcatraz.afkprotect.core.Record
import io.alcatraz.afkprotect.extended.CompatWithPipeActivity
import kotlinx.android.synthetic.main.activity_history.*


class HistoryActivity : CompatWithPipeActivity() {
    private val history: MutableList<Record> = mutableListOf()
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        initialize()
    }

    private fun initialize() {
        adapter = HistoryAdapter(this, history)
        initViews()
        initData()
    }

    private fun initViews() {
        setSupportActionBar(history_toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        history_recycler.layoutManager = LinearLayoutManager(this)
        val controller: LayoutAnimationController =
            AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fall_down)
        history_recycler.layoutAnimation = controller
        history_recycler.adapter = adapter
        StatusBarUtil.setColor(this, Color.parseColor("#212121"),0)
    }

    private fun initData() {
        history.clear()
        val data = getAEventTaskMgr().records
        history.addAll(data)
        adapter.notifyDataSetChanged()
        history_recycler.scheduleLayoutAnimation()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}