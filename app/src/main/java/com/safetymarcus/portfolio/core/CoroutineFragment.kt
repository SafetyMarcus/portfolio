package com.safetymarcus.portfolio.core

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.safetymarcus.portfolio.utils.DispatchScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * This is a generic fragment class that ties the lifecycle of all coroutines launched from within
 * its scope to its own lifecycle, making sure that they are cancelled whenever the fragment is
 * cleaned up. This avoids issues like memory leaks and callbacks being triggered after
 * a fragment has been finished.
 *
 * @author Marcus Hooper
 */
abstract class CoroutineFragment : Fragment(), DispatchScope {
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

    override val IO = Dispatchers.IO
}