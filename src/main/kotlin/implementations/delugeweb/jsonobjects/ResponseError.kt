package implementations.delugeweb.jsonobjects

import kotlinx.serialization.Serializable

@Serializable
data class ResponseError(
    val message: String,
    val code: Int
)