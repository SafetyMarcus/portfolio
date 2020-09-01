package com.safetymarcus.portfolio

import android.content.Intent
import android.os.Bundle
import com.safetymarcus.portfolio.core.CoroutineActivity

/**
 * @author Marcus Hooper
 */
class SplashActivity : CoroutineActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}