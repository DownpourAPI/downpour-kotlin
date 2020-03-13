package implementations.delugeweb.jsonobjects

import kotlinx.serialization.Serializable

@Serializable
data class GetAllTorrentsResponse(
    val id: Int,
    val result: HashMap<String, Torrent>?,
    val error: ResponseError?
)