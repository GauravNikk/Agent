package com.gaurav.fieldagent.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.gaurav.fieldagent.R
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var offlineOnlySwitch: SwitchMaterial
    private lateinit var autoRefreshSwitch: SwitchMaterial
    private lateinit var networkStatusValue: TextView
    private lateinit var lastRefreshValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"

        offlineOnlySwitch = findViewById(R.id.offline_only_switch)
        autoRefreshSwitch = findViewById(R.id.auto_refresh_switch)
        networkStatusValue = findViewById(R.id.network_status_value)
        lastRefreshValue = findViewById(R.id.last_refresh_value)

        // TODO: Implement logic for switches and display network/refresh status
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}