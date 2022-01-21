package com.example.trackingapp.models

import java.util.*

class Event(
    val eventName : EventName,
    val timestamp: Long,
    val event: String?,
    val description: String? = null,
    val name: String? = null,
    val packageName: String? = null,
    var timezoneOffset: Int? = null
) {
    init {
        timezoneOffset = calculateTimezoneOffset(timestamp)
    }

    private fun calculateTimezoneOffset(timestamp: Long): Int {
        return TimeZone.getDefault().getOffset(timestamp)
    }
}