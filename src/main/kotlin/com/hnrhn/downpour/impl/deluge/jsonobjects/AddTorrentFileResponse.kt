package com.hnrhn.downpour.impl.deluge.jsonobjects

import kotlinx.serialization.Serializable

@Serializable
data class AddTorrentFileResponse(
    val id: Int,
    val result: String?,
    val error: ResponseError?
)