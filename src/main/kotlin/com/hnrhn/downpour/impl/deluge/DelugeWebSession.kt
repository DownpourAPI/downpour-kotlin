package com.hnrhn.downpour.impl.deluge

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.hnrhn.downpour.common.AddMagnetResult
import com.hnrhn.downpour.common.AddTorrentFileResult
import com.hnrhn.downpour.common.DownpourResult
import com.hnrhn.downpour.common.Torrent
import com.hnrhn.downpour.impl.deluge.jsonobjects.*
import com.hnrhn.downpour.interfaces.RemoteTorrentController
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.File
import java.util.*

@UnstableDefault
class DelugeWebSession: RemoteTorrentController {
    private var cookie: String
    private val apiEndpoint: String

    private val json = Json(JsonConfiguration(ignoreUnknownKeys = true))


    // TODO: Remove this and separate tests properly.
    internal constructor(unitTestCookie: String) {
        this.cookie = unitTestCookie
        this.apiEndpoint = "http://unit-test"
    }

    constructor(apiEndpoint: String, password: String) {
        this.apiEndpoint = apiEndpoint
        this.cookie = login(password)
    }

    private fun login(password: String): String {
        val result = Fuel.post(apiEndpoint)
            .jsonBody("""{"id":1,"method":"auth.login","params":["$password"]}""")
            .response()

        val responseDetails = result.second

        val (responseBody, connectionError) = result.third

        when {
            responseBody != null -> {
                val loginResponse = json.parse(DelugeResponse.serializer(), responseBody.toString(Charsets.UTF_8))
                if (loginResponse.error != null) {
                    throw Error(loginResponse.error.message)
                }
                return responseDetails.headers["Set-Cookie"].first().split(";")[0]
            }
            connectionError != null -> {
                throw connectionError
            }
            else -> {
                throw Error("Something weird happened while logging in")
            }
        }
    }

    override fun addMagnet(magnetLink: String): AddMagnetResult {
        val response = Fuel.post(apiEndpoint)
            .jsonBody("""{"id":1,"method":"core.add_torrent_magnet","params":["$magnetLink",{}]}""")
            .header("Cookie", cookie)
            .response()
            .third

        val (data, error) = response

        if (error != null) {
            throw error
        }

        if (data != null) {
            val addMagnetResponse = json.parse(AddMagnetResponse.serializer(), data.toString(Charsets.UTF_8))
            if (addMagnetResponse.error == null) {
                if (addMagnetResponse.result == null) {
                    return AddMagnetResult.alreadyExists()
                }
                return AddMagnetResult.success(addMagnetResponse.result)
            }

            return AddMagnetResult.failure()
        }

        return AddMagnetResult.failure()
    }

    override fun addTorrentFile(torrentFile: File): AddTorrentFileResult {
        val fileDump: String = Base64.getEncoder().encodeToString(torrentFile.readBytes())

        val response = Fuel.post(apiEndpoint)
            .jsonBody("""{"id":1, "method":"core.add_torrent_file", "params":["file","$fileDump",{}]}""")
            .header("Cookie", cookie)
            .response()
            .third

        val (data, error) = response

        if (error != null) {
            throw error
        }

        if (data != null) {
            val addTorrentFileResponse = json.parse(AddTorrentFileResponse.serializer(), data.toString(Charsets.UTF_8))
            if (addTorrentFileResponse.error == null) {
                if (addTorrentFileResponse.result == null) {
                    return AddTorrentFileResult.alreadyExists()
                }
                return AddTorrentFileResult.success(addTorrentFileResponse.result)
            }

            return AddTorrentFileResult.failure()
        }

        return AddTorrentFileResult.failure()
    }

