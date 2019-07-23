package com.safetymarcus.portfolio.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * This class is designed to hold a view currentState and provide a way for lifecycles to easily observe changes to the [currentState].
 * Any modifications to [currentState] will push a change to any registered observers on the main thread. Removal of observers
 * will happen automatically through the lifecycle owner.
 *
 * This class is also a [ViewModel] but cannot be retrieved through [ViewModelProviders] as it requires an initial currentState
 * to initialise the live data with. Because of this, you should wrap this class in another class that specifies its'
 * data type and creates a [ViewModelProvider.NewInstanceFactory] class.
 *
 * @author Marcus Hooper
 */
open class ViewStateStore<T : Any>(initialState: T) : ViewModel() {
    private val data = MutableLiveData<T>().apply { value = initialState }
    private val job = SupervisorJob()
    private val ui = CoroutineScope(Dispatchers.Main + job)

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun observe(owner: LifecycleOwner, observer: (T) -> Unit) =
        data.observe(owner, Observer { observer(it) })

    var currentState: T
        get() = data.value!!
        set(value) {
            ui.launch { data.value = value }
        }
}