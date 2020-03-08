package models

import kotlinx.serialization.Serializable

@Serializable
data class TorrentResponse(
    val hash: String,
    val torrentDetails: TorrentDetails
)
