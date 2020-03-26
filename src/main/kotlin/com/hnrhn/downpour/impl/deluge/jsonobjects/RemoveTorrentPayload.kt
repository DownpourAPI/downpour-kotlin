package com.hnrhn.downpour.impl.deluge.jsonobjects

class RemoveTorrentPayload(private val torrentHash: String, private val withData: Boolean) {
    override fun toString(): String {
        return """{"id":1,"method":"core.remove_torrent","params":["$torrentHash",${withData.toString().toLowerCase()}]}"""
    }
}