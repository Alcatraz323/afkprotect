package io.alcatraz.facesaver.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem

import io.alcatraz.facesaver.LogBuff
import io.alcatraz.facesaver.R
import io.alcatraz.facesaver.extended.CompatWithPipeActivity
import kotlinx.android.synthetic.main.activity_log.*

class LogActivity : CompatWithPipeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        initViews()
        initData()
    }

    private fun initViews() {
        setSupportActionBar(log_toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    fun initData() {
        log_console_box.text = LogBuff.finalLog
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = MenuInflater(this)
        menuInflater.inflate(R.menu.activity_log_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menu_log_refresh -> initData()
        }
        return super.onOptionsItemSelected(item)
    }
}
