package models

import kotlinx.serialization.Serializable

@Serializable
data class DelugeResponse(
    val id: Int,
    val result: Boolean?,
    val error: ResponseError?
)