package implementations.delugeweb.jsonobjects

import kotlinx.serialization.Serializable

@Serializable
data class PauseTorrentPayload(
    val id: Int = 1,
    val method: String = "core.pause_torrent",
    val params: List<List<String>>
) {
    companion object {
        fun defaultPayload(torrentHash: String): PauseTorrentPayload {
            val params = listOf(listOf(torrentHash))
            return PauseTorrentPayload(params = params)
        }
    }
}