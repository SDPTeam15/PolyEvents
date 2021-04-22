package com.github.sdpteam15.polyevents.exceptions

import java.lang.Exception

/**
 * An exception thrown when the number participants to an event exceeds the maximum amount
 * of slots for that event.
 */
class MaxAttendeesException(message: String): Exception(message)