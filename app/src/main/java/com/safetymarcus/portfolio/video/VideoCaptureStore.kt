package com.safetymarcus.portfolio.video

import com.safetymarcus.portfolio.utils.ViewStateStore
import com.safetymarcus.portfolio.video.VideoCaptureContract.Action.*

/**
 * @author Marcus Hooper
 */
class VideoCaptureStore : VideoCaptureContractStore,
    ViewStateStore<VideoCaptureContractViewState>(VideoCaptureContractViewState()) {

    override fun performActions(vararg actions: VideoCaptureContract.Action) {
        actions.forEach {
            currentState = when (it) {
                is StartRecording -> currentState.copy(startRecording = true, stopRecording = false)
                is StopRecording -> currentState.copy(startRecording = false, stopRecording = true)
                is ShowToast -> currentState.copy(toast = it.message)
                is AddClip -> currentState
            }
        }

        //Reset state after updates have been posted. This should be updated to be handled by received listeners
        currentState = currentState.copy(stopRecording = false, startRecording = false, toast = null)
    }
}