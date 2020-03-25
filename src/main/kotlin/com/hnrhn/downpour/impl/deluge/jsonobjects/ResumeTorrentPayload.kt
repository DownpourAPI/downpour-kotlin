package com.hnrhn.downpour.impl.deluge.jsonobjects

import kotlinx.serialization.Serializable

@Serializable
data class ResumeTorrentPayload(
    val id: Int = 1,
    val method: String = "core.resume_torrent",
    val params: List<List<String>>
) {
    companion object {
        fun defaultPayload(torrentHash: String): ResumeTorrentPayload {
            val params = listOf(listOf(torrentHash))
            return ResumeTorrentPayload(
                params = params
            )
        }
    }
}