package io.alcatraz.afkprotect.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import io.alcatraz.afkprotect.Constants
import io.alcatraz.afkprotect.R
import io.alcatraz.afkprotect.core.Record
import io.alcatraz.afkprotect.extended.CompatWithPipeActivity
import kotlinx.android.synthetic.main.activity_record.*

class RecordActivity : CompatWithPipeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        initialize()
    }

    @SuppressLint("SetTextI18n")
    private fun initialize(){
        setSupportActionBar(record_toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        val data = intent.getParcelableExtra<Record>(KEY_RECORD_DATA)
        record_start.text = Record.convertTime(data.startTime)
        record_end.text = Record.convertTime(data.endTime)
        record_period.text = ((data.endTime-data.startTime)/1000/3600).toString()+"h"
        record_screen_times.text = data.screenOnTimes.toString()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object{
        const val KEY_RECORD_DATA = Constants.MY_PROJECT_CODE+"_key_record_data"
    }
}