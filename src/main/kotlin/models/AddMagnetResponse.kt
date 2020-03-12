package models

import kotlinx.serialization.Serializable

@Serializable
data class AddMagnetResponse(
    val id: Int,
    val result: String?,
    val error: ResponseError?
)