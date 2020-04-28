package io.alcatraz.facesaver.activities

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import com.alcatraz.support.v4.appcompat.StatusBarUtil
import io.alcatraz.facesaver.Constants
import io.alcatraz.facesaver.R
import io.alcatraz.facesaver.extended.CompatWithPipeActivity
import kotlinx.android.synthetic.main.activity_profile_configure.*

class ProfileConfigureActivity : CompatWithPipeActivity() {
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
        StatusBarUtil.setColor(this, Color.parseColor("#212121"), 0);
    }

    private fun initData(){
        
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val ACTION_PACK_PICK = Constants.MY_PROJECT_CODE + "_key_pack_pick_data"
        const val ACTION_LOAD_FORMER = Constants.MY_PROJECT_CODE + "_key_profile_load_former"
        const val ACTION_NEW = Constants.MY_PROJECT_CODE + "_key_profile_new"
    }
}