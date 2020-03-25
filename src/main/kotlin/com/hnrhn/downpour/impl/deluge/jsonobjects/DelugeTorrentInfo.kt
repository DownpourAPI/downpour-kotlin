package com.hnrhn.downpour.impl.deluge.jsonobjects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DelugeTorrentInfo(
    @SerialName("max_download_speed")
    val maxDownloadSpeed: Double,

    @SerialName("upload_payload_rate")
    val uploadPayloadRate: Long,

    @SerialName("download_payload_rate")
    val downloadPayloadRate: Long,

    @SerialName("num_peers")
    val numPeers: Long,

    val ratio: Double,

    @SerialName("total_peers")
    val totalPeers: Long,

    @SerialName("max_upload_speed")
    val maxUploadSpeed: Double,

    val state: String,

    @SerialName("distributed_copies")
    val distributedCopies: Double,

    @SerialName("save_path")
    val savePath: String,

    val progress: Double,

    @SerialName("time_added")
    val timeAdded: Double,

    @SerialName("tracker_host")
    val trackerHost: String,

    @SerialName("total_uploaded")
    val totalUploaded: Long,

    @SerialName("total_done")
    val totalDone: Long,

    @SerialName("total_wanted")
    val totalWanted: Long,

    @SerialName("total_seeds")
    val totalSeeds: Long,

    @SerialName("seeds_peers_ratio")
    val seedsPeerRatio: Double,

    @SerialName("num_seeds")
    val numSeeds: Long,

    val name: String,

    @SerialName("is_auto_managed")
    val isAutoManaged: Boolean,

    val queue: Long,

    val eta: Long
)