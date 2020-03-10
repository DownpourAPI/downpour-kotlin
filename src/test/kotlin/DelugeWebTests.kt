import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.serialization.UnstableDefault
import models.DownpourResult
import models.Torrent
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.assertj.core.api.Assertions.*
import java.io.File

@TestInstance(Lifecycle.PER_CLASS)
@UnstableDefault
class DelugeWebTests {
    val testSession = DelugeWebSession("unit_test_cookie")

    @Nested
    inner class GetAll {
        @Test
        fun `payload is stringified correctly`() {
            val returnedJson = """{"id": 1, "result": null, "error": null}"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client
            val expectedPayload = """{"id":1,"method":"web.update_ui","params":[["queue","name","total_wanted","state","progress","num_seeds","total_seeds","num_peers","total_peers","download_payload_rate","upload_payload_rate","eta","ratio","distributed_copies","is_auto_managed","time_added","tracker_host","save_path","total_done","total_uploaded","max_download_speed","max_upload_speed","seeds_peers_ratio"],{}]}"""

            testSession.getAllTorrents()

            verify(exactly = 1) {
                client.executeRequest(
                    withArg {
                        assertThat(it.body.asString("application/json").replace(" ", ""))
                            .isEqualTo(expectedPayload)
                    }
                )
            }
        }

        @Test
        fun `response is parsed correctly - returns a List of Torrents`() {
            val client = mockk<Client>()
            val returnedJson = """{"id":1,"result":{"stats":{"upload_protocol_rate":78799,"max_upload":-1.0,"download_protocol_rate":75432,"download_rate":2353262,"has_incoming_connections":true,"num_connections":4,"max_download":-1.0,"upload_rate":0,"dht_nodes":386,"free_space":3419688734720,"max_num_connections":50},"connected":true,"torrents":{"hash1":{"max_download_speed":-1.0,"upload_payload_rate":0,"download_payload_rate":2353262,"num_peers":0,"ratio":0.0,"total_peers":2,"max_upload_speed":-1.0,"state":"Downloading","distributed_copies":2.0220000743865967,"save_path":"/save/directory/path","progress":100.0,"time_added":1582554880.0,"tracker_host":"tracker.host","total_uploaded":0,"total_done":40370254,"total_wanted":1771415839,"total_seeds":6,"seeds_peers_ratio":3.0,"num_seeds":2,"name":"Test Torrent One","is_auto_managed":true,"queue":0,"eta":735},"hash2":{"max_download_speed":-1.0,"upload_payload_rate":0,"download_payload_rate":2353262,"num_peers":0,"ratio":0.0,"total_peers":2,"max_upload_speed":-1.0,"state":"Downloading","distributed_copies":2.0220000743865967,"save_path":"/save/directory/path","progress":97.4,"time_added":1582554880.0,"tracker_host":"tracker.host","total_uploaded":0,"total_done":40370254,"total_wanted":1771415839,"total_seeds":6,"seeds_peers_ratio":3.0,"num_seeds":2,"name":"Test Torrent Two","is_auto_managed":true,"queue":0,"eta":735}},"filters":{"state":[["All",1],["Downloading",1],["Seeding",0],["Active",1],["Paused",0],["Queued",0],["Checking",0],["Error",0]],"tracker_host":[["All",1],["Error",0],["rarbg.me",1]]}},"error":null}"""
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray(Charsets.UTF_8)
            FuelManager.instance.client = client

            val expected = listOf(
                Torrent("hash1", 100.0, "Test Torrent One"),
                Torrent("hash2", 97.4, "Test Torrent Two")
            )

            val actual = testSession.getAllTorrents()

            assertThat(actual)
                .containsAll(expected)
        }

        @Test
        fun `request rejection is handled`() {
            val client = mockk<Client>()
            val returnedJson = """{"id":1,"result":null,"error":{"message": "Unit Test Error", "code": 500}}"""
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray(Charsets.UTF_8)
            FuelManager.instance.client = client

            assertThatThrownBy {
                testSession.getAllTorrents()
            }.isInstanceOf(Error::class.java)
        }

        @Test
        fun `500 response throws FuelError`() {
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 500
            every { client.executeRequest(any()).responseMessage } returns "Server Error"
            FuelManager.instance.client = client

            assertThatThrownBy { testSession.getAllTorrents() }
                .isInstanceOf(FuelError::class.java)
        }
    }

    @Nested
    inner class AddTorrent {
        @Test
        fun `payload is stringified correctly`() {
            val returnedJson = """{"id": 1, "result": null, "error": null}"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client
            val expectedPayload = """{"id":1,"method":"web.add_torrents","params":[[{"path":"TESTMAGNET","options":{"filePriorities":[],"addPaused":false,"compactAllocation":false,"downloadLocation":"/home32/reteph/downloads/","moveOnCompletion":false,"moveToLocation":null,"maxConnections":-1,"maxDownloadSpeed":-1,"maxUploadSlots":-1,"maxUploadSpeed":-1,"prioritizeFirstLastPieces":false}}]]}"""

            testSession.addTorrent("TESTMAGNET")

            verify(exactly = 1) {
                client.executeRequest(
                    withArg {
                        assertThat(it.body.asString("application/json").replace(" ", ""))
                            .isEqualTo(expectedPayload)
                    }
                )
            }
        }

        @Test
        fun `success result returns SUCCESS enum`() {
            val returnedJson = """{"id": 1, "result": true, "error": null}"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client

            val actual = testSession.addTorrent("TESTMAGNET")

            assertThat(actual)
                .isEqualTo(DownpourResult.SUCCESS)
        }

        @Test
        fun `failure result returns FAILURE enum`() {
            val returnedJson = """{"id": 1, "result": null, "error": {"message": "Unit Test Failure", "code": -1}}"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client

            val actual = testSession.addTorrent("TESTMAGNET")
            assertThat(actual)
                .isEqualTo(DownpourResult.FAILURE)
        }

        @Test
        fun `500 error returns FAILURE enum`() {
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 500
            every { client.executeRequest(any()).responseMessage } returns "Server Error"
            FuelManager.instance.client = client

            val actual = testSession.addTorrent("TESTMAGNET")

            assertThat(actual).isEqualTo(DownpourResult.FAILURE)
        }
    }

