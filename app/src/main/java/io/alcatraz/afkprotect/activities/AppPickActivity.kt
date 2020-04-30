package io.alcatraz.afkprotect.activities

import android.content.pm.PackageInfo
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.alcatraz.support.v4.appcompat.StatusBarUtil
import io.alcatraz.afkprotect.Constants
import io.alcatraz.afkprotect.R
import io.alcatraz.afkprotect.adapters.PackageAdapter
import io.alcatraz.afkprotect.extended.CompatWithPipeActivity
import io.alcatraz.afkprotect.utils.Utils
import kotlinx.android.synthetic.main.activity_profile_pick_app.*


class AppPickActivity : CompatWithPipeActivity() {
    private val apps: MutableList<PackageInfo> = mutableListOf()
    private lateinit var adapter: PackageAdapter
    private lateinit var progressBar: ProgressBar

    private var loadForIgnorePackPick = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_pick_app)
        initialize()
    }

    private fun initialize() {
        loadStarter()
        adapter = PackageAdapter(this, apps,loadForIgnorePackPick, getAEventTaskMgr())
        initViews()
        pick_app_toolbar.post {
            initData()
        }
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
            for ((index, i) in data.withIndex()) {
                if (i.packageName == Constants.MY_PACKAGE_NAME) {
                    data.removeAt(index)
                    break
                }
            }
            apps.addAll(data)
            runOnUiThread {
                adapter.notifyDataSetChanged()
                pick_app_recycler.scheduleLayoutAnimation()
                hideProcessing()
            }
        }).start()
    }

    private fun loadStarter() {
        if (intent.action == ACTION_PICK_IGNORE_PACKAGE) {
            loadForIgnorePackPick = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = MenuInflater(this)
        menuInflater.inflate(R.menu.activity_pick_app_menu, menu)

        val searchView = menu!!.findItem(R.id.action_search).actionView as SearchView
        searchView.queryHint = getString(R.string.profile_search_hint)
        val underline: View = searchView.findViewById(R.id.search_plate)
        underline.setBackgroundColor(Color.TRANSPARENT)

        progressBar = menu.findItem(R.id.action_progress_bar).actionView as ProgressBar
        val dp24: Int = Utils.dp2Px(this, 24f)
        val params = ViewGroup.LayoutParams(dp24, dp24)
        progressBar.layoutParams = params

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {
                if (Utils.isStringNotEmpty(s)) {
                    adapter.onTextChanged(s)
                } else {
                    adapter.onTextChanged("")
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

    companion object {
        const val ACTION_PICK_IGNORE_PACKAGE =
            Constants.MY_PROJECT_CODE + "_key_pick_ignore_package"
    }
}