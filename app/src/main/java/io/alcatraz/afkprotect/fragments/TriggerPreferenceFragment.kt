package io.alcatraz.afkprotect.fragments

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import io.alcatraz.afkprotect.Constants
import io.alcatraz.afkprotect.R
import io.alcatraz.afkprotect.activities.AppPickActivity

class TriggerPreferenceFragment : PreferenceFragmentCompat() {
    private lateinit var ignorePackage: PreferenceScreen

    private fun bindListeners() {
        ignorePackage.onPreferenceClickListener = object : Preference.OnPreferenceClickListener{
            override fun onPreferenceClick(preference: Preference?): Boolean {
                val intent = Intent(context,AppPickActivity::class.java)
                intent.action = AppPickActivity.ACTION_PICK_IGNORE_PACKAGE
                startActivity(intent)
                return true
            }
        }
    }

    private fun findPreferences() {
        ignorePackage = findPreference(Constants.PREF_TRIGGER_IGNORE_PACKAGE)!!
    }

    private fun updateEditTextSummay() {

    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_trigger, rootKey)
        findPreferences()
        bindListeners()
        updateEditTextSummay()
    }
}