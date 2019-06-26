package com.safetymarcus.portfolio

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.safetymarcus.portfolio.video.VideoCaptureActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        record.setOnClickListener { VideoCaptureActivity.startVideoCapture(this@MainActivity) }
    }
}
