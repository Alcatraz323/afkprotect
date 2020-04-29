package io.alcatraz.facesaver.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import io.alcatraz.facesaver.Constants
import io.alcatraz.facesaver.R

class TriggerPreferenceFragment : PreferenceFragmentCompat() {
    private lateinit var ignorePackage: PreferenceScreen

    private fun bindListeners() {

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