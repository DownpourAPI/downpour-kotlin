package models

data class GetTorrentDetailsPayload(
    val id: Int = 1,
    val method: String = "core.get_torrent_status",
    val params: List<Any>
) {
    companion object {
        fun defaultPayload(torrentHash: String): GetTorrentDetailsPayload {
            return GetTorrentDetailsPayload(params = listOf(torrentHash, listOf<String>()))
        }
    }

    override fun toString(): String {
        return """{"id":${this.id},"method":"${this.method}","params":["${this.params[0]}", []]}"""
    }
}