package com.safetymarcus.portfolio

import com.safetymarcus.portfolio.utils.DispatchScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.coroutines.CoroutineContext

/**
 * This is here purely for test purposes when mocking out implementations on an object that extends its parent's scope.
 * All coroutine's running from this scope will run on a blocking thread, as well as all calls to overridden dispatchers.
 *
 * @author Marcus Hooper
 */
class TestCoroutineScope : DispatchScope {
    @ExperimentalCoroutinesApi
    /**
     * Overrides the source coroutineContext to launch all threads from the Unconfined (blocking) thread in tests
     */
    override val coroutineContext: CoroutineContext = Dispatchers.Unconfined

    @ExperimentalCoroutinesApi
    /**
     * Overrides the IO dispatcher to make sure business logic is running on the Unconfined (blocking) thread in tests
     */
    override val IO = Dispatchers.Unconfined
}