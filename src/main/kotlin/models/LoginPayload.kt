package models

import kotlinx.serialization.Serializable

@Serializable
data class LoginPayload(
    val params: List<String>,
    val id: Int,
    val method: String = "auth.login"
)