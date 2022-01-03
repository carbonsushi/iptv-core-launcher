package io.github.carbonsushi.iptvcorelauncher

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.posick.mdns.Lookup
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.xbill.DNS.ARecord
import org.xbill.DNS.DClass
import org.xbill.DNS.Type

class LaunchActivity : AppCompatActivity() {
    private fun startIntent(playlistUri: Uri) {
        val sendIntent = Intent().apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            setClassName(
                "ru.iptvremote.android.iptv.core",
                "ru.iptvremote.android.iptv.core.ChannelsActivity"
            )
            data = playlistUri
        }
        runCatching {
            startActivity(sendIntent)
        }.onFailure {
            Toast.makeText(this, R.string.start_activity_error, Toast.LENGTH_LONG).show()
        }
        finishAndRemoveTask()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val playlistUrl = sharedPreferences.getString("playlist_address", "")!!
        val playlistHttpUrl = playlistUrl.toHttpUrlOrNull()

        if (playlistHttpUrl == null) {
            Toast.makeText(this, R.string.set_up_error, Toast.LENGTH_LONG).show()
            startActivity(Intent(this, SettingsActivity::class.java))
            finish()
        } else {
            if (sharedPreferences.getBoolean("resolve_mdns", false)) {
                lifecycleScope.launch {
                    val records = withContext(Dispatchers.IO) {
                        Lookup(playlistHttpUrl.host, Type.A, DClass.IN).lookupRecords()
                    }
                    if (records.isEmpty()) {
                        startIntent(Uri.parse(playlistUrl))
                    } else {
                        startIntent(
                            Uri.parse(
                                playlistHttpUrl.newBuilder()
                                    .host((records[0] as ARecord).address.hostAddress!!).build()
                                    .toString()
                            )
                        )
                    }
                }
            } else {
                startIntent(Uri.parse(playlistUrl))
            }
        }
    }
}