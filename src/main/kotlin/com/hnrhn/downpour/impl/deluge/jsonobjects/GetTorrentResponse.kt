package com.hnrhn.downpour.impl.deluge.jsonobjects

import kotlinx.serialization.Serializable

@Serializable
data class GetTorrentResponse(
    val id: Int,
    val result: DelugeTorrent?,
    val error: ResponseError?
)