    @Nested
    inner class RemoveTorrent {
        @Test
        fun `payload is stringified correctly`() {
            val returnedJson = """{"id": 1, "result": null, "error": null}"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client
            val expectedPayload = """{"params":["TEST_HASH",true],"id":1,"method":"core.remove_torrent"}"""

            testSession.removeTorrent("TEST_HASH")

            verify(exactly = 1) {
                client.executeRequest(
                    withArg {
                        assertThat(it.body.asString("application/json").replace(" ", ""))
                            .isEqualTo(expectedPayload)
                    }
                )
            }
        }

        @Test
        fun `success returns SUCCESS enum`() {
            val returnedJson = """{"id": 1, "result": true, "error": null}"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client

            val actual = testSession.removeTorrent("TEST_HASH")

            assertThat(actual).isEqualTo(DownpourResult.SUCCESS)
        }

        @Test
        fun `failure result returns FAILURE enum`() {
            val returnedJson = """{"id": 1, "result": null, "error": {"message": "Unit Test Failure", "code": -1}}"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client

            val actual = testSession.removeTorrent("TEST_HASH")

            assertThat(actual).isEqualTo(DownpourResult.FAILURE)
        }

        @Test
        fun `500 error returns FAILURE enum`() {
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 500
            every { client.executeRequest(any()).responseMessage } returns "Server Error"
            FuelManager.instance.client = client

            val actual = testSession.removeTorrent("TEST_HASH")

            assertThat(actual).isEqualTo(DownpourResult.FAILURE)
        }
    }

    @Nested
    inner class SetMaxRatio {
        @Test
        fun `payload is stringified correctly`() {
            val returnedJson = """{"id": 1, "result": null, "error": null}"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client
            val expectedPayload = """{"id":1,"method":"core.set_torrent_options","params":[["TEST_HASH"],{"stop_at_ratio":true,"stop_ratio":5}]}"""

            testSession.setMaxRatio("TEST_HASH", 5)

            verify(exactly = 1) {
                client.executeRequest(
                    withArg {
                        assertThat(it.body.asString("application/json").replace(" ", ""))
                            .isEqualTo(expectedPayload)
                    }
                )
            }
        }

        @Test
        fun `success returns SUCCESS enum`() {
            val returnedJson = """{"id": 1, "result": true, "error": null}"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client

            val actual = testSession.setMaxRatio("TEST_HASH", 5)

            assertThat(actual).isEqualTo(DownpourResult.SUCCESS)
        }

        @Test
        fun `failure result returns FAILURE enum`() {
            val returnedJson = """{"id": 1, "result": null, "error": {"message": "Unit Test Failure", "code": -1}}"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client

            val actual = testSession.setMaxRatio("TEST_HASH", 5)

            assertThat(actual).isEqualTo(DownpourResult.FAILURE)
        }

        @Test
        fun `500 error returns FAILURE enum`() {
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 500
            every { client.executeRequest(any()).responseMessage } returns "Server Error"
            FuelManager.instance.client = client

            val actual = testSession.setMaxRatio("TEST_HASH", 5)

            assertThat(actual).isEqualTo(DownpourResult.FAILURE)
        }
    }

