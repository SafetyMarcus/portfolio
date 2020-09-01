package com.safetymarcus.portfolio

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.safetymarcus.portfolio.utils.IntPref
import com.safetymarcus.portfolio.utils.nightModeEnabled
import kotlinx.android.synthetic.main.settings.*

/**
 * @author Marcus Hooper
 */
class SettingsActivity : AppCompatActivity() {
    private var nightMode: Int by IntPref(
        PortfolioApplication.prefs,
        "theme",
        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    )

    override fun onCreate(savedInstanceState: Bundle?) {
//        AppCompatDelegate.setDefaultNightMode(nightMode)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        themeSwitch.isChecked = nightMode != AppCompatDelegate.MODE_NIGHT_NO && resources.nightModeEnabled
        themeSwitch.setOnCheckedChangeListener { _, checked ->
            nightMode = if (checked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(nightMode)
        }
    }
}