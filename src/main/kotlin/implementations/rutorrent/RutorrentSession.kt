package implementations.rutorrent

import implementations.delugeweb.jsonobjects.AddMagnetResult
import implementations.delugeweb.jsonobjects.AddTorrentFileResult
import implementations.delugeweb.jsonobjects.DownpourResult
import implementations.delugeweb.jsonobjects.Torrent
import interfaces.RemoteTorrentController
import java.io.File

class RutorrentSession : RemoteTorrentController {
    override fun login(password: String): String {
        TODO("Not yet implemented")
    }

    override fun getTorrentDetails(torrentHash: String): Torrent? {
        TODO("Not yet implemented")
    }

    override fun setMaxRatio(torrentHash: String, maxRatio: Int): DownpourResult {
        TODO("Not yet implemented")
    }

    override fun getAllTorrents(): List<Torrent> {
        TODO("Not yet implemented")
    }

    override fun removeTorrent(torrentHash: String, withData: Boolean): DownpourResult {
        TODO("Not yet implemented")
    }

    override fun pauseTorrent(torrentHash: String): DownpourResult {
        TODO("Not yet implemented")
    }

    override fun resumeTorrent(torrentHash: String): DownpourResult {
        TODO("Not yet implemented")
    }

    override fun addMagnet(magnetLink: String): AddMagnetResult {
        TODO("Not yet implemented")
    }

    override fun addTorrentFile(torrentFile: File): AddTorrentFileResult {
        TODO("Not yet implemented")
    }
}