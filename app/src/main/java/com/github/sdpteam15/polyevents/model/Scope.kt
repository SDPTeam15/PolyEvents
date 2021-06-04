package com.github.sdpteam15.polyevents.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/** Defines a scope for new coroutines. Every **coroutine builder**
 *  is an extension on Scope and inherits its Context
 *  to automatically propagate all its elements and cancellation.
 */
interface Scope {
    fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    )
}