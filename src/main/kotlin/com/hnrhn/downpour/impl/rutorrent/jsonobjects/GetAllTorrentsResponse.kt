package com.hnrhn.downpour.impl.rutorrent.jsonobjects

import com.hnrhn.downpour.common.Torrent
import kotlinx.serialization.Serializable

@Serializable
data class GetAllTorrentsResponse(
    val t: HashMap<String, List<String>>,
    val cid: Long
) {
    fun toTorrents(): List<Torrent> {
        val torrents: MutableList<Torrent> = mutableListOf()
        for (entry in t) {
            torrents.add(
                Torrent(
                    entry.key,
                    entry.value[4],
                    entry.value[11].toLong(),
                    entry.value[12].toLong(),
                    entry.value[17].toInt(),
                    entry.value[15].toInt(),
                    entry.value[10].toDouble() / 1000,
                    entry.value[5].toLong(),
                    (entry.value[8].toDouble() / entry.value[5].toDouble()) * 100,
                    entry.value[34],
                    entry.value[21].toDouble(),
                    entry.value[8].toLong(),
                    entry.value[9].toLong(),
                    listOf(),   // TODO: Find a quick way to add Files
                    entry.value[25].replace("\\", "")
                )
            )
        }
        return torrents
    }
}