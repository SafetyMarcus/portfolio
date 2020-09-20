package com.safetymarcus.portfolio

import android.os.Bundle
import com.safetymarcus.portfolio.core.CoroutineActivity

class MainActivity : CoroutineActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
