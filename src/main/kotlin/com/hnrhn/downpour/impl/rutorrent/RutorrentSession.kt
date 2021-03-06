/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package com.hnrhn.downpour.impl.rutorrent

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.FuelManager
import com.hnrhn.downpour.common.AddMagnetResult
import com.hnrhn.downpour.common.AddTorrentFileResult
import com.hnrhn.downpour.common.DownpourResult
import com.hnrhn.downpour.common.Torrent
import com.hnrhn.downpour.impl.rutorrent.jsonobjects.GetAllTorrentsResponse
import com.hnrhn.downpour.interfaces.RemoteTorrentController
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.File
import java.util.*

@UnstableDefault
class RutorrentSession(basePath: String, user: String, password: String) : RemoteTorrentController {
    private var authHeader: String = Base64.getEncoder().encodeToString("$user:$password".toByteArray())

    private val json = Json(JsonConfiguration(ignoreUnknownKeys = true))

    init {
        FuelManager.instance.basePath = basePath
        FuelManager.instance.baseHeaders = hashMapOf("Authorization" to "Basic $authHeader")
    }

    override fun getTorrentDetails(torrentHash: String): Torrent? {
        val result = Fuel.post("/plugins/httprpc/action.php")
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

            val resultValues = results.map { res -> res.groupValues[1] }.toList()

            if (resultValues.filter { r -> r == "Unsupported target type found." }.any()) {
                throw Error("Unsupported target type found. Is your info-hash correct?")
            }

            return Torrent(
                torrentHash,
                resultValues[0],
                resultValues[1].toLong(),
                resultValues[2].toLong(),
                resultValues[3].toInt(),
                resultValues[4].toInt(),
                resultValues[5].toDouble() / 1000,
                resultValues[6].toLong(),
                (resultValues[11].toDouble() / resultValues[6].toDouble()) * 100,
                resultValues[7],
                resultValues[10].toDouble(),
                resultValues[11].toLong(),
                resultValues[12].toLong(),
                listOf(),
                resultValues[13]
            )
        }

