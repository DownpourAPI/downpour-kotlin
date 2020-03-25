package com.hnrhn.downpour.impl.deluge.jsonobjects

import kotlinx.serialization.Serializable

@Serializable
data class FileInTorrent(
    val index: Int,
    val path: String,
    val offset: Long,
    val size: Long
)