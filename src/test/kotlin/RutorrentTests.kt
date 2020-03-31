import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.hnrhn.downpour.common.AddMagnetStatus
import com.hnrhn.downpour.common.AddTorrentFileStatus
import com.hnrhn.downpour.common.DownpourResult
import com.hnrhn.downpour.common.Torrent
import com.hnrhn.downpour.impl.rutorrent.RutorrentSession
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RutorrentTests {
    val testSession = RutorrentSession("https://example.com", "unit", "test")

    @Nested
    inner class GetAll {
        @Test
        fun `response is parsed correctly - returns a List of Torrents`() {
            val client = mockk<Client>()
            val returnedJson = """{"t":{"HASH1":["1","0","1","1","DOWNLOAD_ONE","3655647378","1744","1744","3655647378","42224261995","11550","3556004","0","2097152","","47","0","48","0","0","2","1585676761","14478032","0","1744","\/path\/to\/download\/ONE","0","3","1","Tracker: [Timeout was reached]","","3212046032896","0","1", "seed"],"HASH2":["1","0","1","1","DOWNLOAD_TWO","629785740","2403","2403","629785740","873852687","1387","0","0","262144","","0","0","0","0","0","2","1585679580","2110386","0","2403","\/path\/to\/download\/Two","0","3","1","Tracker: [Timeout was reached]","","3212046032896","0","1", "seed"]},"cid":2000000002}"""
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray(Charsets.UTF_8)
            FuelManager.instance.client = client

            val expected = listOf(
                Torrent(
                    "HASH1",
                    "DOWNLOAD_ONE",
                    3556004,
                    0,
                    48,
                    47,
                    11.55,
                    3655647378,
                    100.0,
                    "seed",
                    1585676761.0,
                    3655647378,
                    42224261995,
                    listOf(),
                    "/path/to/download/ONE"
                ),
                Torrent(
                    "HASH2",
                    "DOWNLOAD_TWO",
                    0,
                    0,
                    0,
                    0,
                    1.387,
                    629785740,
                    100.0,
                    "seed",
                    1585679580.0,
                    629785740,
                    873852687,
                    listOf(),
                    "/path/to/download/Two"
                )
            )

            val actual = testSession.getAllTorrents()

            Assertions.assertThat(actual)
                .containsAll(expected)
        }

        @Test
        fun `error response throws Error()`() {
            val client = mockk<Client>()
            val returnedXml = """<?xml version="1.0" encoding="UTF-8"?>
<methodResponse>
  <fault>
    <value>
      <struct>
        <member>
          <name>faultCode</name>
          <value>
            <i4>-503</i4>
          </value>
        </member>
        <member>
          <name>faultString</name>
          <value>
            <string>Call XML not a proper XML-RPC call.  Call is not valid XML.  not well-formed</string>
          </value>
        </member>
      </struct>
    </value>
  </fault>
</methodResponse>
"""
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedXml.toByteArray(Charsets.UTF_8)
            FuelManager.instance.client = client

            Assertions.assertThatThrownBy {
                testSession.getAllTorrents()
            }.isInstanceOf(Error::class.java)
        }

        @Test
        fun `500 response throws FuelError`() {
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 500
            every { client.executeRequest(any()).responseMessage } returns "Server Error"
            FuelManager.instance.client = client

            Assertions.assertThatThrownBy { testSession.getAllTorrents() }
                .isInstanceOf(FuelError::class.java)
        }
    }

    @Nested
    inner class AddMagnet {
        @Test
        fun `returns Success with Hash when result is not null`() {
            val returnedJson = """noty("magnet:?xt=urn:btih:TEST_TORRENT_HASH&whatever"+theUILang.addTorrentSuccess,"success");"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client

            val actual = testSession.addMagnet("magnet:?xt=urn:btih:TEST_TORRENT_HASH&whatever")

            Assertions.assertThat(actual.status).isEqualTo(AddMagnetStatus.Success)
            Assertions.assertThat(actual.resultHash).isEqualTo("TEST_TORRENT_HASH")
        }

        @Test
        fun `returns Failure when request fails`() {
            val returnedJson = """noty("magnet:?xt=urn:btih:torrent_hash&goesinhere"+theUILang.addTorrentFailed,"error");"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client

            val actual = testSession.addMagnet("TEST_MAGNET")

            Assertions.assertThat(actual.status).isEqualTo(AddMagnetStatus.Failure)
        }

        @Test
        fun `throws FuelError when 500 error hit`() {
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 500
            every { client.executeRequest(any()).responseMessage } returns "Server Error"
            FuelManager.instance.client = client

            Assertions.assertThatThrownBy { testSession.addMagnet("TEST_MAGNET") }
                .isInstanceOf(FuelError::class.java)
        }
    }

    @Nested
    inner class AddTorrentFile {
        @Test
        fun `returns Success with no Hash`() {
            val returnedBody = """noty("UPLOADED_FILE.torrent - "+theUILang.addTorrentSuccess,"success");"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedBody.toByteArray()
            FuelManager.instance.client = client

            val testFile = File.createTempFile("tmp", ".txt")

            val actual = testSession.addTorrentFile(testFile)

            Assertions.assertThat(actual.status).isEqualTo(AddTorrentFileStatus.Success)
            Assertions.assertThat(actual.resultHash).isEqualTo("")
        }

        @Test
        fun `returns Failure when request fails`() {
            val returnedJson = """noty("UPLOADED_FILE.torrent - "+theUILang.addTorrentFailed,"error");"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client

            val testFile = File.createTempFile("tmp", ".txt")

            val actual = testSession.addTorrentFile(testFile)

            Assertions.assertThat(actual.status).isEqualTo(AddTorrentFileStatus.Failure)
        }

        @Test
        fun `throws FuelError when 500 error hit`() {
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 500
            every { client.executeRequest(any()).responseMessage } returns "Server Error"
            FuelManager.instance.client = client

            val testFile = File.createTempFile("tmp", ".txt")

            Assertions.assertThatThrownBy { testSession.addTorrentFile(testFile) }
                .isInstanceOf(FuelError::class.java)
        }
    }

    @Nested
    inner class RemoveTorrent {
        @Test
        fun `success returns SUCCESS enum`() {
            val returnedJson = """<?xml version="1.0" encoding="UTF-8"?>
<methodResponse>
  <params>
    <param>
      <value>
        <array>
          <data>
            <value>
              <array>
                <data>
                  <value>
                    <string>1</string>
                  </value>
                </data>
              </array>
            </value>
            <value>
              <array>
                <data>
                  <value>
                    <i4>0</i4>
                  </value>
                </data>
              </array>
            </value>
            <value>
              <array>
                <data>
                  <value>
                    <i4>0</i4>
                  </value>
                </data>
              </array>
            </value>
          </data>
        </array>
      </value>
    </param>
  </params>
</methodResponse>
"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client

            val actual = testSession.removeTorrent("TEST_HASH")

            Assertions.assertThat(actual).isEqualTo(DownpourResult.SUCCESS)
        }

        @Test
        fun `failure result returns FAILURE enum`() {
            val returnedJson = """<?xml version="1.0" encoding="UTF-8"?>
<methodResponse>
  <params>
    <param>
      <value>
        <array>
          <data>
            <value>
              <array>
                <data>
                  <value>
                    <string>0</string>
                  </value>
                </data>
              </array>
            </value>
            <value>
              <array>
                <data>
                  <value>
                    <i4>1</i4>
                  </value>
                </data>
              </array>
            </value>
            <value>
              <array>
                <data>
                  <value>
                    <i4>1</i4>
                  </value>
                </data>
              </array>
            </value>
          </data>
        </array>
      </value>
    </param>
  </params>
</methodResponse>
"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client

            val actual = testSession.removeTorrent("TEST_HASH")

            Assertions.assertThat(actual).isEqualTo(DownpourResult.FAILURE)
        }

        @Test
        fun `500 error throws Fuel Error`() {
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 500
            every { client.executeRequest(any()).responseMessage } returns "Server Error"
            FuelManager.instance.client = client

            Assertions.assertThatThrownBy { testSession.removeTorrent("TEST_HASH") }
                .isInstanceOf(FuelError::class.java)
        }
    }

    @Nested
    inner class Pause {
        @Test
        fun `success result returns SUCCESS enum`() {
            val returnedBody = """["0"]"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedBody.toByteArray()
            FuelManager.instance.client = client

            val actual = testSession.pauseTorrent("TEST_HASH")

            Assertions.assertThat(actual).isEqualTo(DownpourResult.SUCCESS)
        }

        @Test
        fun `failure result returns FAILURE enum`() {
            val returnedJson = """["1"]"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client

            val actual = testSession.pauseTorrent("TEST_HASH")

            Assertions.assertThat(actual).isEqualTo(DownpourResult.FAILURE)
        }

        @Test
        fun `500 error returns FAILURE enum`() {
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 500
            every { client.executeRequest(any()).responseMessage } returns "Server Error"
            FuelManager.instance.client = client

            Assertions.assertThatThrownBy { testSession.pauseTorrent("TEST_HASH") }
                .isInstanceOf(FuelError::class.java)
        }
    }

    @Nested
    inner class Resume {
        @Test
        fun `success result returns SUCCESS enum`() {
            val returnedBody = """["0","0"]"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedBody.toByteArray()
            FuelManager.instance.client = client

            val actual = testSession.resumeTorrent("TEST_HASH")

            Assertions.assertThat(actual).isEqualTo(DownpourResult.SUCCESS)
        }

        @Test
        fun `failure result returns FAILURE enum`() {
            val returnedBody = """["1","1"]"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedBody.toByteArray()
            FuelManager.instance.client = client

            val actual = testSession.resumeTorrent("TEST_HASH")

            Assertions.assertThat(actual).isEqualTo(DownpourResult.FAILURE)
        }

        @Test
        fun `500 error returns FAILURE enum`() {
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 500
            every { client.executeRequest(any()).responseMessage } returns "Server Error"
            FuelManager.instance.client = client

            Assertions.assertThatThrownBy { testSession.getTorrentDetails("TEST_HASH") }
                .isInstanceOf(FuelError::class.java)
        }
    }

    @Nested
    inner class GetSingleTorrent {
        @Test
        fun `success result returns correct Torrent object`() {
            val client = mockk<Client>()
            val returnedXml = """<?xml version="1.0" encoding="UTF-8"?>
<methodResponse>
  <params>
    <param>
      <value>
        <array>
          <data>
            <value>
              <array>
                <data>
                  <value>
                    <string>DOWNLOADED_FILE</string>
                  </value>
                </data>
              </array>
            </value>
            <value>
              <array>
                <data>
                  <value>
                    <i8>0</i8>
                  </value>
                </data>
              </array>
            </value>
            <value>
              <array>
                <data>
                  <value>
                    <i8>0</i8>
                  </value>
                </data>
              </array>
            </value>
            <value>
              <array>
                <data>
                  <value>
                    <i8>0</i8>
                  </value>
                </data>
              </array>
            </value>
            <value>
              <array>
                <data>
                  <value>
                    <i8>0</i8>
                  </value>
                </data>
              </array>
            </value>
            <value>
              <array>
                <data>
                  <value>
                    <i8>2298</i8>
                  </value>
                </data>
              </array>
            </value>
            <value>
              <array>
                <data>
                  <value>
                    <i8>3655647378</i8>
                  </value>
                </data>
              </array>
            </value>
            <value>
              <array>
                <data>
                  <value>
                    <string>seed</string>
                  </value>
                </data>
              </array>
            </value>
            <value>
              <array>
                <data>
                  <value>
                    <i8>0</i8>
                  </value>
                </data>
              </array>
            </value>
            <value>
              <array>
                <data>
                  <value>
                    <i8>0</i8>
                  </value>
                </data>
              </array>
            </value>
            <value>
              <array>
                <data>
                  <value>
                    <i8>1585670375</i8>
                  </value>
                </data>
              </array>
            </value>
            <value>
              <array>
                <data>
                  <value>
                    <i8>3655647378</i8>
                  </value>
                </data>
              </array>
            </value>
            <value>
              <array>
                <data>
                  <value>
                    <i8>8404123939</i8>
                  </value>
                </data>
              </array>
            </value>
            <value>
              <array>
                <data>
                  <value>
                    <string>/path/to/file</string>
                  </value>
                </data>
              </array>
            </value>
          </data>
        </array>
      </value>
    </param>
  </params>
</methodResponse>
"""
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedXml.toByteArray(Charsets.UTF_8)
            FuelManager.instance.client = client

            val expected = Torrent(
                "hash1",
                "DOWNLOADED_FILE",
                0,
                0,
                0,
                0,
                2.298,
                3655647378,
                100.0,
                "seed",
                1585670375.0,
                3655647378,
                8404123939,
                listOf(),
                "/path/to/file"
            )

            val actual = testSession.getTorrentDetails("hash1")

            Assertions.assertThat(actual)
                .isEqualTo(expected)
        }

        @Test
        fun `failure result throws Error`() {
            val client = mockk<Client>()
            val returnedXml = """<?xml version="1.0" encoding="UTF-8"?>
<methodResponse>
  <params>
    <param>
      <value>
        <array>
          <data>
            <value>
              <struct>
                <member>
                  <name>faultCode</name>
                  <value>
                    <i4>-501</i4>
                  </value>
                </member>
                <member>
                  <name>faultString</name>
                  <value>
                    <string>Unsupported target type found.</string>
                  </value>
                </member>
              </struct>
            </value>
            <value>
              <struct>
                <member>
                  <name>faultCode</name>
                  <value>
                    <i4>-501</i4>
                  </value>
                </member>
                <member>
                  <name>faultString</name>
                  <value>
                    <string>Unsupported target type found.</string>
                  </value>
                </member>
              </struct>
            </value>
            <value>
              <array>
                <data>
                  <value>
                    <i8>0</i8>
                  </value>
                </data>
              </array>
            </value>
            <value>
              <struct>
                <member>
                  <name>faultCode</name>
                  <value>
                    <i4>-501</i4>
                  </value>
                </member>
                <member>
                  <name>faultString</name>
                  <value>
                    <string>Unsupported target type found.</string>
                  </value>
                </member>
              </struct>
            </value>
            <value>
              <struct>
                <member>
                  <name>faultCode</name>
                  <value>
                    <i4>-501</i4>
                  </value>
                </member>
                <member>
                  <name>faultString</name>
                  <value>
                    <string>Unsupported target type found.</string>
                  </value>
                </member>
              </struct>
            </value>
            <value>
              <struct>
                <member>
                  <name>faultCode</name>
                  <value>
                    <i4>-501</i4>
                  </value>
                </member>
                <member>
                  <name>faultString</name>
                  <value>
                    <string>Unsupported target type found.</string>
                  </value>
                </member>
              </struct>
            </value>
            <value>
              <struct>
                <member>
                  <name>faultCode</name>
                  <value>
                    <i4>-501</i4>
                  </value>
                </member>
                <member>
                  <name>faultString</name>
                  <value>
                    <string>Unsupported target type found.</string>
                  </value>
                </member>
              </struct>
            </value>
            <value>
              <struct>
                <member>
                  <name>faultCode</name>
                  <value>
                    <i4>-501</i4>
                  </value>
                </member>
                <member>
                  <name>faultString</name>
                  <value>
                    <string>Unsupported target type found.</string>
                  </value>
                </member>
              </struct>
            </value>
            <value>
              <struct>
                <member>
                  <name>faultCode</name>
                  <value>
                    <i4>-501</i4>
                  </value>
                </member>
                <member>
                  <name>faultString</name>
                  <value>
                    <string>Unsupported target type found.</string>
                  </value>
                </member>
              </struct>
            </value>
            <value>
              <struct>
                <member>
                  <name>faultCode</name>
                  <value>
                    <i4>-501</i4>
                  </value>
                </member>
                <member>
                  <name>faultString</name>
                  <value>
                    <string>Unsupported target type found.</string>
                  </value>
                </member>
              </struct>
            </value>
            <value>
              <struct>
                <member>
                  <name>faultCode</name>
                  <value>
                    <i4>-501</i4>
                  </value>
                </member>
                <member>
                  <name>faultString</name>
                  <value>
                    <string>Unsupported target type found.</string>
                  </value>
                </member>
              </struct>
            </value>
            <value>
              <struct>
                <member>
                  <name>faultCode</name>
                  <value>
                    <i4>-501</i4>
                  </value>
                </member>
                <member>
                  <name>faultString</name>
                  <value>
                    <string>Unsupported target type found.</string>
                  </value>
                </member>
              </struct>
            </value>
            <value>
              <struct>
                <member>
                  <name>faultCode</name>
                  <value>
                    <i4>-501</i4>
                  </value>
                </member>
                <member>
                  <name>faultString</name>
                  <value>
                    <string>Unsupported target type found.</string>
                  </value>
                </member>
              </struct>
            </value>
            <value>
              <struct>
                <member>
                  <name>faultCode</name>
                  <value>
                    <i4>-501</i4>
                  </value>
                </member>
                <member>
                  <name>faultString</name>
                  <value>
                    <string>Unsupported target type found.</string>
                  </value>
                </member>
              </struct>
            </value>
          </data>
        </array>
      </value>
    </param>
  </params>
</methodResponse>
"""
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedXml.toByteArray(Charsets.UTF_8)
            FuelManager.instance.client = client

            Assertions.assertThatThrownBy { testSession.getTorrentDetails("TEST_HASH") }
                .isInstanceOf(Error::class.java)
        }

        @Test
        fun `500 error throws FuelError`() {
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 500
            every { client.executeRequest(any()).responseMessage } returns "Server Error"
            FuelManager.instance.client = client

            Assertions.assertThatThrownBy { testSession.getTorrentDetails("TEST_HASH") }
                .isInstanceOf(FuelError::class.java)
        }
    }

    @Nested
    inner class GetFreeSpace {
        @Test
        fun `Success returns correct value`() {
            var i = 0
            val returnValues = listOf(
                """{"t":{"HASH1":["1","0","1","1","DOWNLOAD_ONE","3655647378","1744","1744","3655647378","42224261995","11550","3556004","0","2097152","","47","0","48","0","0","2","1585676761","14478032","0","1744","\/path\/to\/download\/ONE","0","3","1","Tracker: [Timeout was reached]","","3212046032896","0","1", "seed"]}, "cid": 101}""",
                """<?xml version="1.0" encoding="UTF-8"?>
                    <methodResponse>
                      <params>
                        <param>
                          <value>
                            <i8>3212045914112</i8>
                          </value>
                        </param>
                      </params>
                    </methodResponse>"""
            )
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returnsMany returnValues.map { rv -> rv.toByteArray() }
            FuelManager.instance.client = client

            val actual = testSession.getFreeSpace()

            Assertions.assertThat(actual).isEqualTo(3212045914112)
        }

        @Test
        fun `Failure returns -1`() {
            val returnValues = listOf("""{"t":{"HASH1":["1","0","1","1","DOWNLOAD_ONE","3655647378","1744","1744","3655647378","42224261995","11550","3556004","0","2097152","","47","0","48","0","0","2","1585676761","14478032","0","1744","\/path\/to\/download\/ONE","0","3","1","Tracker: [Timeout was reached]","","3212046032896","0","1", "seed"]}, "cid": 101}""",
                """<?xml version="1.0" encoding="UTF-8"?>
<methodResponse>
  <fault>
    <value>
      <struct>
        <member>
          <name>faultCode</name>
          <value>
            <i4>-501</i4>
          </value>
        </member>
        <member>
          <name>faultString</name>
          <value>
            <string>Unsupported target type found.</string>
          </value>
        </member>
      </struct>
    </value>
  </fault>
</methodResponse>"""
            )
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returnsMany returnValues.map { rv -> rv.toByteArray() }
            FuelManager.instance.client = client

            val actual = testSession.getFreeSpace()

            Assertions.assertThat(actual).isEqualTo(-1)
        }

        @Test
        fun `Server Error throws FuelError`() {
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 500
            every { client.executeRequest(any()).responseMessage } returns "Server Error"
            FuelManager.instance.client = client

            Assertions.assertThatThrownBy { testSession.getFreeSpace() }
                .isInstanceOf(FuelError::class.java)
        }
    }

    @Nested
    inner class ForceRecheck {
        @Test
        fun `success result returns SUCCESS enum`() {
            val returnedJson = """<?xml version="1.0" encoding="UTF-8"?>
<methodResponse>
  <params>
    <param>
      <value>
        <i4>0</i4>
      </value>
    </param>
  </params>
</methodResponse>"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client

            val actual = testSession.forceRecheck("TEST_HASH")

            Assertions.assertThat(actual).isEqualTo(DownpourResult.SUCCESS)
        }

        @Test
        fun `failure result returns FAILURE enum`() {
            val returnedJson = """<?xml version="1.0" encoding="UTF-8"?>
<methodResponse>
  <fault>
    <value>
      <struct>
        <member>
          <name>faultCode</name>
          <value>
            <i4>-501</i4>
          </value>
        </member>
        <member>
          <name>faultString</name>
          <value>
            <string>Unsupported target type found.</string>
          </value>
        </member>
      </struct>
    </value>
  </fault>
</methodResponse>
"""
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 200
            every { client.executeRequest(any()).responseMessage } returns "OK"
            every { client.executeRequest(any()).data } returns returnedJson.toByteArray()
            FuelManager.instance.client = client

            val actual = testSession.forceRecheck("TEST_HASH")

            Assertions.assertThat(actual).isEqualTo(DownpourResult.FAILURE)
        }

        @Test
        fun `500 error returns FAILURE enum`() {
            val client = mockk<Client>()
            every { client.executeRequest(any()).statusCode } returns 500
            every { client.executeRequest(any()).responseMessage } returns "Server Error"
            FuelManager.instance.client = client

            Assertions.assertThatThrownBy { testSession.forceRecheck("TEST_HASH") }
                .isInstanceOf(FuelError::class.java)
        }
    }
}