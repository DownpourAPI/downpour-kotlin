package com.hnrhn.downpour.impl.rutorrent

import com.github.kittinunf.fuel.Fuel
import com.hnrhn.downpour.common.*
import com.hnrhn.downpour.impl.rutorrent.jsonobjects.GetAllTorrentsResponse
import com.hnrhn.downpour.interfaces.RemoteTorrentController
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.File
import java.lang.Exception
import java.util.*

@UnstableDefault
class RutorrentSession(private var endpoint: String, user: String, password: String) : RemoteTorrentController {
    private var authHeader: String = Base64.getEncoder().encodeToString("$user:$password".toByteArray())

    private val json = Json(JsonConfiguration(ignoreUnknownKeys = true))

    override fun getTorrentDetails(torrentHash: String): Torrent? {
        val result = Fuel.post(endpoint)
            .header("Authorization" to "Basic $authHeader")
            .body("""
                <?xml version='1.0'?>
                <methodCall>
                	<methodName>system.multicall</methodName>
                	<params>
                		<param>
                			<value>
                			<array>
                				<data>
                					<value>
                						<struct>
                							<member>
                								<name>methodName</name>
                								<value>
                									<string>d.name</string>
                								</value>
                							</member>
                							<member>
                								<name>params</name>
                								<value>
                									<array>
                										<data>
                											<value>
                												<string>$torrentHash</string>
                											</value>
                										</data>
                									</array>
                								</value>
                							</member>
                						</struct>
                					</value>
                					<value>
                						<struct>
                							<member>
                								<name>methodName</name>
                								<value>
                									<string>d.up.rate</string>
                								</value>
                							</member>
                							<member>
                								<name>params</name>
                								<value>
                									<array>
                										<data>
                											<value>
                												<string>$torrentHash</string>
                											</value>
                										</data>
                									</array>
                								</value>
                							</member>
                						</struct>
                					</value>
                					<value>
                						<struct>
                							<member>
                								<name>methodName</name>
                								<value>
                									<string>d.down.rate</string>
                								</value>
                							</member>
                							<member>
                								<name>params</name>
                								<value>
                									<array>
                										<data>
                											<value>
                												<string>$torrentHash</string>
                											</value>
                										</data>
                									</array>
                								</value>
                							</member>
                						</struct>
                					</value>
                					<value>
                						<struct>
                							<member>
                								<name>methodName</name>
                								<value>
                									<string>d.get_peers_complete</string> <!-- Seeders -->
                								</value>
                							</member>
                							<member>
                								<name>params</name>
                								<value>
                									<array>
                										<data>
                											<value>
                												<string>$torrentHash</string>
                											</value>
                										</data>
                									</array>
                								</value>
                							</member>
                						</struct>
                					</value>
                					<value>
                						<struct>
                							<member>
                								<name>methodName</name>
                								<value>
                									<string>d.get_peers_accounted</string> <!-- Peers -->
                								</value>
                							</member>
                							<member>
                								<name>params</name>
                								<value>
                									<array>
                										<data>
                											<value>
                												<string>$torrentHash</string>
                											</value>
                										</data>
                									</array>
                								</value>
                							</member>
                						</struct>
                					</value>
                					<value>
                						<struct>
                							<member>
                								<name>methodName</name>
                								<value>
                									<string>d.ratio</string>
                								</value>
                							</member>
                							<member>
                								<name>params</name>
                								<value>
                									<array>
                										<data>
                											<value>
                												<string>$torrentHash</string>
                											</value>
                										</data>
                									</array>
                								</value>
                							</member>
                						</struct>
                					</value>
                					<value>
                						<struct>
                							<member>
                								<name>methodName</name>
                								<value>
                									<string>d.size_bytes</string>
                								</value>
                							</member>
                							<member>
                								<name>params</name>
                								<value>
                									<array>
                										<data>
                											<value>
                												<string>$torrentHash</string>
                											</value>
                										</data>
                									</array>
                								</value>
                							</member>
                						</struct>
                					</value>
                					<value>
                						<struct>
                							<member>
                								<name>methodName</name>
                								<value>
                									<string>d.connection_current</string>
                								</value>
                							</member>
                							<member>
                								<name>params</name>
                								<value>
                									<array>
                										<data>
                											<value>
                												<string>$torrentHash</string>
                											</value>
                										</data>
                									</array>
                								</value>
                							</member>
                						</struct>
                					</value>
                					<value>
                						<struct>
                							<member>
                								<name>methodName</name>
                								<value>
                									<string>d.state</string>
                								</value>
                							</member>
                							<member>
                								<name>params</name>
                								<value>
                									<array>
                										<data>
                											<value>
                												<string>$torrentHash</string>
                											</value>
                										</data>
                									</array>
                								</value>
                							</member>
                						</struct>
                					</value>
                					<value>
                						<struct>
                							<member>
                								<name>methodName</name>
                								<value>
                									<string>d.is_active</string>
                								</value>
                							</member>
                							<member>
                								<name>params</name>
                								<value>
                									<array>
                										<data>
                											<value>
                												<string>$torrentHash</string>
                											</value>
                										</data>
                									</array>
                								</value>
                							</member>
                						</struct>
                					</value>
                					<value>
                						<struct>
                							<member>
                								<name>methodName</name>
                								<value>
                									<string>d.timestamp.started</string>
                								</value>
                							</member>
                							<member>
                								<name>params</name>
                								<value>
                									<array>
                										<data>
                											<value>
                												<string>$torrentHash</string>
                											</value>
                										</data>
                									</array>
                								</value>
                							</member>
                						</struct>
                					</value>
                					<value>
                						<struct>
                							<member>
                								<name>methodName</name>
                								<value>
                									<string>d.completed_bytes</string>
                								</value>
                							</member>
                							<member>
                								<name>params</name>
                								<value>
                									<array>
                										<data>
                											<value>
                												<string>$torrentHash</string>
                											</value>
                										</data>
                									</array>
                								</value>
                							</member>
                						</struct>
                					</value>
                					<value>
                						<struct>
                							<member>
                								<name>methodName</name>
                								<value>
                									<string>d.up.total</string>
                								</value>
                							</member>
                							<member>
                								<name>params</name>
                								<value>
                									<array>
                										<data>
                											<value>
                												<string>$torrentHash</string>
                											</value>
                										</data>
                									</array>
                								</value>
                							</member>
                						</struct>
                					</value>
                					<value>
                						<struct>
                							<member>
                								<name>methodName</name>
                								<value>
                									<string>d.base_path</string>
                								</value>
                							</member>
                							<member>
                								<name>params</name>
                								<value>
                									<array>
                										<data>
                											<value>
                												<string>$torrentHash</string>
                											</value>
                										</data>
                									</array>
                								</value>
                							</member>
                						</struct>
                					</value>
                				</data>
                			</array>
                			</value>
                		</param>
                	</params>
                </methodCall>
            """.trimIndent())
            .response()
            .third

        val (responseBody, error) = result

        if (error != null) {
            throw error
        }

        if (responseBody != null)
        {
            val responseString = responseBody.toString(Charsets.UTF_8)
            val re = Regex("<(?:string|i8)>(.*?)<")

            val results = re.findAll(responseString)

            val reses = results.map { res -> res.groupValues[1] }.toList()

            return Torrent(
                torrentHash,
                reses[0],
                reses[1].toLong(),
                reses[2].toLong(),
                reses[3].toInt(),
                reses[4].toInt(),
                reses[5].toDouble() / 1000,
                reses[6].toLong(),
                (reses[11].toDouble() / reses[6].toDouble()) * 100,
                reses[7],
                reses[10].toDouble(),
                reses[11].toLong(),
                reses[12].toLong(),
                listOf(),
                reses[13]
            )
        }

        return null
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