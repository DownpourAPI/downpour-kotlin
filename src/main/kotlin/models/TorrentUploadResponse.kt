package models

import kotlinx.serialization.Serializable

@Serializable
data class TorrentUploadResponse(
    val files: List<String>,
    val success: Boolean
)
