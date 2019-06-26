package com.safetymarcus.portfolio.video

import android.Manifest
import android.content.Intent
import android.graphics.Matrix
import android.os.Bundle
import android.util.Rational
import android.util.Size
import android.view.Surface
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.fragment.app.FragmentActivity
import com.github.florent37.runtimepermission.RuntimePermission
import com.safetymarcus.portfolio.R
import com.safetymarcus.portfolio.utils.CoroutineActivity
import kotlinx.android.synthetic.main.video_capture_activity.*

/**
 * @author Marcus Hooper
 */
class VideoCaptureActivity : CoroutineActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_capture_activity)
        camera.post { startCamera() }
        camera.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ -> updateTransform() }
    }

    private fun startCamera() {
        // Create configuration object for the viewfinder use case
        val config = VideoCaptureConfig.Builder().apply {
            setTargetAspectRatio(Rational(1, 1))
            setTargetRotation(camera.display.rotation)
        }.build()

        val previewConfig = PreviewConfig.Builder().apply {
            setTargetAspectRatio(Rational(1, 1))
            setTargetResolution(Size(camera.width, camera.height))
        }.build()

        val preview = Preview(previewConfig)
        val capture = VideoCapture(config)

        // Every time the viewfinder is updated, recompute layout
        preview.onPreviewOutputUpdateListener = Preview.OnPreviewOutputUpdateListener {

            // To update the SurfaceTexture, we have to remove it and re-add it
            val parent = camera.parent as ViewGroup
            parent.removeView(camera)
            parent.addView(camera, 0)

            camera.surfaceTexture = it.surfaceTexture
            updateTransform()
        }

        CameraX.bindToLifecycle(this, preview, capture)
    }

    private fun updateTransform() {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = camera.width / 2f
        val centerY = camera.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when (camera.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Finally, apply transformations to our TextureView
        camera.setTransform(matrix)
    }

    companion object {
        const val REQUEST_CODE = 1111

        fun startVideoCapture(activity: FragmentActivity) {
            RuntimePermission.askPermission(
                activity,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).ask {
                if (it.isAccepted) activity.startActivityForResult(
                    Intent(activity, VideoCaptureActivity::class.java),
                    REQUEST_CODE
                )
            }
        }
    }
}