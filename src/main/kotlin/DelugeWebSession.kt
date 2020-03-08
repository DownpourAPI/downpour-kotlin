import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.DataPart
import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.extensions.jsonBody
import interfaces.SeedboxController
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import models.*
import java.io.File

@UnstableDefault
class DelugeWebSession: SeedboxController {
    private val defaultUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:72.0) Gecko/20100101 Firefox/72.0"
    private var requestId = 1
    private var cookie: String = ""
    private var apiEndpoint: String

    private val json = Json(JsonConfiguration(strictMode = false))


    internal constructor(unitTestCookie: String) {
        this.cookie = unitTestCookie
        this.apiEndpoint = "http://unit-test"
    }

    constructor(apiEndpoint: String, password: String) {
        this.apiEndpoint = apiEndpoint
        login(password)
    }

    override fun login(password: String): String {
        val payload = LoginPayload(listOf(password), requestId++)
        val result = Fuel.post(apiEndpoint)
            .header("User-Agent", defaultUserAgent)
            .jsonBody(json.stringify(LoginPayload.serializer(), payload))
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
                println(connectionError)
                throw connectionError
            }
            else -> {
                throw Error("Something weird happened while logging in")
            }
        }
    }

    override fun addMagnet(magnet: String): DownpourResult {
        val magnetPayload = AddMagnetPayload(magnet)

        val response = Fuel.post(apiEndpoint)
            .jsonBody(json.stringify(AddMagnetPayload.serializer(), magnetPayload))
            .header("User-Agent", defaultUserAgent)
            .header("Cookie", cookie)
            .response()
            .third

        // TODO: Convert to using Result.fold or Result.success & Result.failure
        val (data, error) = response

        return if (data != null) {
            val addMagnetResponse: DelugeResponse = json.parse(DelugeResponse.serializer(), data.toString(Charsets.UTF_8))
            when (addMagnetResponse.result) {
                true -> DownpourResult.SUCCESS
                false -> DownpourResult.FAILURE
                null -> DownpourResult.FAILURE
            }
        } else {
            println(error)
            DownpourResult.FAILURE
        }
    }

    override fun addTorrentFile(torrentFilePath: String): DownpourResult {
        TODO("Not yet implemented")
    }

    override fun getTorrentDetails(torrentHash: String): DelugeTorrentInfo {
        TODO("Not implemented")
    }

    override fun setMaxRatio(torrentHash: String, maxRatio: Int): DownpourResult {
        val maxRatioPayload: MaxRatioPayload = MaxRatioPayload.defaultPayload(torrentHash, maxRatio)

        val response = Fuel.post(apiEndpoint)
            .header("User-Agent", defaultUserAgent)
            .header("Cookie", cookie)
            .jsonBody(maxRatioPayload.toString())
            .response()
            .third

        val (data, error) = response

        return if (data != null) {
            val addMagnetResponse: DelugeResponse = json.parse(DelugeResponse.serializer(), data.toString(Charsets.UTF_8))
            when (addMagnetResponse.result) {
                true -> DownpourResult.SUCCESS
                false -> DownpourResult.FAILURE
                null -> DownpourResult.FAILURE
            }
        } else {
            println(error)
            DownpourResult.FAILURE
        }
    }

    override fun getAllTorrents(): List<Torrent> {
        val allTorrentsPayload = GetAllTorrentsPayload.defaultPayload()
        val response = Fuel.post(apiEndpoint)
            .header("Cookie", cookie)
            .header("User-Agent", defaultUserAgent)
            .jsonBody(allTorrentsPayload.toString())
            .response()

        val (data, error) = response.third

        val allTorrents = arrayListOf<Torrent>()
        if (data != null) {
            val jsonResponse = data.toString(Charsets.UTF_8)
            val torrentResponse: GetAllTorrentsResponse = json.parse(GetAllTorrentsResponse.serializer(), jsonResponse)

            if (torrentResponse.error != null) { throw Error(torrentResponse.error.message) }
            if (torrentResponse.result == null) { return listOf() }

            for (torrent in torrentResponse.result.torrents) {
                allTorrents.add(
                    Torrent(
                        torrent.key,
                        torrent.value.progress,
                        torrent.value.name
                    )
                )
            }
        } else {
            throw error!!
        }

        return allTorrents
    }

    override fun removeTorrent(torrentHash: String, withData: Boolean): DownpourResult {
        val payload = RemoveTorrentPayload.defaultPayload(torrentHash, withData)
        val response = Fuel.post(apiEndpoint)
            .header("Cookie", cookie)
            .header("User-Agent", defaultUserAgent)
            .jsonBody(json.stringify(RemoveTorrentPayload.serializer(), payload))
            .response()
            .third

        val (data, error) = response

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
        val payload = PauseTorrentPayload.defaultPayload(torrentHash)
        val response = Fuel.post(apiEndpoint)
            .header("Cookie", this.cookie)
            .header("User-Agent", defaultUserAgent)
            .jsonBody(json.stringify(PauseTorrentPayload.serializer(), payload))
            .response()
            .third
        val (data, error) = response

        return if (data != null) {
            DownpourResult.SUCCESS
        } else {
            println(error)
            DownpourResult.FAILURE
        }    }

    override fun resumeTorrent(torrentHash: String): DownpourResult {
        val payload = ResumeTorrentPayload.defaultPayload(torrentHash)
        val response = Fuel.post(apiEndpoint)
            .header("Cookie", this.cookie)
            .header("User-Agent", defaultUserAgent)
            .jsonBody(json.stringify(ResumeTorrentPayload.serializer(), payload))
            .response()
            .third
        val (data, error) = response

        return if (data != null) {
            DownpourResult.SUCCESS
        } else {
            println(error)
            DownpourResult.FAILURE
        }
    }
}