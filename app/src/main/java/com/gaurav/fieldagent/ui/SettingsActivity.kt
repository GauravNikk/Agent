package com.gaurav.fieldagent.ui

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gaurav.fieldagent.R
import com.gaurav.fieldagent.data.datastore.SettingsDataStore
import com.gaurav.fieldagent.utils.NetworkUtils
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

    private lateinit var offlineOnlySwitch: SwitchMaterial
    private lateinit var autoRefreshSwitch: SwitchMaterial
    private lateinit var networkStatusValue: TextView
    private lateinit var lastRefreshValue: TextView
    private lateinit var refreshButton: Button

    private lateinit var settingsDataStore: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"

        offlineOnlySwitch = findViewById(R.id.offline_only_switch)
        autoRefreshSwitch = findViewById(R.id.auto_refresh_switch)
        networkStatusValue = findViewById(R.id.network_status_value)
        lastRefreshValue = findViewById(R.id.last_refresh_value)
//        refreshButton = findViewById(R.id.refresh_button)

        settingsDataStore = SettingsDataStore(this)

        lifecycleScope.launch {
            settingsDataStore.isOfflineOnly.collect { isOfflineOnly ->
                offlineOnlySwitch.isChecked = isOfflineOnly
            }
        }

        lifecycleScope.launch {
            settingsDataStore.isAutoRefresh.collect { isAutoRefresh ->
                autoRefreshSwitch.isChecked = isAutoRefresh
            }
        }

        lifecycleScope.launch {
            settingsDataStore.lastRefresh.collect { lastRefresh ->
                if (lastRefresh > 0) {
                    val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.getDefault())
                    lastRefreshValue.text = sdf.format(Date(lastRefresh))
                } else {
                    lastRefreshValue.text = "Never"
                }
            }
        }

        offlineOnlySwitch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                settingsDataStore.setOfflineOnly(isChecked)
            }
        }

        autoRefreshSwitch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                settingsDataStore.setAutoRefresh(isChecked)
            }
        }

//        refreshButton.setOnClickListener {
//            lifecycleScope.launch {
//                settingsDataStore.setLastRefresh(System.currentTimeMillis())
//            }
//        }

        val isOnline = NetworkUtils.isOnline(this)

        networkStatusValue.apply {
            text = if (isOnline) "Online" else "Offline"
            setTextColor(if (isOnline) Color.GREEN else Color.RED)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}