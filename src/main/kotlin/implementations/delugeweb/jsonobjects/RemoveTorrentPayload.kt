package implementations.delugeweb.jsonobjects

import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable

@Serializable
data class RemoveTorrentPayload(
    val params: List<@ContextualSerialization Any>,
    val id: Int = 1,
    val method: String = "core.remove_torrent"

) {
    companion object Factory {
        fun defaultPayload(torrentHash: String, withData: Boolean): RemoveTorrentPayload {
            return RemoveTorrentPayload(listOf(torrentHash, withData))
        }
    }
}