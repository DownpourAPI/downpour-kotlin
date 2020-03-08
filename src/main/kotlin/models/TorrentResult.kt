package models

import kotlinx.serialization.Serializable

@Serializable
data class TorrentResult(
    val stats: DelugeStats,
    val connected: Boolean,
    val torrents: HashMap<String, DelugeTorrentInfo>
    // val filters: HashMap<String, DelugeFilter>
)