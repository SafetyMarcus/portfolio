package com.safetymarcus.portfolio

import android.os.Bundle
import android.view.View
import android.view.View.*
import android.view.WindowId
import android.view.WindowInsets
import androidx.core.app.ActivityOptionsCompat
import com.safetymarcus.portfolio.core.CoroutineActivity
import com.safetymarcus.portfolio.utils.IntentBuilder.Companion.startActivity
import com.safetymarcus.portfolio.utils.convertToPixels
import com.safetymarcus.portfolio.video.VideoCaptureActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : CoroutineActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        record.setOnClickListener { VideoCaptureActivity.startVideoCapture(this@MainActivity) {} }
        settings.setOnClickListener { startActivity<SettingsActivity>(this) {} }
    }
}
