package com.hnrhn.downpour.impl.rutorrent

import com.github.kittinunf.fuel.Fuel
import com.hnrhn.downpour.common.*
import com.hnrhn.downpour.impl.rutorrent.jsonobjects.GetAllTorrentsResponse
import com.hnrhn.downpour.interfaces.RemoteTorrentController
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.File
import java.util.*

@UnstableDefault
class RutorrentSession(private var endpoint: String, user: String, password: String) : RemoteTorrentController {
    private var authHeader: String = Base64.getEncoder().encodeToString("$user:$password".toByteArray())

    private val json = Json(JsonConfiguration(ignoreUnknownKeys = true))

    override fun getTorrentDetails(torrentHash: String): Torrent? {
        TODO("Not yet implemented")
    }

    override fun setMaxRatio(torrentHash: String, maxRatio: Int): DownpourResult {
        TODO("Not yet implemented")
    }

    override fun getAllTorrents(): List<Torrent> {
        val result = Fuel.post(endpoint, listOf("mode" to "list", "cmd" to "d.connection_current="))
            .header("Authorization" to "Basic $authHeader")
            .response()
            .third

        val (responseBody, error) = result

        if (error != null) {
            throw error
        }

        return if (responseBody != null) {
            val response = json.parse(GetAllTorrentsResponse.serializer(), responseBody.toString(Charsets.UTF_8))
            response.toTorrents()
        } else {
            listOf()
        }
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

    override fun setMaxDownloadSpeed(torrentHash: String, maxSpeedKibiBytes: Double): DownpourResult {
        TODO("Not yet implemented")
    }

    override fun setMaxUploadSpeed(torrentHash: String, maxSpeedKibiBytes: Double): DownpourResult {
        TODO("Not yet implemented")
    }

    override fun forceRecheck(torrentHash: String): DownpourResult {
        TODO("Not yet implemented")
    }

    override fun getFreeSpace(): Long {
        TODO("Not yet implemented")
    }
}