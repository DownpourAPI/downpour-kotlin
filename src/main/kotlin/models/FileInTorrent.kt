package models

import kotlinx.serialization.Serializable

@Serializable
data class FileInTorrent(
    val index: Int,
    val path: String,
    val offset: Int,
    val size: Long
)