        return null
    }

    override fun getAllTorrents(): List<Torrent> {
        val result = Fuel.post("/plugins/httprpc/action.php", listOf("mode" to "list", "cmd" to "d.connection_current="))
            .header("Authorization" to "Basic $authHeader")
            .response()
            .third

        val (responseBody, error) = result

        if (error != null) {
            throw error
        }

        return if (responseBody != null) {
            try {
                val response = json.parse(GetAllTorrentsResponse.serializer(), responseBody.toString(Charsets.UTF_8))
                response.toTorrents()
            } catch (error: Exception) {
                throw Error("Bad Request")
            }
        } else {
            throw Error()
        }
    }

    override fun removeTorrent(torrentHash: String, withData: Boolean): DownpourResult {
        val withDataString = """
            <value>
                <struct>
                    <member>
                        <name>methodName</name>
                        <value>
                            <string>d.custom5.set</string>
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
                                    <value>
                                        <string>1</string>
                                    </value>
                                </data>
                            </array>
                        </value>
                    </member>
                </struct>
            </value>
        """.trimIndent()

        val bodyString = """
            <?xml version="1.0" encoding="UTF-8"?>
            <methodCall>
                <methodName>system.multicall</methodName>
                <params>
                    <param>
                        <value>
                            <array>
                                <data>
                                    ${if (withData) withDataString else ""}
                                    <value>
                                        <struct>
                                            <member>
                                                <name>methodName</name>
                                                <value>
                                                    <string>d.delete_tied</string>
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
                                                    <string>d.erase</string>
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
        """.trim()

        val result = Fuel.post("/plugins/httprpc/action.php")
            .header("Authorization" to "Basic $authHeader")
            .body(bodyString)
            .response()

        val (responseBody, error) = result.third

        if (error != null) {
            throw error
        }

        if (responseBody != null) {
            val responseString = responseBody.toString(Charsets.UTF_8)
            val re = Regex("<(?:i4|string)>(.*?)<")
            val responseValues = re.findAll(responseString).map { match -> match.groupValues[1] }.toList()
            return if (responseValues[0] == "1" && responseValues[1] == "0" && responseValues[1] == "0") {
                DownpourResult.SUCCESS
            } else {
                DownpourResult.FAILURE
            }
        }

        return DownpourResult.FAILURE
    }

    override fun pauseTorrent(torrentHash: String): DownpourResult {
        val result = Fuel.post("/plugins/httprpc/action.php", listOf("mode" to "pause", "hash" to torrentHash))
            .header("Authorization" to "Basic $authHeader")
            .response()
            .third

        val (responseBody, error) = result

        if (error != null) {
            throw error
        }

        if (responseBody != null) {
            val responseString = responseBody.toString(Charsets.UTF_8)
            return if (responseString == """["0"]""") {
                DownpourResult.SUCCESS
            } else {
                DownpourResult.FAILURE
            }
        }

        return DownpourResult.FAILURE
    }

    override fun resumeTorrent(torrentHash: String): DownpourResult {
        val result = Fuel.post("/plugins/httprpc/action.php", listOf("mode" to "start", "hash" to torrentHash))
            .header("Authorization" to "Basic $authHeader")
            .response()
            .third

        val (responseBody, error) = result

        if (error != null) {
            throw error
        }

        if (responseBody != null) {
            val responseString = responseBody.toString(Charsets.UTF_8)
            return if (responseString == """["0","0"]""") {
                DownpourResult.SUCCESS
            } else {
                DownpourResult.FAILURE
            }
        }

        return DownpourResult.FAILURE
    }

    override fun addMagnet(magnetLink: String): AddMagnetResult {
        val result = Fuel.post("/php/addtorrent.php", listOf("url" to magnetLink))
            .header("Authorization" to "Basic $authHeader")
            .response()
            .third

        val (responseBody, error) = result

        if (error != null) {
            throw error
        }

        if (responseBody != null) {
            val responseString = responseBody.toString(Charsets.UTF_8)
            return if (responseString.endsWith("success\");")) {
                val hash = Regex("btih:(.*?)&").find(magnetLink)!!.groupValues[1].toUpperCase()

                AddMagnetResult.success(hash)
            } else {
                AddMagnetResult.failure()
            }
        }


        return AddMagnetResult.failure()
    }

    override fun addTorrentFile(torrentFile: File): AddTorrentFileResult {
        val result = Fuel
            .upload("/php/addtorrent.php")
            .add(FileDataPart(torrentFile, name = "torrent_file[]", filename = torrentFile.name))
            .response()
            .third

        val (responseBody, error) = result

        if (error != null) {
            throw error
        }

        if (responseBody != null) {
            val responseString = responseBody.toString(Charsets.UTF_8)
            return if (responseString.endsWith("success\");")) {
                AddTorrentFileResult.success("")
            } else {
                AddTorrentFileResult.failure()
            }
        }

        return AddTorrentFileResult.failure()
    }

    override fun forceRecheck(torrentHash: String): DownpourResult {
        val result = Fuel.post("/plugins/httprpc/action.php")
            .body("""
                <?xml version='1.0'?>
                <methodCall>
                	<methodName>d.check_hash</methodName>
                	<params>
                		<param>
                			<value>
                				<string>$torrentHash</string>
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

        if (responseBody != null) {
            val actionResult = Regex("<i4>(.*?)<").find(responseBody.toString(Charsets.UTF_8))!!.groupValues[1]
            return if (actionResult == "0") {
                DownpourResult.SUCCESS
            } else {
                DownpourResult.FAILURE
            }
        }

        return DownpourResult.FAILURE
    }

    override fun getFreeSpace(): Long {
        val allTorrents = getAllTorrents()

        if (allTorrents.isEmpty()) {
            return -1
        }

        val hash = allTorrents.first().hash

        val result = Fuel.post("/plugins/httprpc/action.php")
            .body("""
                <?xml version='1.0'?>
                <methodCall>
                	<methodName>d.free_diskspace</methodName>
                	<params>
                		<param>
                			<value>
                				<string>$hash</string>
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

        if (responseBody != null) {
            return Regex("<i8>(.*?)<")
                .find(responseBody.toString(Charsets.UTF_8))
                ?.groupValues
                ?.get(1)
                ?.toLong()
                ?: return -1
        }

        return -1
    }
}