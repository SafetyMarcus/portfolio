package com.safetymarcus.portfolio

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.*
import com.safetymarcus.portfolio.utils.IntPref
import com.safetymarcus.portfolio.utils.nightModeEnabled
import com.safetymarcus.portfolio.video.VideoCaptureActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var nightMode: Int by IntPref(PortfolioApplication.prefs, "theme", MODE_NIGHT_FOLLOW_SYSTEM)

    override fun onCreate(savedInstanceState: Bundle?) {
        setDefaultNightMode(nightMode)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        record.setOnClickListener { VideoCaptureActivity.startVideoCapture(this@MainActivity) }
        themeSwitch.isChecked = nightMode != MODE_NIGHT_NO && resources.nightModeEnabled
        themeSwitch.setOnCheckedChangeListener { _, checked ->
            nightMode = if (checked) MODE_NIGHT_YES else MODE_NIGHT_NO
            setDefaultNightMode(nightMode)
        }
    }
}
