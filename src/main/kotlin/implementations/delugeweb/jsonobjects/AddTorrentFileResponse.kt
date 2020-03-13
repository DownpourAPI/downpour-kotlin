package implementations.delugeweb.jsonobjects

import kotlinx.serialization.Serializable

@Serializable
data class AddTorrentFileResponse(
    val id: Int,
    val result: String?,
    val error: ResponseError?
)