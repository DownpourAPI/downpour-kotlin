package com.hnrhn.downpour.impl.deluge.jsonobjects

import kotlinx.serialization.Serializable

@Serializable
data class TorrentDetails(
    val maxDownloadSpeed: Int,
    val uploadPayloadRate: Int,
    val downloadPayloadRate: Int,
    val numberOfPeers: Int,
    val ratio: Int,
    val totalPeers: Int,
    val maxUploadSpeed: Int,
    val state: String,
    val distributedCopies: Float,
    val savePath: String,
    val progress: Float,
    val timeAdded: Float,
    val trackerHost: String,
    val totalUploaded: Int,
    val totalDone: Int,
    val totalWanted: Int,
    val totalSeeds: Int,
    val seedsPeersRatio: Float,
    val numberOfSeeds: Int,
    val name: String,
    val isAutoManaged: Boolean,
    val queue: Int,
    val eta: Int
)