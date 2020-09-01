package com.safetymarcus.portfolio.video

import androidx.camera.core.VideoCapture
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.safetymarcus.portfolio.utils.DispatchScope
import com.safetymarcus.portfolio.video.VideoCaptureContract.Action.ShowToast
import kotlinx.coroutines.CoroutineScope
import java.io.File
import javax.inject.Inject

/**
 * @author Marcus Hooper
 */
class VideoCaptureController @Inject constructor(
    private val view: VideoCaptureContract.View,
    val store: VideoCaptureContractStore,
    scope: DispatchScope
) : LifecycleObserver, CoroutineScope by scope {

    private val videoCaptureListener = object : VideoCapture.OnVideoSavedListener {
        override fun onVideoSaved(file: File?) {
            store.performActions(ShowToast("Video saved to ${file?.absolutePath}"))
        }

        override fun onError(
            useCaseError: VideoCapture.UseCaseError?,
            message: String?,
            cause: Throwable?
        ) {
            store.performActions(ShowToast("Failed to save video because of $useCaseError"))
        }
    }

    private var recording: Boolean = false

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        view.recordListener = {
            recording = !recording
            if (recording) store.performActions(VideoCaptureContract.Action.StartRecording)
            else store.performActions(VideoCaptureContract.Action.StopRecording)
        }
        view.videoCapturedListener = videoCaptureListener
    }
}