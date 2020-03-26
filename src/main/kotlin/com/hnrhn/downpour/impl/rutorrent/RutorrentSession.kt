package com.hnrhn.downpour.impl.rutorrent

import com.hnrhn.downpour.impl.deluge.jsonobjects.*
import com.hnrhn.downpour.interfaces.RemoteTorrentController
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

    override fun addTorrentFile(torrentFile: File): AddTorrentFileStatus {
        TODO("Not yet implemented")
    }
}