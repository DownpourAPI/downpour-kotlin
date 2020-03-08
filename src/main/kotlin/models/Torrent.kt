package models

data class Torrent(
    val hash: String,
    val percentDone: Double,
    val name: String
)