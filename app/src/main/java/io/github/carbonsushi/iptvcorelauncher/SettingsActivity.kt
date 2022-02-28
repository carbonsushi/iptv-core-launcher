package io.github.carbonsushi.iptvcorelauncher

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mikepenz.aboutlibraries.LibsBuilder
import io.github.carbonsushi.iptvcorelauncher.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            findPreference<Preference>("about")?.setOnPreferenceClickListener {
                LibsBuilder().apply {
                    aboutShowIcon = true
                    aboutAppName = getString(R.string.app_name)
                    aboutShowVersionName = true
                    aboutDescription = getString(R.string.about_description)
                }.start(requireContext())
                true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.settings_container, SettingsFragment()).commit()
        }
    }
}