    @Nested
    inner class UploadTorrentFile {
        @Test
        fun `success result returns location of uploaded file`() {
            val mockRemoteLocation = "/tmp/mocklocation.torrent"
            val returnedJson = """{"files": ["$mockRemoteLocation"],"success": true}"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client

            val mockFile = File.createTempFile("tmp", ".torrent")

            val returnedLocation = testSession.uploadTorrentFile(mockFile)

            assertThat(returnedLocation)
                .isEqualTo(mockRemoteLocation)
        }


        @Test
        fun `failure result returns null`() {
            val returnedJson = """{"files": [],"success": false}"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client

            val mockFile = File.createTempFile("tmp", ".torrent")

            val returnedLocation = testSession.uploadTorrentFile(mockFile)

            assertThat(returnedLocation)
                .isNull()
        }

        @Test
        fun `500 error returns null`() {
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 500
            every { client.executeRequest(any()).responseMessage } returns "Server Error"
            FuelManager.instance.client = client

            val mockFile = File.createTempFile("tmp", ".torrent")

            val returnedLocation = testSession.uploadTorrentFile(mockFile)

            assertThat(returnedLocation)
                .isNull()
        }
    }


    //////////////////////////
    // NOT YET IMPLEMENTED: //
    //////////////////////////

    @Nested
    inner class GetSingleTorrent {
        @Test
        fun `payload is stringified correctly`() {
            assertThat(true).isFalse()
        }

        @Test
        fun `success result returns correct TorrentDetails object`() {
            assertThat(true).isFalse()
        }

        @Test
        fun `failure result returns FAILURE enum`() {
            assertThat(true).isFalse()
        }

        @Test
        fun `500 error throws FuelError`() {
            assertThat(true).isFalse()
        }
    }

    @Nested
    inner class Pause {
        @Test
        fun `payload is stringified correctly`() {
            assertThat(true).isFalse()
        }

        @Test
        fun `success result returns SUCCESS enum`() {
            assertThat(true).isFalse()
        }

        @Test
        fun `failure result returns FAILURE enum`() {
            assertThat(true).isFalse()
        }

        @Test
        fun `500 error throws FuelError`() {
            assertThat(true).isFalse()
        }
    }

    @Nested
    inner class Resume {
        @Test
        fun `payload is stringified correctly`() {
            assertThat(true).isFalse()
        }

        @Test
        fun `success result returns SUCCESS enum`() {
            assertThat(true).isFalse()
        }

        @Test
        fun `failure result returns FAILURE enum`() {
            assertThat(true).isFalse()
        }

        @Test
        fun `500 error throws FuelError`() {
            assertThat(true).isFalse()
        }
    }
}