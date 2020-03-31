/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

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