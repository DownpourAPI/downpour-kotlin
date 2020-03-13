package interfaces

import implementations.delugeweb.jsonobjects.AddMagnetResult
import implementations.delugeweb.jsonobjects.AddTorrentFileResult
import implementations.delugeweb.jsonobjects.DownpourResult
import implementations.delugeweb.jsonobjects.Torrent
import java.io.File

interface SeedboxController {
    fun login(password: String): String

    fun getTorrentDetails(torrentHash: String): Torrent?

    fun setMaxRatio(torrentHash: String, maxRatio: Int): DownpourResult

    fun getAllTorrents(): List<Torrent>

    fun removeTorrent(torrentHash: String, withData: Boolean = true): DownpourResult

    fun pauseTorrent(torrentHash: String): DownpourResult

    fun resumeTorrent(torrentHash: String): DownpourResult

    fun addMagnet(magnetLink: String): AddMagnetResult

    fun addTorrentFile(torrentFile: File): AddTorrentFileResult
}