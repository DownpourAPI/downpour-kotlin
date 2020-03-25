package com.hnrhn.downpour.impl.deluge.jsonobjects

import kotlinx.serialization.Serializable

@Serializable
data class Tracker(
    val send_stats: Boolean,
    val fails: Int,
    val verified: Boolean,
    val min_announce: Int?,
    val url: String,
    val fail_limit: Int,
    val next_announce: Int?,
    val complete_sent: Boolean,
    val source: Int,
    val start_sent: Boolean,
    val tier: Int,
    val updating: Boolean
)