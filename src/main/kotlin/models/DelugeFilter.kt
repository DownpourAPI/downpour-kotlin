package models

import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DelugeFilter(
    val state: List<List<@ContextualSerialization Any>>,

    @SerialName("tracker_host")
    val trackerHost: List<List<@ContextualSerialization Any>>
)