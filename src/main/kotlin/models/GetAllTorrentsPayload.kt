package models

import kotlinx.serialization.*
import kotlinx.serialization.internal.HexConverter
import kotlinx.serialization.internal.StringDescriptor

@Serializable
data class GetAllTorrentsPayload(
    val id: Int,
    val method: String,
    val params: List<@ContextualSerialization Any>
) {

    override fun toString(): String {
        val paramsString = "[[${(params[0] as List<*>).joinToString(",") { x -> "\"${x}\"" }}], {}]"
        return "{\"id\": ${this.id}, \"method\": \"${this.method}\", \"params\": ${paramsString}}"
    }

    companion object {
        fun defaultPayload(): GetAllTorrentsPayload {
           return GetAllTorrentsPayload(
               1,
               "web.update_ui",
               listOf(
                   listOf(
                       "queue",
                       "name",
                       "total_wanted",
                       "state",
                       "progress",
                       "num_seeds",
                       "total_seeds",
                       "num_peers",
                       "total_peers",
                       "download_payload_rate",
                       "upload_payload_rate",
                       "eta",
                       "ratio",
                       "distributed_copies",
                       "is_auto_managed",
                       "time_added",
                       "tracker_host",
                       "save_path",
                       "total_done",
                       "total_uploaded",
                       "max_download_speed",
                       "max_upload_speed",
                       "seeds_peers_ratio"
                   ),
                   hashMapOf<String, String>()
               )
           )
        }
    }
}