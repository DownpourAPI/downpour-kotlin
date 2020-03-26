package com.hnrhn.downpour.impl.deluge.jsonobjects

class GetTorrentDetailsPayload(private val torrentHash: String) {
    override fun toString(): String {
        return """{"id":1,"method":"core.get_torrent_status","params":["$torrentHash", []]}"""
    }
}