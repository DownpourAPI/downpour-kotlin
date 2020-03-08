package models

import kotlinx.serialization.Serializable

@Serializable
data class PauseTorrentPayload(
    val params: List<List<String>>,
    val id: Int = 1,
    val method: String = "core.pause_torrent"
) {
    companion object {
        fun defaultPayload(torrentHash: String): PauseTorrentPayload {
            val params = listOf(listOf(torrentHash))
            return PauseTorrentPayload(params)
        }
    }
}