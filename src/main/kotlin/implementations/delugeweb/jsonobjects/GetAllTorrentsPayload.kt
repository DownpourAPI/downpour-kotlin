package models


data class GetAllTorrentsPayload(
    val id: Int = 1
) {
    override fun toString(): String {
        return """{"id": ${this.id},"method":"core.get_torrents_status","params":[{}, []]}"""
    }
}