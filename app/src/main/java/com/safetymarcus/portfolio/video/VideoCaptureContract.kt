package com.safetymarcus.portfolio.video

import androidx.camera.core.VideoCapture
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.safetymarcus.annotationprocessor.Actions
import com.safetymarcus.annotationprocessor.Contract
import com.safetymarcus.annotationprocessor.ViewState
import com.safetymarcus.portfolio.core.CoroutineActivity
import com.safetymarcus.portfolio.utils.DispatchScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import kotlinx.coroutines.CoroutineScope

/**
 * @author Marcus Hooper
 */
@Contract
interface VideoCaptureContract {
    interface View {
        var recordListener: () -> Unit
        var videoCapturedListener: VideoCapture.OnVideoSavedListener
    }

    @Module
    @InstallIn(ActivityComponent::class)
    object VideoCaptureModule {

        @Provides
        fun provideView(activity: FragmentActivity): View = activity as View

        @Provides
        fun provideStore(context: FragmentActivity): VideoCaptureContractStore =
            ViewModelProvider(context).get(VideoCaptureStore::class.java)

        @Provides
        fun provideScope(activity: FragmentActivity): DispatchScope = activity as CoroutineActivity
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