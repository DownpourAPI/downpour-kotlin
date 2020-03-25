package com.hnrhn.downpour.impl.deluge.jsonobjects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DelugeStats(
    @SerialName("upload_protocol_rate")
    val uploadProtocolRate: Long,

    @SerialName("max_upload")
    val maxUpload: Double,

    @SerialName("download_protocol_rate")
    val downloadProtocolRate: Long,

    @SerialName("download_rate")
    val downloadRate: Long,

    @SerialName("has_incoming_connections")
    val hasIncomingConnections: Boolean,

    @SerialName("num_connections")
    val numConnections: Long,

    @SerialName("max_download")
    val maxDownload: Double,

    @SerialName("upload_rate")
    val uploadRate: Long,

    @SerialName("dht_nodes")
    val dhtNodes: Long,

    @SerialName("free_space")
    val freeSpace: Long,

    @SerialName("max_num_connections")
    val maxNumConnections: Long
)