package com.safetymarcus.portfolio.core

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.safetymarcus.portfolio.R
import com.safetymarcus.portfolio.utils.DispatchScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * This is a generic activity class that ties the lifecycle of all coroutines launched from
 * within its scope to its own lifecycle, making sure that they are cancelled whenever the activity
 * is cleaned up. This avoids issues like memory leaks and callbacks being triggered after
 * an activity has been finished or destroyed.
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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R)
            window.setDecorFitsSystemWindows(false)
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

    var error: AlertDialog? = null

    /**
     * Shows a generic alert dialog to the user that just displays the passed in message and has an option for them to
     * press "Ok". The [ok] callback will be triggered when the dialog is dismissed for any reason, including the user
     * pressing "Ok" or pressing outside of the alert dialog. Use this if need to show errors when the
     * user is logged out.
     *
     * @param message    The text to be displayed within the alert dialog
     * @param ok         Callback to be triggered when the user presses ok in the dialog
     */
    fun showAlert(message: String, ok: (() -> Unit)? = null): Unit = run {
        if (error == null) {
            error = AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setOnDismissListener {
                    error = null
                    ok?.invoke()
                }
                .create()
            error?.show()
        }
    }
}