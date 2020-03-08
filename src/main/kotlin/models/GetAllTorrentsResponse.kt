package models

import kotlinx.serialization.Serializable

@Serializable
data class GetAllTorrentsResponse(
    val id: Int,
    val result: TorrentResult?,
    val error: ResponseError?
)