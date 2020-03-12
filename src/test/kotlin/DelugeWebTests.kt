import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.serialization.UnstableDefault
import models.*
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

        @Test
        fun `response is parsed correctly - returns a List of Torrents`() {
            val client = mockk<Client>()
            val returnedJson = """{"id": 1,"result":{"hash1":{"comment":"","active_time":23030,"is_seed":true,"hash":"hash1","upload_payload_rate":0,"move_completed_path":"/path/to/downloads/","private":false,"total_payload_upload":60438823504,"paused":true,"seed_rank":339,"seeding_time":22984,"max_upload_slots":-1,"prioritize_first_last":false,"distributed_copies":0.0,"download_payload_rate":0,"message":"OK","num_peers":0,"max_download_speed":-1,"max_connections":-1,"compact":false,"ratio":37.82151412963867,"total_peers":124,"total_size":1597887280,"total_wanted":1597887280,"state":"Paused","file_priorities":[1,1,1,1],"max_upload_speed":-1,"remove_at_ratio":false,"tracker":"udp://tracker.com:2950","save_path":"/path/to/downloads/","progress":100.0,"time_added":1583840384.0,"tracker_host":"tracker.com","total_uploaded":60434516604,"files":[{"index":0,"path":"DownloadOne/FileOne.iso","offset":0,"size":1597776636},{"index":1,"path":"DownloadOne/README.txt","offset":1597776636,"size":30}],"total_done":1597887280,"num_pieces":762,"tracker_status":"tracker.com: Announce OK","total_seeds":368,"move_on_completed":false,"next_announce":0,"stop_at_ratio":false,"file_progress":[1.0,1.0,1.0,1.0],"move_completed":false,"piece_length":2097152,"all_time_download":1598002583,"move_on_completed_path":"/path/to/root","num_seeds":0,"peers":[],"name":"DownloadOne","trackers":[{"send_stats":true,"fails":0,"verified":false,"min_announce":null,"url":"http://tracker.com:80/announce","fail_limit":0,"next_announce":null,"complete_sent":false,"source":4,"start_sent":false,"tier":0,"updating":false},{"send_stats":true,"fails":0,"verified":false,"min_announce":null,"url":"udp://tracker.com:2950","fail_limit":0,"next_announce":null,"complete_sent":false,"source":4,"start_sent":false,"tier":0,"updating":false}],"total_payload_download":1598002583,"is_auto_managed":true,"seeds_peers_ratio":2.9677419662475586,"queue":-1,"num_files":4,"eta":0,"stop_ratio":2.0,"is_finished":true},"hash2":{"comment":"","active_time":34332,"is_seed":true,"hash":"hash2","upload_payload_rate":0,"move_completed_path":"/path/to/root","private":false,"total_payload_upload":12651894202,"paused":true,"seed_rank":300,"seeding_time":33923,"max_upload_slots":-1,"prioritize_first_last":false,"distributed_copies":0.0,"download_payload_rate":0,"message":"OK","num_peers":0,"max_download_speed":-1.0,"max_connections":-1,"compact":false,"ratio":5.1918721199035645,"total_peers":2,"total_size":2436865598,"total_wanted":2436865598,"state":"Paused","file_priorities":[1,1,1],"max_upload_speed":-1.0,"remove_at_ratio":false,"tracker":"udp://tracker.com:2730","save_path":"/path/to/downloads","progress":100.0,"time_added":1583095552.0,"tracker_host":"tracker.com","total_uploaded":12651894202,"files":[{"index":0,"path":"DownloadTwo/INFO.txt","offset":0,"size":30},{"index":1,"path":"DownloadTwo/File.iso","offset":30,"size":2436767043},{"index":2,"path":"DownloadTwo/README.md","offset":2436767073,"size":54}],"total_done":2436865598,"num_pieces":2324,"tracker_status":"tracker.com: Announce OK","total_seeds":10,"move_on_completed":false,"next_announce":0,"stop_at_ratio":true,"file_progress":[1.0,1.0,1.0],"move_completed":false,"piece_length":1048576,"all_time_download":2438021828,"move_on_completed_path":"/path/to/root","num_seeds":0,"peers":[],"name":"DownloadTwo","trackers":[{"send_stats":true,"fails":0,"verified":false,"min_announce":null,"url":"http://tracker.com:80/announce","fail_limit":0,"next_announce":null,"complete_sent":false,"source":4,"start_sent":false,"tier":0,"updating":false},{"send_stats":true,"fails":0,"verified":false,"min_announce":null,"url":"udp://tracker.com:2730","fail_limit":0,"next_announce":null,"complete_sent":false,"source":4,"start_sent":false,"tier":0,"updating":false}],"total_payload_download":2438021828,"is_auto_managed":true,"seeds_peers_ratio":5.0,"queue":-1,"num_files":3,"eta":0,"stop_ratio":5.0,"is_finished":true}}, error: null}"""
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray(Charsets.UTF_8)
            FuelManager.instance.client = client

            val expected = listOf(
                Torrent(
                    "",
                    23030,
                    true,
                    "hash1",
                    0,
                    "/path/to/downloads/",
                    false,
                    60438823504,
                    true,
                    339,
                    22984,
                    -1,
                    false,
                    0.0,
                    0,
                    "OK",
                    0,
                    -1.0,
                    -1,
                    false,
                    37.82151412963867,
                    124,
                    1597887280,
                    1597887280,
                    "Paused",
                    listOf(
                        1,
                        1,
                        1,
                        1
                    ),
                    -1.0,
                    false,
                    "udp://tracker.com:2950",
                    "/path/to/downloads/",
                    100.0,
                    1583840384.0,
                    "tracker.com",
                    60434516604,
                    listOf(
                        FileInTorrent(
                            0,
                            "DownloadOne/FileOne.iso",
                            0,
                            1597776636
                        ),
                        FileInTorrent(
                            1,
                            "DownloadOne/README.txt",
                            1597776636,
                            30
                        )
                    ),
                    1597887280,
                    762,
                    "tracker.com: Announce OK",
                    368,
                    false,
                    0,
                    false,
                    listOf(
                        1.0,
                        1.0,
                        1.0,
                        1.0
                    ),
                    false,
                    2097152,
                    1598002583,
                    "/path/to/root",
                    0,
                    listOf(),
                    "DownloadOne",
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
                        ),
                        Tracker(
                            true,
                            0,
                            false,
                            null,
                            "udp://tracker.com:2950",
                            0,
                            null,
                            false,
                            4,
                            false,
                            0,
                            false
                        )
                    ),
                    1598002583,
                    true,
                    2.9677419662475586,
                    -1,
                    4,
                    0,
                    2.0,
                    true
                ),
                Torrent(
                    "",
                    34332,
                    true,
                    "hash2",
                    0,
                    "/path/to/root",
                    false,
                    12651894202,
                    true,
                    300,
                    33923,
                    -1,
                    false,
                    0.0,
                    0,
                    "OK",
                    0,
                    -1.0,
                    -1,
                    false,
                    5.1918721199035645,
                    2,
                    2436865598,
                    2436865598,
                    "Paused",
                    listOf(
                        1,
                        1,
                        1
                    ),
                    -1.0,
                    false,
                    "udp://tracker.com:2730",
                    "/path/to/downloads",
                    100.0,
                    1583095552.0,
                    "tracker.com",
                    12651894202,
                    listOf(
                        FileInTorrent(
                            0,
                            "DownloadTwo/INFO.txt",
                            0,
                            30
                        ),
                        FileInTorrent(
                            1,
                            "DownloadTwo/File.iso",
                            30,
                            2436767043
                        ),
                        FileInTorrent(
                            2,
                            "DownloadTwo/README.md",
                            2436767073,
                            54
                        )
                    ),
                    2436865598,
                    2324,
                    "tracker.com: Announce OK",
                    10,
                    false,
                    0,
                    true,
                    listOf(
                        1.0,
                        1.0,
                        1.0
                    ),
                    false,
                    1048576,
                    2438021828,
                    "/path/to/root",
                    0,
                    listOf(),
                    "DownloadTwo",
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
                        ),
                        Tracker(
                            true,
                            0,
                            false,
                            null,
                            "udp://tracker.com:2730",
                            0,
                            null,
                            false,
                            4,
                            false,
                            0,
                            false
                        )
                    ),
                    2438021828,
                    true,
                    5.0,
                    -1,
                    3,
                    0,
                    5.0,
                    true
                )
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
    inner class AddMagnet {
        @Test
        fun `correct payload is sent`() {
            val returnedJson = """{"id": 1, "result": null, "error": null}"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client

            val expectedPayload = """{"id":1,"method":"core.add_torrent_magnet","params":["TEST_MAGNET",{}]}"""

            testSession.addMagnet("TEST_MAGNET")

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
        fun `returns Success when result is not null`() {
            val returnedJson = """{"id": 1, "result": "hash", "error": null}"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client

            val actual = testSession.addMagnet("TEST_MAGNET")

            assertThat(actual).isEqualTo(AddMagnetResult.Success)
        }

        @Test
        fun `returns AlreadyExists when result and error are null`() {
            val returnedJson = """{"id": 1, "result": null, "error": null}"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client

            val actual = testSession.addMagnet("TEST_MAGNET")

            assertThat(actual).isEqualTo(AddMagnetResult.AlreadyExists)
        }

        @Test
        fun `returns Failure when request fails`() {
            val returnedJson = """{"id": 1, "result": null, "error": {"message":"unit_test_failure", "code": 400}}"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client

            val actual = testSession.addMagnet("TEST_MAGNET")

            assertThat(actual).isEqualTo(AddMagnetResult.Failure)
        }

        @Test
        fun `throws FuelError when 500 error hit`() {
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 500
            every { client.executeRequest(any()).responseMessage } returns "Server Error"
            FuelManager.instance.client = client

            assertThatThrownBy { testSession.addMagnet("TEST_MAGNET") }
                .isInstanceOf(FuelError::class.java)
        }
    }

    @Nested
    inner class AddTorrentFile {
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