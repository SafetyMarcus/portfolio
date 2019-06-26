package com.safetymarcus.portfolio.utils

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * This is a generic activity class that all activities that are using coroutines should extend from.
 * It ties the lifecycle of all coroutines launched from within its scope to its own lifecycle, making sure that
 * they are cancelled whenever the activity is cleaned up. This avoids issues like memory leaks and callbacks being
 * triggered after an activity has closed
 *
 * @author Marcus Hooper
 */
abstract class CoroutineActivity : AppCompatActivity(), DispatchScope {
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
    }

    override fun onResume() {
        super.onResume()
        if (job.isCancelled) job = Job()
    }

    override fun onPause() {
        super.onPause()
        job.cancel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (job.isCancelled) job = Job()
        super.onActivityResult(requestCode, resultCode, data)
    }

    override val IO = Dispatchers.IO
}