package com.safetymarcus.portfolio.video

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Rational
import android.util.Size
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.camera.core.*
import androidx.fragment.app.FragmentActivity
import com.github.florent37.runtimepermission.RuntimePermission
import com.safetymarcus.portfolio.PortfolioApplication
import com.safetymarcus.portfolio.R
import com.safetymarcus.portfolio.core.CoroutineActivity
import com.safetymarcus.portfolio.utils.updateTransform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.video_capture_activity.*
import java.io.File
import java.util.*
import javax.inject.Inject

/**
 * @author Marcus Hooper
 */
@AndroidEntryPoint
class VideoCaptureActivity : CoroutineActivity(), VideoCaptureContract.View {

    private lateinit var preview: Preview
    private lateinit var capture: VideoCapture

    @Inject
    lateinit var controller: VideoCaptureController

    // Configuration object for the video capture
    private val config
        get() = VideoCaptureConfig.Builder().apply {
            setTargetAspectRatio(Rational(1, 1))
            setTargetRotation(camera.display.rotation)
        }.build()

    // Configuration object for the view finder preview
    private val previewConfig
        get() = PreviewConfig.Builder().apply {
            setTargetAspectRatio(Rational(1, 1))
            setTargetResolution(Size(camera.width, camera.height))
        }.build()

    override var recordListener: () -> Unit = {}
        set(value) {
            field = value
            record.setOnClickListener { value() }
        }

    override lateinit var videoCapturedListener: VideoCapture.OnVideoSavedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_capture_activity)
        File(
            "${
                PortfolioApplication.INSTANCE.externalMediaDirs.takeIf { it.isNotEmpty() }
                    ?.get(0)?.absolutePath
                    ?: ""
            }/portfolio/"
        ).mkdirs()

        controller.store.observe(this) {
            if (it.startRecording) {
                record.setImageResource(R.drawable.stop)
                capture.startRecording(videoLocation, videoCapturedListener)
            } else if (it.stopRecording) {
                record.setImageResource(R.drawable.record)
                capture.stopRecording()
            }
            showToast(it.toast)
        }
        camera.post { startCamera() }
        camera.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ -> camera.updateTransform() }
    }

    private fun showToast(toast: String?) {
        toast?.let {
            AlertDialog.Builder(this).setMessage(toast)
                .setPositiveButton(android.R.string.ok, null).show()
        }
    }

    private fun startCamera() {
        preview = Preview(previewConfig)
        capture = VideoCapture(config)

        // Every time the viewfinder is updated, recompute layout
        preview.onPreviewOutputUpdateListener = Preview.OnPreviewOutputUpdateListener {
            // To update the SurfaceTexture, we have to remove it and re-add it
            (camera.parent as ViewGroup).apply {
                removeView(camera)
                addView(camera, 0)
            }

            camera.setSurfaceTexture(it.surfaceTexture)
            camera.updateTransform()
        }

        CameraX.bindToLifecycle(this, preview, capture)
    }

    private val videoLocation
        get() = File("${
            PortfolioApplication.INSTANCE.externalMediaDirs.takeIf { it.isNotEmpty() }
                ?.get(0)?.absolutePath
                ?: ""
        }/portfolio/${UUID.randomUUID()}.mp4").also { it.createNewFile() }

    companion object {
        private const val LOCATION = "location"

        private val contract = object : ActivityResultContract<String, String>() {
            override fun createIntent(context: Context, input: String?) =
                Intent(context, VideoCaptureActivity::class.java)

            override fun parseResult(resultCode: Int, intent: Intent?) =
                intent?.getStringExtra(LOCATION)
        }

        fun startVideoCapture(
            activity: FragmentActivity,
            callback: ActivityResultCallback<String>
        ) {
            RuntimePermission.askPermission(
                activity,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).ask {
                if (it.isAccepted) activity.registerForActivityResult(contract, callback)
            }
        }
    }
}