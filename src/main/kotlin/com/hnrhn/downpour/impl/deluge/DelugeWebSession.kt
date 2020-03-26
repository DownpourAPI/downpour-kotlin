package com.hnrhn.downpour.impl.deluge

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.hnrhn.downpour.impl.deluge.jsonobjects.*
import com.hnrhn.downpour.interfaces.RemoteTorrentController
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.File

@UnstableDefault
class DelugeWebSession: RemoteTorrentController {
    private var cookie: String
    private val apiEndpoint: String

    private val json = Json(JsonConfiguration(strictMode = false))


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
        val payload = LoginPayload(password)
        val result = Fuel.post(apiEndpoint)
            .jsonBody(payload.toString())
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
        val payload = AddMagnetPayload(magnetLink)

        val response = Fuel.post(apiEndpoint)
            .jsonBody(payload.toString())
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
                    return AddMagnetResult.AlreadyExists()
                }
                return AddMagnetResult.Success(addMagnetResponse.result)
            }

            return AddMagnetResult.Failure()
        }

        return AddMagnetResult.Failure()
    }

    override fun addTorrentFile(torrentFile: File): AddTorrentFileResult {
        val payload = AddTorrentFilePayload(torrentFile)

        val response = Fuel.post(apiEndpoint)
            .jsonBody(payload.toString())
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
                    return AddTorrentFileResult.AlreadyExists()
                }
                return AddTorrentFileResult.Success(addTorrentFileResponse.result)
            }

            return AddTorrentFileResult.Failure()
        }

        return AddTorrentFileResult.Failure()
    }

    override fun getTorrentDetails(torrentHash: String): Torrent? {
        val singleTorrentPayload = GetTorrentDetailsPayload(torrentHash)
        val response = Fuel.post(apiEndpoint)
            .header("Cookie", cookie)
            .jsonBody(singleTorrentPayload.toString())
            .response()

        val (data, error) = response.third

        return if (data != null) {
            val jsonResponse = data.toString(Charsets.UTF_8)
            val torrentDetailsResponse = json.parse(GetTorrentResponse.serializer(), jsonResponse)
            torrentDetailsResponse.result
        } else {
            throw error!!
        }
    }

    override fun setMaxRatio(torrentHash: String, maxRatio: Int): DownpourResult {
        val maxRatioPayload = MaxRatioPayload(torrentHash, maxRatio)

        val response = Fuel.post(apiEndpoint)
            .header("Cookie", cookie)
            .jsonBody(maxRatioPayload.toString())
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

    override fun getAllTorrents(): List<Torrent> {
        val allTorrentsPayload = GetAllTorrentsPayload()
        val response = Fuel.post(apiEndpoint)
            .header("Cookie", cookie)
            .jsonBody(allTorrentsPayload.toString())
            .response()

        val (data, error) = response.third

        val allTorrents = arrayListOf<Torrent>()
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

        return allTorrents
    }

    override fun removeTorrent(torrentHash: String, withData: Boolean): DownpourResult {
        val payload = RemoveTorrentPayload(torrentHash, withData)
        val response = Fuel.post(apiEndpoint)
            .header("Cookie", cookie)
            .jsonBody(payload.toString())
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
        val payload = PauseTorrentPayload(torrentHash)
        val response = Fuel.post(apiEndpoint)
            .header("Cookie", this.cookie)
            .jsonBody(payload.toString())
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
        val payload = ResumeTorrentPayload(torrentHash)
        val response = Fuel.post(apiEndpoint)
            .header("Cookie", this.cookie)
            .jsonBody(payload.toString())
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

    override fun setMaxDownloadSpeed(torrentHash: String): DownpourResult {
        TODO("Not yet implemented")
    }

    override fun setMaxUploadSpeed(torrentHash: String): DownpourResult {
        TODO("Not yet implemented")
    }

    override fun forceRecheck(torrentHash: String): DownpourResult {
        TODO("Not yet implemented")
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
