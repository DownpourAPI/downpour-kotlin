package interfaces

import models.*

interface SeedboxController {
    fun login(password: String): String

    fun addMagnet(magnet: String): DownpourResult

    fun addTorrentFile(torrentFilePath: String): DownpourResult

    fun getTorrentDetails(torrentHash: String): DelugeTorrentInfo

    fun setMaxRatio(torrentHash: String, maxRatio: Int): DownpourResult

    fun getAllTorrents(): List<Torrent>

    fun removeTorrent(torrentHash: String, withData: Boolean = true): DownpourResult

    fun pauseTorrent(torrentHash: String): DownpourResult

    fun resumeTorrent(torrentHash: String): DownpourResult
}