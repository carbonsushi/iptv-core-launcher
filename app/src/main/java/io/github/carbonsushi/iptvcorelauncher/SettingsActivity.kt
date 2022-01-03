package io.github.carbonsushi.iptvcorelauncher

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.mikepenz.aboutlibraries.LibsBuilder

class SettingsActivity : AppCompatActivity(R.layout.settings_activity) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val playlist = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("playlist_address", "")

            if (playlist == "") {
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.settings_container, SettingsFragment())
                    .commit()
            } else {
                val intent = Intent().apply {
                    setClassName(
                        "ru.iptvremote.android.iptv.core",
                        "ru.iptvremote.android.iptv.core.ChannelsActivity"
                    )
                    data = Uri.parse(playlist)
                }
                runCatching {
                    startActivity(intent)
                }.onFailure {
                    Toast.makeText(this, R.string.start_activity_error, Toast.LENGTH_SHORT).show()
                }
                finish()
            }
        }
    }

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
}