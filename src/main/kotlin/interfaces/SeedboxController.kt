package interfaces

import models.*
import java.io.File

interface SeedboxController {
    fun login(password: String): String

    fun addTorrent(magnetLinkOrRemotePath: String): DownpourResult

    fun uploadTorrentFile(torrentFile: File): String?

    fun getTorrentDetails(torrentHash: String): Torrent?

    fun setMaxRatio(torrentHash: String, maxRatio: Int): DownpourResult

    fun getAllTorrents(): List<Torrent>

    fun removeTorrent(torrentHash: String, withData: Boolean = true): DownpourResult

    fun pauseTorrent(torrentHash: String): DownpourResult

    fun resumeTorrent(torrentHash: String): DownpourResult
}