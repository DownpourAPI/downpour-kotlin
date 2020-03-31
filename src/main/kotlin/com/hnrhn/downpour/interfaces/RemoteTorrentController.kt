package com.hnrhn.downpour.interfaces

import com.hnrhn.downpour.common.AddMagnetResult
import com.hnrhn.downpour.common.AddTorrentFileResult
import com.hnrhn.downpour.common.DownpourResult
import com.hnrhn.downpour.common.Torrent
import java.io.File

interface RemoteTorrentController {
    fun getTorrentDetails(torrentHash: String): Torrent?

    fun getAllTorrents(): List<Torrent>

    fun removeTorrent(torrentHash: String, withData: Boolean = true): DownpourResult

    fun pauseTorrent(torrentHash: String): DownpourResult

    fun resumeTorrent(torrentHash: String): DownpourResult

    fun addMagnet(magnetLink: String): AddMagnetResult

    fun addTorrentFile(torrentFile: File): AddTorrentFileResult

    fun setMaxDownloadSpeed(torrentHash: String, maxSpeedKibiBytes: Double): DownpourResult

    fun setMaxUploadSpeed(torrentHash: String, maxSpeedKibiBytes: Double): DownpourResult

    fun forceRecheck(torrentHash: String): DownpourResult

    fun getFreeSpace(): Long
}