package implementations.delugeweb.jsonobjects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TorrentOptions(
    @SerialName("file_priorities")
    val filePriorities: List<Int> = listOf(1),

    @SerialName("add_paused")
    val addPaused: Boolean = false,

    @SerialName("compact_allocation")
    val compactAllocation: Boolean = false,

    @SerialName("download_location")
    val downloadLocation: String = "",

    @SerialName("move_on_completion")
    val moveOnCompletion: Boolean = false,

    @SerialName("move_to_location")
    val moveToLocation: String? = null,

    @SerialName("max_connections")
    val maxConnections: Int = -1,

    @SerialName("max_download_speed")
    val maxDownloadSpeed: Int = -1,

    @SerialName("max_upload_slots")
    val maxUploadSlots: Int = -1,

    @SerialName("max_upload_speed")
    val maxUploadSpeed: Int = -1,

    @SerialName("prioritize_first_last_pieces")
    val prioritizeFirstLastPieces: Boolean = false
)