    override fun getTorrentDetails(torrentHash: String): Torrent? {
        val response = Fuel.post(apiEndpoint)
            .header("Cookie", cookie)
            .jsonBody("""{"id":1,"method":"core.get_torrent_status","params":["$torrentHash", []]}""")
            .response()

        val (data, error) = response.third

        return if (data != null) {
            val jsonResponse = data.toString(Charsets.UTF_8)
            val torrentDetailsResponse = json.parse(GetTorrentResponse.serializer(), jsonResponse)
            torrentDetailsResponse.result?.toTorrent()
        } else {
            throw error!!
        }
    }

    fun setMaxRatio(torrentHash: String, maxRatio: Int): DownpourResult {
        val response = Fuel.post(apiEndpoint)
            .header("Cookie", cookie)
            .jsonBody("""{"id":1,"method":"core.set_torrent_options","params":[["$torrentHash"],{"stop_at_ratio":true,"stop_ratio":$maxRatio}]}""")
            .response()
            .third

        val (data, error) = response

        if (error != null) {
            throw error
        }

        return if (data != null) {
            val setMaxRatioResponse = json.parse(DelugeResponse.serializer(), data.toString(Charsets.UTF_8))
            when (setMaxRatioResponse.result) {
                true -> DownpourResult.SUCCESS
                false -> DownpourResult.FAILURE
                null -> DownpourResult.FAILURE
            }
        } else {
            DownpourResult.FAILURE
        }
    }

    override fun getAllTorrents(): List<Torrent> {
        val response = Fuel.post(apiEndpoint)
            .header("Cookie", cookie)
            .jsonBody("""{"id": 1,"method":"core.get_torrents_status","params":[{}, []]}""")
            .response()

        val (data, error) = response.third

        val allTorrents = arrayListOf<DelugeTorrent>()
        if (data != null) {
            val jsonResponse = data.toString(Charsets.UTF_8)
            val torrentResponse: GetAllTorrentsResponse = json.parse(GetAllTorrentsResponse.serializer(), jsonResponse)

            if (torrentResponse.error != null) { throw Error(torrentResponse.error.message) }
            if (torrentResponse.result == null) { return listOf() }

            for (torrent in torrentResponse.result.values) {
                allTorrents.add(torrent)
            }
        } else {
            throw error!!
        }

        return allTorrents.map { delugeTorrent -> delugeTorrent.toTorrent() }
    }

    override fun removeTorrent(torrentHash: String, withData: Boolean): DownpourResult {
        val response = Fuel.post(apiEndpoint)
            .header("Cookie", cookie)
            .jsonBody("""{"id":1,"method":"core.remove_torrent","params":["$torrentHash",$withData]}""")
            .response()
            .third

        val (data, error) = response

        if (error != null) {
            throw error
        }

        return if (data != null) {
            val jsonResponse = data.toString(Charsets.UTF_8)
            val removeTorrentResult: DelugeResponse = json.parse(DelugeResponse.serializer(), jsonResponse)
            when (removeTorrentResult.result) {
                true -> DownpourResult.SUCCESS
                else -> DownpourResult.FAILURE
            }
        } else {
            DownpourResult.FAILURE
        }
    }

    override fun pauseTorrent(torrentHash: String): DownpourResult {
        val response = Fuel.post(apiEndpoint)
            .header("Cookie", this.cookie)
            .jsonBody("""{"id":1,"method":"core.pause_torrent","params":["$torrentHash"]}""")
            .response()
            .third

        val (data, error) = response

        if (error != null) {
            throw error
        }

        return if (data != null) {
            val jsonResponse = data.toString(Charsets.UTF_8)
            val pauseTorrentResult: DelugeResponse = json.parse(DelugeResponse.serializer(), jsonResponse)
            when (pauseTorrentResult.error) {
                null -> DownpourResult.SUCCESS
                else -> DownpourResult.FAILURE
            }
        } else {
            DownpourResult.FAILURE
        }
    }

