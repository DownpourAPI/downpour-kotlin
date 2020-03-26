package com.hnrhn.downpour.interfaces

import com.hnrhn.downpour.impl.deluge.jsonobjects.*
import java.io.File

interface RemoteTorrentController {
    fun getTorrentDetails(torrentHash: String): Torrent?

    fun setMaxRatio(torrentHash: String, maxRatio: Int): DownpourResult

    fun getAllTorrents(): List<Torrent>

    fun removeTorrent(torrentHash: String, withData: Boolean = true): DownpourResult

    fun pauseTorrent(torrentHash: String): DownpourResult

    fun resumeTorrent(torrentHash: String): DownpourResult

    fun addMagnet(magnetLink: String): AddMagnetResult

    fun addTorrentFile(torrentFile: File): AddTorrentFileResult

    fun setMaxDownloadSpeed(torrentHash: String): DownpourResult

    fun setMaxUploadSpeed(torrentHash: String): DownpourResult

    fun forceRecheck(torrentHash: String): DownpourResult

    fun getFreeSpace(): Long
}