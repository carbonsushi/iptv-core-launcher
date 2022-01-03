package io.github.carbonsushi.iptvcorelauncher

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.preference.PreferenceManager

class LaunchActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sendIntent = Intent().apply {
            setClassName(
                "ru.iptvremote.android.iptv.core",
                "ru.iptvremote.android.iptv.core.ChannelsActivity"
            )
            data = Uri.parse(
                PreferenceManager.getDefaultSharedPreferences(this@LaunchActivity)
                    .getString("playlist_address", "")
            )
        }
        runCatching {
            startActivity(sendIntent)
        }.onFailure {
            Toast.makeText(this, R.string.start_activity_error, Toast.LENGTH_SHORT).show()
        }
        finish()
    }
}