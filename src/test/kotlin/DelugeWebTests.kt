import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.serialization.UnstableDefault
import models.DownpourResult
import models.FileInTorrent
import models.Torrent
import models.Tracker
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
            val expectedPayload = """{"id":1,"method":"core.get_torrents_status","params":[{},[]]}"""

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

//        @Test
//        fun `response is parsed correctly - returns a List of Torrents`() {
//            val client = mockk<Client>()
//            val returnedJson = """{"id":1,"result":{"stats":{"upload_protocol_rate":78799,"max_upload":-1.0,"download_protocol_rate":75432,"download_rate":2353262,"has_incoming_connections":true,"num_connections":4,"max_download":-1.0,"upload_rate":0,"dht_nodes":386,"free_space":3419688734720,"max_num_connections":50},"connected":true,"torrents":{"hash1":{"max_download_speed":-1.0,"upload_payload_rate":0,"download_payload_rate":2353262,"num_peers":0,"ratio":0.0,"total_peers":2,"max_upload_speed":-1.0,"state":"Downloading","distributed_copies":2.0220000743865967,"save_path":"/save/directory/path","progress":100.0,"time_added":1582554880.0,"tracker_host":"tracker.host","total_uploaded":0,"total_done":40370254,"total_wanted":1771415839,"total_seeds":6,"seeds_peers_ratio":3.0,"num_seeds":2,"name":"Test Torrent One","is_auto_managed":true,"queue":0,"eta":735},"hash2":{"max_download_speed":-1.0,"upload_payload_rate":0,"download_payload_rate":2353262,"num_peers":0,"ratio":0.0,"total_peers":2,"max_upload_speed":-1.0,"state":"Downloading","distributed_copies":2.0220000743865967,"save_path":"/save/directory/path","progress":97.4,"time_added":1582554880.0,"tracker_host":"tracker.host","total_uploaded":0,"total_done":40370254,"total_wanted":1771415839,"total_seeds":6,"seeds_peers_ratio":3.0,"num_seeds":2,"name":"Test Torrent Two","is_auto_managed":true,"queue":0,"eta":735}},"filters":{"state":[["All",1],["Downloading",1],["Seeding",0],["Active",1],["Paused",0],["Queued",0],["Checking",0],["Error",0]],"tracker_host":[["All",1],["Error",0],["tracker.com",1]]}},"error":null}"""
//            every { client.executeRequest(any()).statusCode } returns 200
//            every { client.executeRequest(any()).responseMessage } returns "OK"
//            every { client.executeRequest(any()).data } returns returnedJson.toByteArray(Charsets.UTF_8)
//            FuelManager.instance.client = client
//
//            val expected = listOf(
//                Torrent("hash1", 100.0, "Test Torrent One"),
//                Torrent("hash2", 97.4, "Test Torrent Two")
//            )
//
//            val actual = testSession.getAllTorrents()
//
//            assertThat(actual)
//                .containsAll(expected)
//        }

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
            val expectedPayload = """{"id":1,"method":"web.add_torrents","params":[[{"path":"TESTMAGNET","options":{"file_priorities":[1],"add_paused":false,"compact_allocation":false,"download_location":"/path/to/downloads/","move_on_completion":false,"move_to_location":null,"max_connections":-1,"max_download_speed":-1,"max_upload_slots":-1,"max_upload_speed":-1,"prioritize_first_last_pieces":false}}]]}"""

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

    @Nested
    inner class Pause {
        @Test
        fun `payload is stringified correctly`() {
            val returnedJson = """{"id": 1, "result": null, "error": null}"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client
            val expectedPayload = """{"id":1,"method":"core.pause_torrent","params":[["TEST_HASH"]]}"""

            testSession.pauseTorrent("TEST_HASH")

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

            val actual = testSession.pauseTorrent("TEST_HASH")

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

            val actual = testSession.pauseTorrent("TEST_HASH")

            assertThat(actual).isEqualTo(DownpourResult.FAILURE)
        }

        @Test
        fun `500 error returns FAILURE enum`() {
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 500
            every { client.executeRequest(any()).responseMessage } returns "Server Error"
            FuelManager.instance.client = client

            val actual = testSession.pauseTorrent("TEST_HASH")

            assertThat(actual).isEqualTo(DownpourResult.FAILURE)
        }
    }

    @Nested
    inner class Resume {
        @Test
        fun `payload is stringified correctly`() {
            val returnedJson = """{"id": 1, "result": null, "error": null}"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client
            val expectedPayload = """{"id":1,"method":"core.resume_torrent","params":[["TEST_HASH"]]}"""

            testSession.resumeTorrent("TEST_HASH")

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

            val actual = testSession.resumeTorrent("TEST_HASH")

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

            val actual = testSession.resumeTorrent("TEST_HASH")

            assertThat(actual).isEqualTo(DownpourResult.FAILURE)
        }

        @Test
        fun `500 error returns FAILURE enum`() {
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 500
            every { client.executeRequest(any()).responseMessage } returns "Server Error"
            FuelManager.instance.client = client

            val actual = testSession.resumeTorrent("TEST_HASH")

            assertThat(actual).isEqualTo(DownpourResult.FAILURE)
        }
    }

    @Nested
    inner class GetSingleTorrent {
        @Test
        fun `payload is stringified correctly`() {
            val returnedJson = """{"id": 1, "result": null, "error": null}"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client
            val expectedPayload = """{"id":1,"method":"core.get_torrent_status","params":["TEST_HASH",[]]}"""

            testSession.getTorrentDetails("TEST_HASH")

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
        fun `success result returns correct Torrent object`() {
            val client = mockk<Client>()
            val returnedJson = """{"id":1,"result":{"comment":"","active_time":88528,"is_seed":true,"hash":"hash1","upload_payload_rate":0,"move_completed_path":"/path/to/directory","private":false,"total_payload_upload":35008787612,"paused":true,"seed_rank":538,"seeding_time":87813,"max_upload_slots":-1,"prioritize_first_last":false,"distributed_copies":0.0,"download_payload_rate":0,"message":"OK","num_peers":0,"max_download_speed":-1.0,"max_connections":-1,"compact":false,"ratio":5.861778736114502,"total_peers":6,"total_size":5972383023,"total_wanted":5972383023,"state":"Paused","file_priorities":[1,1],"max_upload_speed":-1.0,"remove_at_ratio":false,"tracker":"udp://tracker.com","save_path":"/path/to/downloads","progress":100.0,"time_added":1583095552.0,"tracker_host":"tracker.com","total_uploaded":35008787612,"files":[{"index":0,"path":"directory/file0","offset":0,"size":4028},{"index":1,"path":"directory/file1","offset":4028,"size":794650369}],"total_done":1000000,"num_pieces":1024,"tracker_status":"Announce OK","total_seeds":13,"move_on_completed":false,"next_announce":0,"stop_at_ratio":true,"file_progress":[1.0, 1.0],"move_completed":false,"piece_length":1,"all_time_download":1,"move_on_completed_path":"/path/to/directory","num_seeds":0,"peers":[],"name":"Test Torrent","trackers":[{"send_stats":true,"fails":0,"verified":false,"min_announce":null,"url":"http://tracker.com:80/announce","fail_limit":0,"next_announce":null,"complete_sent":false,"source":4,"start_sent":false,"tier":0,"updating":false}],"total_payload_download":5975695473,"is_auto_managed":true,"seeds_peers_ratio":2.1666667461395264,"queue":-1,"num_files":30,"eta":0,"stop_ratio":5.0,"is_finished":true},"error":null}"""
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray(Charsets.UTF_8)
            FuelManager.instance.client = client

            val expected = Torrent(
                "",
                88528,
                true,
                "hash1",
                0,
                "/path/to/directory",
                false,
                35008787612,
                true,
                538,
                87813,
                -1,
                false,
                0.0,
                0,
                "OK",
                0,
                -1.0,
                -1,
                false,
                5.861778736114502,
                6,
                5972383023,
                5972383023,
                "Paused",
                listOf(1, 1),
                -1.0,
                false,
                "udp://tracker.com",
                "/path/to/downloads",
                100.0,
                1583095552.0,
                "tracker.com",
                35008787612,
                listOf(FileInTorrent(0,"directory/file0",0,4028), FileInTorrent(1,"directory/file1",4028,794650369)),
                1000000,
                1024,
                "Announce OK",
                13,
                false,
                0,
                true,
                listOf(1.0, 1.0),
                false,
                1,
                1,
                "/path/to/directory",
                0,
                listOf(),
                "Test Torrent",
                listOf(
                    Tracker(
                        true,
                        0,
                        false,
                        null,
                        "http://tracker.com:80/announce",
                        0,
                        null,
                        false,
                        4,
                        false,
                        0,
                        false
                    )
                ),
                5975695473,
                true,
                2.1666667461395264,
                -1,
                30,
                0,
                5.0,
                true
            )

            val actual = testSession.getTorrentDetails("hash1")

            assertThat(actual)
                .isEqualTo(expected)
        }

        @Test
        fun `failure result returns null`() {
            val client = mockk<Client>()
            val returnedJson = """{"id":1,"result":null,"error":{"message": "Unit Test Error", "code": -1}}"""
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray(Charsets.UTF_8)
            FuelManager.instance.client = client

            val actual = testSession.getTorrentDetails("test_hash")

            assertThat(actual)
                .isNull()
        }

        @Test
        fun `500 error throws FuelError`() {
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 500
            every { client.executeRequest(any()).responseMessage } returns "Server Error"
            FuelManager.instance.client = client

            assertThatThrownBy { testSession.getTorrentDetails("test_hash") }
                .isInstanceOf(FuelError::class.java)
        }
    }
}