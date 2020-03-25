package com.hnrhn.downpour.impl.deluge.jsonobjects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Torrent(
    val comment: String,

    @SerialName("active_time")
    val activeTime: Int,

    @SerialName("is_seed")
    val isSeed: Boolean,

    val hash: String,

    @SerialName("upload_payload_rate")
    val uploadPayloadRate: Int,

    @SerialName("move_completed_path")
    val moveCompletedPath: String,

    val private: Boolean,

    @SerialName("total_payload_upload")
    val totalPayloadUpload: Long,

    val paused: Boolean,

    @SerialName("seed_rank")
    val seedRank: Int,

    @SerialName("seeding_time")
    val seedingTime: Int,

    @SerialName("max_upload_slots")
    val maxUploadSlots: Int,

    @SerialName("prioritize_first_last")
    val prioritizeFirstAndLastChunks: Boolean,

    @SerialName("distributed_copies")
    val distributedCopies: Double,

    @SerialName("download_payload_rate")
    val downloadPayloadRate: Int,

    val message: String,

    @SerialName("num_peers")
    val numberOfPeers: Int,

    @SerialName("max_download_speed")
    val maxDownloadSpeed: Double,

    @SerialName("max_connections")
    val maxConnections: Int,

    val compact: Boolean,

    val ratio: Double,

    @SerialName("total_peers")
    val totalPeers: Int,

    @SerialName("total_size")
    val totalSize: Long,

    @SerialName("total_wanted")
    val totalWanted: Long,

    val state: String,

    @SerialName("file_priorities")
    val filePriorities: List<Int>,

    @SerialName("max_upload_speed")
    val maxUploadSpeed: Double,

    @SerialName("remove_at_ratio")
    val willRemoveAtRatio: Boolean,

    val tracker: String,

    @SerialName("save_path")
    val savePath: String,

    val progress: Double,

    @SerialName("time_added")
    val timeAdded: Double,

    @SerialName("tracker_host")
    val trackerHost: String,

    @SerialName("total_uploaded")
    val totalUploaded: Long,

    val files: List<FileInTorrent>,

    @SerialName("total_done")
    val totalDone: Long,

    @SerialName("num_pieces")
    val numberOfPieces: Int,

    @SerialName("tracker_status")
    val trackerStatus: String,

    @SerialName("total_seeds")
    val totalSeeds: Int,

    @SerialName("move_on_completed")
    val willMoveOnCompletion: Boolean,

    @SerialName("next_announce")
    val nextAnnounce: Int,

    @SerialName("stop_at_ratio")
    val willStopAtRatio: Boolean,

    @SerialName("file_progress")
    val fileProgress: List<Double>,

    @SerialName("move_completed")
    val moveOnCompletion: Boolean,

    @SerialName("piece_length")
    val pieceLength: Int,

    @SerialName("all_time_download")
    val allTimeDownload: Long,

    @SerialName("move_on_completed_path")
    val moveOnCompletedPath: String,

    @SerialName("num_seeds")
    val numberOfSeeds: Int,

    val peers: List<String>,

    val name: String,

    val trackers: List<Tracker>,

    @SerialName("total_payload_download")
    val totalPayloadDownload: Long,

    @SerialName("is_auto_managed")
    val isAutoManaged: Boolean,

    @SerialName("seeds_peers_ratio")
    val seedsToPeersRatio: Double,

    val queue: Int,

    @SerialName("num_files")
    val numberOfFiles: Int,

    val eta: Int,

    @SerialName("stop_ratio")
    val stopAtRatioValue: Double,

    @SerialName("is_finished")
    val isFinished: Boolean
)