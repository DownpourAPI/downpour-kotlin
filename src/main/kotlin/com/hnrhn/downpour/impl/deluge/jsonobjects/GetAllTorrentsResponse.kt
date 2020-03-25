package com.hnrhn.downpour.impl.deluge.jsonobjects

import kotlinx.serialization.Serializable

@Serializable
data class GetAllTorrentsResponse(
    val id: Int,
    val result: HashMap<String, Torrent>?,
    val error: ResponseError?
)