package models

import kotlinx.serialization.Serializable

@Serializable
data class ResumeTorrentPayload(
    val params: List<List<String>>,
    val id: Int = 1,
    val method: String = "core.resume_torrent"
) {
    companion object {
        fun defaultPayload(torrentHash: String): ResumeTorrentPayload {
            val params = listOf(listOf(torrentHash))
            return ResumeTorrentPayload(params)
        }
    }
}