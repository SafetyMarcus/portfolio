package com.safetymarcus.portfolio.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

/**
 * This is a wrapper around [kotlinx.coroutines.CoroutineScope] in order to ensure that calls to different dispatchers
 * are running on the correct threads within tests. All dispatchers should be called through from this class.
 * See: TestCoroutineScope for how this is done in tests.
 *
 * @author Marcus Hooper
 */
interface DispatchScope : CoroutineScope {
    val IO: CoroutineDispatcher
}