    override fun resumeTorrent(torrentHash: String): DownpourResult {
        val response = Fuel.post(apiEndpoint)
            .header("Cookie", this.cookie)
            .jsonBody("""{"id":1,"method":"core.resume_torrent","params":["$torrentHash"]}""")
            .response()
            .third

        val (data, error) = response

        if (error != null) {
            throw error
        }

        return if (data != null) {
            val jsonResponse = data.toString(Charsets.UTF_8)
            val pauseTorrentResult: DelugeResponse = json.parse(DelugeResponse.serializer(), jsonResponse)
            when (pauseTorrentResult.error) {
                null -> DownpourResult.SUCCESS
                else -> DownpourResult.FAILURE
            }
        } else {
            DownpourResult.FAILURE
        }
    }

    override fun setMaxDownloadSpeed(torrentHash: String, maxSpeedKibiBytes: Double): DownpourResult {
        val response = Fuel.post(apiEndpoint)
            .header("Cookie", cookie)
            .jsonBody("""{"id":1,"method":"core.set_torrent_options","params":[["$torrentHash"],{"max_download_speed":$maxSpeedKibiBytes}]}""")
            .response()
            .third

        val (data, error) = response

        if (error != null) {
            throw error
        }

        return if (data != null) {
            val addMagnetResponse: DelugeResponse = json.parse(DelugeResponse.serializer(), data.toString(Charsets.UTF_8))
            when (addMagnetResponse.result) {
                true -> DownpourResult.SUCCESS
                false -> DownpourResult.FAILURE
                null -> DownpourResult.FAILURE
            }
        } else {
            DownpourResult.FAILURE
        }
    }

    override fun setMaxUploadSpeed(torrentHash: String, maxSpeedKibiBytes: Double): DownpourResult {
        val response = Fuel.post(apiEndpoint)
            .header("Cookie", cookie)
            .jsonBody("""{"id":1,"method":"core.set_torrent_options","params":[["$torrentHash"],{"max_upload_speed":$maxSpeedKibiBytes}]}""")
            .response()
            .third

        val (data, error) = response

        if (error != null) {
            throw error
        }

        return if (data != null) {
            val addMagnetResponse: DelugeResponse = json.parse(DelugeResponse.serializer(), data.toString(Charsets.UTF_8))
            when (addMagnetResponse.result) {
                true -> DownpourResult.SUCCESS
                false -> DownpourResult.FAILURE
                null -> DownpourResult.FAILURE
            }
        } else {
            DownpourResult.FAILURE
        }
    }

    override fun forceRecheck(torrentHash: String): DownpourResult {
        val response = Fuel.post(apiEndpoint)
            .header("Cookie", cookie)
            .jsonBody("""{"id":1,"method":"core.force_recheck","params":[["$torrentHash"]]}""")
            .response()
            .third

        val (data, error) = response

        if (error != null) {
            throw error
        }

        return if (data != null) {
            val forceRecheckResponse: DelugeResponse = json.parse(DelugeResponse.serializer(), data.toString(Charsets.UTF_8))
            if (forceRecheckResponse.result == null && forceRecheckResponse.error == null) {
                DownpourResult.SUCCESS
            } else {
                DownpourResult.FAILURE
            }
        } else {
            DownpourResult.FAILURE
        }
    }

    override fun getFreeSpace(): Long {
        val response = Fuel.post(apiEndpoint)
            .header("Cookie", this.cookie)
            .jsonBody("""{"id":1,"method":"core.get_free_space","params":[]}""")
            .response()
            .third

        val (data, error) = response

        if (error != null) {
            throw error
        }

        return if (data != null) {
            val jsonResponse = data.toString(Charsets.UTF_8)
            val getFreeSpaceResult = json.parse(GetFreeSpaceResponse.serializer(), jsonResponse)
            if (getFreeSpaceResult.error == null && getFreeSpaceResult.result != null) {
                getFreeSpaceResult.result
            } else {
                (-1).toLong()
            }
        } else {
            (-1).toLong()
        }
    }
}
