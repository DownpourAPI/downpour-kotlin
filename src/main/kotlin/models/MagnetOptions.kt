package models

import kotlinx.serialization.Serializable

@Serializable
data class MagnetOptions(
    val filePriorities: List<Int> = listOf(1),
    val addPaused: Boolean = false,
    val compactAllocation: Boolean = false,
    val downloadLocation: String = "/home32/reteph/downloads/",
    val moveOnCompletion: Boolean = false,
    val moveToLocation: String? = null,
    val maxConnections: Int = -1,
    val maxDownloadSpeed: Int = -1,
    val maxUploadSlots: Int = -1,
    val maxUploadSpeed: Int = -1,
    val prioritizeFirstLastPieces: Boolean = false
)