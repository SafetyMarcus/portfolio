package com.safetymarcus.portfolio

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.SHOW_AS_ACTION_ALWAYS
import androidx.appcompat.app.AppCompatActivity
import com.safetymarcus.portfolio.utils.IntentBuilder.Companion.startActivity
import com.safetymarcus.portfolio.utils.getDrawable
import com.safetymarcus.portfolio.video.VideoCaptureActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        record.setOnClickListener { VideoCaptureActivity.startVideoCapture(this@MainActivity) }
        settings.setOnClickListener { startActivity<SettingsActivity>(this) {} }
    }
}
