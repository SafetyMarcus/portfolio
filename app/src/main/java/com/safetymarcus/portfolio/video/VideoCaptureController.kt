package com.safetymarcus.portfolio.video

import androidx.camera.core.VideoCapture
import com.safetymarcus.portfolio.utils.DispatchScope
import com.safetymarcus.portfolio.video.VideoCaptureContract.Action.ShowToast
import kotlinx.coroutines.CoroutineScope
import java.io.File

/**
 * @author Marcus Hooper
 */
class VideoCaptureController(
    view: VideoCaptureContract.View,
    val store: VideoCaptureContractStore,
    scope: DispatchScope
) : CoroutineScope by scope {

    private val videoCaptureListener = object : VideoCapture.OnVideoSavedListener {
        override fun onVideoSaved(file: File?) {
            store.performActions(ShowToast("Video saved to ${file?.absolutePath}"))
        }

        override fun onError(useCaseError: VideoCapture.UseCaseError?, message: String?, cause: Throwable?) {
            store.performActions(ShowToast("Failed to save video because of $useCaseError"))
        }
    }

    private var recording: Boolean = false

    init {
        view.recordListener = {
            recording = !recording
            if (recording) store.performActions(VideoCaptureContract.Action.StartRecording)
            else store.performActions(VideoCaptureContract.Action.StopRecording)
        }
        view.videoCapturedListener = videoCaptureListener
    }
}