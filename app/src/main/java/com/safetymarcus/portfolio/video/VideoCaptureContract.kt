package com.safetymarcus.portfolio.video

import androidx.camera.core.VideoCapture
import com.safetymarcus.annotationprocessor.Actions
import com.safetymarcus.annotationprocessor.Contract
import com.safetymarcus.annotationprocessor.ViewState

/**
 * @author Marcus Hooper
 */
@Contract
interface VideoCaptureContract {
    interface View {
        var recordListener: () -> Unit
        var videoCapturedListener: VideoCapture.OnVideoSavedListener
    }

    @ViewState
    data class State(
        val startRecording: Boolean,
        val stopRecording: Boolean,
        val clips: ArrayList<String>,
        val toast: String?
    )

    @Actions
    sealed class Action {
        object StartRecording : Action()
        object StopRecording : Action()
        data class ShowToast(val message: String) : Action()
        data class AddClip(val file: String) : Action()
    }
}