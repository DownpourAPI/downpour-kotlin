package models

import kotlinx.serialization.Serializable

@Serializable
data class MagnetUpload(
    val path: String,
    val options: MagnetOptions
)