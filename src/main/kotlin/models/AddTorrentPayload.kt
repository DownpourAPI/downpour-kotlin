package models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class AddTorrentPayload(@Transient val magnet: String = "") {
    var id: Int = 1
    val method = "web.add_torrents"
    val params: List<List<MagnetUpload>> = listOf(listOf(MagnetUpload(magnet, MagnetOptions())))
}