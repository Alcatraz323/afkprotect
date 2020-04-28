package io.alcatraz.facesaver.activities

import android.content.pm.PackageInfo
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.alcatraz.support.v4.appcompat.StatusBarUtil
import io.alcatraz.facesaver.R
import io.alcatraz.facesaver.adapters.PackageAdapter
import io.alcatraz.facesaver.extended.CompatWithPipeActivity
import io.alcatraz.facesaver.utils.Utils
import kotlinx.android.synthetic.main.activity_profile_pick_app.*


class AppPickActivity : CompatWithPipeActivity() {
    private val apps: MutableList<PackageInfo> = mutableListOf()
    private lateinit var adapter: PackageAdapter
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_pick_app)
        initialize()
    }

    private fun initialize() {
        adapter = PackageAdapter(this, apps)
        initViews()
        initData()
    }

    private fun initViews() {
        setSupportActionBar(pick_app_toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        pick_app_recycler.layoutManager = LinearLayoutManager(this)
        val controller: LayoutAnimationController =
            AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fall_down)
        pick_app_recycler.layoutAnimation = controller
        pick_app_recycler.adapter = adapter
        StatusBarUtil.setColor(this, Color.parseColor("#212121"), 0)
    }

    private fun initData() {
        showProcessing()
        apps.clear()
        Thread(Runnable {
            val data = packageManager.getInstalledPackages(0)
            apps.addAll(data)
            runOnUiThread {
                adapter.notifyDataSetChanged()
                pick_app_recycler.scheduleLayoutAnimation()
                hideProcessing()
            }
        }).start()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = MenuInflater(this)
        menuInflater.inflate(R.menu.activity_preset_general_menu, menu)

        val searchView = menu!!.findItem(R.id.action_search).actionView as SearchView
        searchView.queryHint = getString(R.string.profile_search_hint)
        val underline: View = searchView.findViewById(R.id.search_plate)
        underline.setBackgroundColor(Color.TRANSPARENT)

        progressBar = menu.findItem(R.id.action_progress_bar).actionView as ProgressBar
        val dp24: Int = Utils.Dp2Px(this, 24f)
        val params = ViewGroup.LayoutParams(dp24, dp24)
        progressBar.layoutParams = params

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {
                if (TextUtils.isEmpty(s)) {
                    adapter.onTextChanged("")
                } else {
                    adapter.onTextChanged(s)
                }
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showProcessing() {
        progressBar.post { progressBar.visibility = View.VISIBLE }
    }

    private fun hideProcessing() {
        progressBar.post { progressBar.visibility = View.GONE }
    }
}