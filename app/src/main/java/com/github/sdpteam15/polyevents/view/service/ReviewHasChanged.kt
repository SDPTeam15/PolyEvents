package com.github.sdpteam15.polyevents.view.service

/**
 * Callback for eventActivity when a comment is posted by the current user of when he modifies his comment
 */
fun interface ReviewHasChanged {
    /**
     * Notify review has changed
     */
    fun onLeaveReview()
}
