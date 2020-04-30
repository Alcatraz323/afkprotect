package io.alcatraz.afkprotect.activities

import android.os.Bundle
import android.view.MenuItem
import io.alcatraz.afkprotect.R
import io.alcatraz.afkprotect.beans.PreferenceHeader
import io.alcatraz.afkprotect.extended.CompatWithPipeActivity
import io.alcatraz.afkprotect.fragments.TriggerPreferenceFragment
import kotlinx.android.synthetic.main.activity_preference_inner.*


class PreferenceInnerActivity : CompatWithPipeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference_inner)

        val intent = intent
        val header = intent.getParcelableExtra<PreferenceHeader>(PREFERENCE_TRANSFER_HEADER)
        preference_act_toolbar.title = header.title
        setSupportActionBar(preference_act_toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        when (header.icon_res) {
            R.drawable.ic_settings_black_24dp->{
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.preference_act_fragment_container, TriggerPreferenceFragment())
                    .commit()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val PREFERENCE_TRANSFER_HEADER = "PREFERENCE_HEADER"
    }
}
